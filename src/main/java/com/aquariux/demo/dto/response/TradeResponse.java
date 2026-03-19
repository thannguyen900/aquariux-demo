package com.aquariux.demo.dto.response;

import com.aquariux.demo.entity.TradeEntity;
import com.aquariux.demo.entity.TradeOrderEntity;
import com.aquariux.demo.entity.WalletEntity;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Getter
@Builder
public class TradeResponse {
    private Long orderId;
    private String clientOrderId;
    private String orderStatus;
    private String failureReason;
    private Long tradeId;
    private String pair;
    private String side;
    private BigDecimal quantity;
    private BigDecimal executedPrice;
    private BigDecimal grossQuoteAmount;
    private BigDecimal feeRate;
    private BigDecimal feeAmount;
    private String feeAsset;
    private BigDecimal netQuoteAmount;
    private String source;
    private LocalDateTime executedAt;
    private List<WalletBalanceItemResponse> wallets;

    public static TradeResponse from(TradeOrderEntity order, TradeEntity trade, WalletEntity wallet1, WalletEntity wallet2) {
        return TradeResponse.builder()
                .orderId(order.getId())
                .clientOrderId(order.getClientOrderId())
                .orderStatus(order.getStatus())
                .failureReason(order.getFailureReason())
                .tradeId(trade.getId())
                .pair(trade.getPairSymbol())
                .side(trade.getSide())
                .quantity(trade.getQuantity())
                .executedPrice(trade.getExecutedPrice())
                .grossQuoteAmount(trade.getGrossQuoteAmount())
                .feeRate(trade.getFeeRate())
                .feeAmount(trade.getFeeAmount())
                .feeAsset(trade.getFeeAsset())
                .netQuoteAmount(trade.getNetQuoteAmount())
                .source(trade.getPriceSource())
                .executedAt(trade.getExecutedAt())
                .wallets(List.of(wallet1, wallet2).stream()
                        .map(w -> WalletBalanceItemResponse.builder()
                                .asset(w.getAsset())
                                .balance(String.valueOf(w.getBalance()))
                                .build())
                        .sorted(Comparator.comparing(WalletBalanceItemResponse::getAsset))
                        .toList())
                .build();
    }
}
