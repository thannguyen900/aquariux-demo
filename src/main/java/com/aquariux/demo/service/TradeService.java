package com.aquariux.demo.service;

import com.aquariux.demo.config.AppProperties;
import com.aquariux.demo.dto.request.TradeRequest;
import com.aquariux.demo.dto.response.TradeExecutionResult;
import com.aquariux.demo.dto.response.TradeResponse;
import com.aquariux.demo.entity.AggregatedPriceEntity;
import com.aquariux.demo.entity.TradeEntity;
import com.aquariux.demo.entity.TradeOrderEntity;
import com.aquariux.demo.entity.WalletEntity;
import com.aquariux.demo.entity.WalletTransactionEntity;
import com.aquariux.demo.enums.OrderStatus;
import com.aquariux.demo.enums.TradeSide;
import com.aquariux.demo.enums.TradingPair;
import com.aquariux.demo.exception.BusinessException;
import com.aquariux.demo.repository.TradeOrderRepository;
import com.aquariux.demo.repository.TradeRepository;
import com.aquariux.demo.repository.WalletRepository;
import com.aquariux.demo.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final WalletRepository walletRepository;
    private final TradeRepository tradeRepository;
    private final TradeOrderRepository tradeOrderRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final MarketPriceService marketPriceService;
    private final AppProperties appProperties;
    private final RoundingPolicyService roundingPolicyService;
    private final FeePolicyService feePolicyService;
    private final IdempotencyService idempotencyService;

    @Transactional
    public TradeResponse executeTrade(TradeRequest request, String idempotencyKey) {
        Long userId = appProperties.getDemoUserId();
        IdempotencyService.IdempotencyDecision decision = idempotencyService.begin(userId, idempotencyKey, request);
        if (decision.replay()) {
            IdempotencyService.ReplayTradeResponse replay = decision.replayResponse();
            WalletEntity quoteWallet = getWallet(userId, replay.order().getQuoteAsset());
            WalletEntity baseWallet = getWallet(userId, replay.order().getBaseAsset());
            return TradeResponse.from(replay.order(), replay.trade(), quoteWallet, baseWallet);
        }

        TradeOrderEntity order = null;
        try {
            TradingPair pair = TradingPair.from(request.getPair());
            TradeSide side = TradeSide.valueOf(request.getSide().toUpperCase());
            BigDecimal quantity = roundingPolicyService.normalizeQuantity(request.getQuantity());

            if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("Quantity must be greater than zero");
            }

            AggregatedPriceEntity latestPrice = marketPriceService.getLatestAggregatedPrice(pair.name());
            order = createOrder(userId, pair, side, quantity, latestPrice);
            idempotencyService.markProcessing(decision.record(), order.getId());
            order = moveOrderToProcessing(order);

            TradeExecutionResult result = switch (side) {
                case BUY -> executeBuy(order, latestPrice);
                case SELL -> executeSell(order, latestPrice);
            };

            idempotencyService.markCompleted(decision.record(), result.getOrder().getId(), result.getTrade().getId());
            return TradeResponse.from(result.getOrder(), result.getTrade(), result.getQuoteWallet(), result.getBaseWallet());
        } catch (RuntimeException ex) {
            String reason = ex.getMessage() != null ? ex.getMessage() : "Trade request failed";
            if (order != null) {
                failOrder(order, reason);
                idempotencyService.markFailed(decision.record(), order.getId(), reason);
            } else {
                idempotencyService.markFailed(decision.record(), null, reason);
            }
            throw ex;
        }
    }

    private TradeOrderEntity createOrder(Long userId,
                                         TradingPair pair,
                                         TradeSide side,
                                         BigDecimal quantity,
                                         AggregatedPriceEntity latestPrice) {
        LocalDateTime now = LocalDateTime.now();
        return tradeOrderRepository.save(TradeOrderEntity.builder()
                .clientOrderId(UUID.randomUUID().toString())
                .userId(userId)
                .pairSymbol(pair.name())
                .side(side.name())
                .baseAsset(pair.getBaseAsset())
                .quoteAsset(pair.getQuoteAsset())
                .quantity(quantity)
                .priceScale(roundingPolicyService.priceScale())
                .quantityScale(roundingPolicyService.quantityScale())
                .quoteScale(roundingPolicyService.quoteScale())
                .feeScale(roundingPolicyService.feeScale())
                .requestedAt(now)
                .latestPriceId(latestPrice.getId())
                .status(OrderStatus.CREATED.name())
                .createdAt(now)
                .updatedAt(now)
                .build());
    }

    private TradeOrderEntity moveOrderToProcessing(TradeOrderEntity order) {
        order.setStatus(OrderStatus.PROCESSING.name());
        order.setUpdatedAt(LocalDateTime.now());
        return tradeOrderRepository.save(order);
    }

    private TradeExecutionResult executeBuy(TradeOrderEntity order,
                                            AggregatedPriceEntity latestPrice) {
        BigDecimal price = roundingPolicyService.normalizePrice(latestPrice.getBestAskPrice());
        BigDecimal grossQuoteAmount = roundingPolicyService.normalizeQuote(price.multiply(order.getQuantity()));
        BigDecimal feeAmount = feePolicyService.calculateFee(grossQuoteAmount);
        BigDecimal totalDebit = roundingPolicyService.normalizeQuote(grossQuoteAmount.add(feeAmount));

        WalletEntity quoteWallet = walletRepository.findByUserIdAndAssetForUpdate(order.getUserId(), order.getQuoteAsset())
                .orElseThrow(() -> new BusinessException(order.getQuoteAsset() + " wallet not found"));
        WalletEntity baseWallet = walletRepository.findByUserIdAndAssetForUpdate(order.getUserId(), order.getBaseAsset())
                .orElseThrow(() -> new BusinessException(order.getBaseAsset() + " wallet not found"));

        if (quoteWallet.getBalance().compareTo(totalDebit) < 0) {
            throw new BusinessException("Insufficient " + order.getQuoteAsset() + " balance including fee");
        }

        BigDecimal quoteBefore = quoteWallet.getBalance();
        BigDecimal baseBefore = baseWallet.getBalance();

        quoteWallet.setBalance(roundingPolicyService.normalizeQuote(quoteWallet.getBalance().subtract(totalDebit)));
        baseWallet.setBalance(roundingPolicyService.normalizeQuantity(baseWallet.getBalance().add(order.getQuantity())));
        quoteWallet.setUpdatedAt(LocalDateTime.now());
        baseWallet.setUpdatedAt(LocalDateTime.now());

        walletRepository.save(quoteWallet);
        walletRepository.save(baseWallet);

        TradeOrderEntity executedOrder = completeOrder(order, latestPrice.getBestAskSource(), price, grossQuoteAmount, feeAmount, totalDebit);
        TradeEntity trade = tradeRepository.save(TradeEntity.builder()
                .orderId(executedOrder.getId())
                .clientOrderId(executedOrder.getClientOrderId())
                .userId(executedOrder.getUserId())
                .pairSymbol(executedOrder.getPairSymbol())
                .side(executedOrder.getSide())
                .baseAsset(executedOrder.getBaseAsset())
                .quoteAsset(executedOrder.getQuoteAsset())
                .quantity(executedOrder.getQuantity())
                .executedPrice(price)
                .grossQuoteAmount(grossQuoteAmount)
                .feeRate(feePolicyService.feeRate())
                .feeAmount(feeAmount)
                .feeAsset(feePolicyService.feeAsset())
                .netQuoteAmount(totalDebit)
                .priceSource(latestPrice.getBestAskSource())
                .orderStatus(executedOrder.getStatus())
                .executedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build());

        executedOrder.setTradeId(trade.getId());
        executedOrder.setUpdatedAt(LocalDateTime.now());
        tradeOrderRepository.save(executedOrder);

        saveWalletAudit(executedOrder.getUserId(), executedOrder.getQuoteAsset(), totalDebit.negate(), quoteBefore, quoteWallet.getBalance(), trade.getId());
        saveWalletAudit(executedOrder.getUserId(), executedOrder.getBaseAsset(), executedOrder.getQuantity(), baseBefore, baseWallet.getBalance(), trade.getId());

        return TradeExecutionResult.builder()
                .order(executedOrder)
                .trade(trade)
                .quoteWallet(quoteWallet)
                .baseWallet(baseWallet)
                .build();
    }

    private TradeExecutionResult executeSell(TradeOrderEntity order,
                                             AggregatedPriceEntity latestPrice) {
        BigDecimal price = roundingPolicyService.normalizePrice(latestPrice.getBestBidPrice());
        BigDecimal grossQuoteAmount = roundingPolicyService.normalizeQuote(price.multiply(order.getQuantity()));
        BigDecimal feeAmount = feePolicyService.calculateFee(grossQuoteAmount);
        BigDecimal netCredit = roundingPolicyService.normalizeQuote(grossQuoteAmount.subtract(feeAmount));

        WalletEntity baseWallet = walletRepository.findByUserIdAndAssetForUpdate(order.getUserId(), order.getBaseAsset())
                .orElseThrow(() -> new BusinessException(order.getBaseAsset() + " wallet not found"));
        WalletEntity quoteWallet = walletRepository.findByUserIdAndAssetForUpdate(order.getUserId(), order.getQuoteAsset())
                .orElseThrow(() -> new BusinessException(order.getQuoteAsset() + " wallet not found"));

        if (baseWallet.getBalance().compareTo(order.getQuantity()) < 0) {
            throw new BusinessException("Insufficient " + order.getBaseAsset() + " balance");
        }

        BigDecimal baseBefore = baseWallet.getBalance();
        BigDecimal quoteBefore = quoteWallet.getBalance();

        baseWallet.setBalance(roundingPolicyService.normalizeQuantity(baseWallet.getBalance().subtract(order.getQuantity())));
        quoteWallet.setBalance(roundingPolicyService.normalizeQuote(quoteWallet.getBalance().add(netCredit)));
        baseWallet.setUpdatedAt(LocalDateTime.now());
        quoteWallet.setUpdatedAt(LocalDateTime.now());

        walletRepository.save(baseWallet);
        walletRepository.save(quoteWallet);

        TradeOrderEntity executedOrder = completeOrder(order, latestPrice.getBestBidSource(), price, grossQuoteAmount, feeAmount, netCredit);
        TradeEntity trade = tradeRepository.save(TradeEntity.builder()
                .orderId(executedOrder.getId())
                .clientOrderId(executedOrder.getClientOrderId())
                .userId(executedOrder.getUserId())
                .pairSymbol(executedOrder.getPairSymbol())
                .side(executedOrder.getSide())
                .baseAsset(executedOrder.getBaseAsset())
                .quoteAsset(executedOrder.getQuoteAsset())
                .quantity(executedOrder.getQuantity())
                .executedPrice(price)
                .grossQuoteAmount(grossQuoteAmount)
                .feeRate(feePolicyService.feeRate())
                .feeAmount(feeAmount)
                .feeAsset(feePolicyService.feeAsset())
                .netQuoteAmount(netCredit)
                .priceSource(latestPrice.getBestBidSource())
                .orderStatus(executedOrder.getStatus())
                .executedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build());

        executedOrder.setTradeId(trade.getId());
        executedOrder.setUpdatedAt(LocalDateTime.now());
        tradeOrderRepository.save(executedOrder);

        saveWalletAudit(executedOrder.getUserId(), executedOrder.getBaseAsset(), executedOrder.getQuantity().negate(), baseBefore, baseWallet.getBalance(), trade.getId());
        saveWalletAudit(executedOrder.getUserId(), executedOrder.getQuoteAsset(), netCredit, quoteBefore, quoteWallet.getBalance(), trade.getId());

        return TradeExecutionResult.builder()
                .order(executedOrder)
                .trade(trade)
                .quoteWallet(quoteWallet)
                .baseWallet(baseWallet)
                .build();
    }

    private TradeOrderEntity completeOrder(TradeOrderEntity order,
                                           String priceSource,
                                           BigDecimal executedPrice,
                                           BigDecimal grossQuoteAmount,
                                           BigDecimal feeAmount,
                                           BigDecimal netQuoteAmount) {
        order.setExecutedPrice(executedPrice);
        order.setPriceSource(priceSource);
        order.setGrossQuoteAmount(grossQuoteAmount);
        order.setFeeRate(feePolicyService.feeRate());
        order.setFeeAmount(feeAmount);
        order.setFeeAsset(feePolicyService.feeAsset());
        order.setNetQuoteAmount(netQuoteAmount);
        order.setStatus(OrderStatus.COMPLETED.name());
        order.setFailureReason(null);
        order.setUpdatedAt(LocalDateTime.now());
        return tradeOrderRepository.save(order);
    }

    private void failOrder(TradeOrderEntity order, String reason) {
        order.setStatus(OrderStatus.FAILED.name());
        order.setFailureReason(reason);
        order.setUpdatedAt(LocalDateTime.now());
        tradeOrderRepository.save(order);
    }

    private WalletEntity getWallet(Long userId, String asset) {
        return walletRepository.findByUserIdAndAsset(userId, asset)
                .orElseThrow(() -> new BusinessException(asset + " wallet not found"));
    }

    private void saveWalletAudit(Long userId,
                                 String asset,
                                 BigDecimal changeAmount,
                                 BigDecimal before,
                                 BigDecimal after,
                                 Long tradeId) {
        walletTransactionRepository.save(WalletTransactionEntity.builder()
                .userId(userId)
                .asset(asset)
                .changeAmount(changeAmount)
                .balanceBefore(before)
                .balanceAfter(after)
                .referenceType("TRADE")
                .referenceId(tradeId)
                .createdAt(LocalDateTime.now())
                .build());
    }
}
