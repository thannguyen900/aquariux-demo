package com.aquariux.demo.dto.response;

import com.aquariux.demo.entity.TradeEntity;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Getter
@Builder
public class TradeHistoryItemResponse {
    private static final int DEFAULT_SCALE = 8;
    private Long orderId;
    private String clientOrderId;
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
    private String orderStatus;
    private LocalDateTime executedAt;

    public static TradeHistoryItemResponse from(TradeEntity trade) {
        return TradeHistoryItemResponse.builder()
                .orderId(trade.getOrderId())
                .clientOrderId(trade.getClientOrderId())
                .tradeId(trade.getId())
                .pair(trade.getPairSymbol())
                .side(trade.getSide())
                .quantity(trade.getQuantity().setScale(DEFAULT_SCALE, RoundingMode.HALF_UP))
                .executedPrice(trade.getExecutedPrice().setScale(DEFAULT_SCALE, RoundingMode.HALF_UP))
                .grossQuoteAmount(trade.getGrossQuoteAmount().setScale(DEFAULT_SCALE, RoundingMode.HALF_UP))
                .feeRate(trade.getFeeRate().setScale(DEFAULT_SCALE, RoundingMode.HALF_UP))
                .feeAmount(trade.getFeeAmount().setScale(DEFAULT_SCALE, RoundingMode.HALF_UP))
                .feeAsset(trade.getFeeAsset())
                .netQuoteAmount(trade.getNetQuoteAmount().setScale(DEFAULT_SCALE, RoundingMode.HALF_UP))
                .source(trade.getPriceSource())
                .orderStatus(trade.getOrderStatus())
                .executedAt(trade.getExecutedAt())
                .build();
    }
}
