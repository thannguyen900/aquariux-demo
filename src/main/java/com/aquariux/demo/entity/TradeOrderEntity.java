package com.aquariux.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trade_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeOrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_order_id", nullable = false, unique = true, length = 64)
    private String clientOrderId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "trade_id")
    private Long tradeId;

    @Column(name = "pair_symbol", nullable = false, length = 20)
    private String pairSymbol;

    @Column(name = "side", nullable = false, length = 10)
    private String side;

    @Column(name = "base_asset", nullable = false, length = 20)
    private String baseAsset;

    @Column(name = "quote_asset", nullable = false, length = 20)
    private String quoteAsset;

    @Column(name = "quantity", nullable = false, precision = 38, scale = 18)
    private BigDecimal quantity;

    @Column(name = "price_scale", nullable = false)
    private Integer priceScale;

    @Column(name = "quantity_scale", nullable = false)
    private Integer quantityScale;

    @Column(name = "quote_scale", nullable = false)
    private Integer quoteScale;

    @Column(name = "fee_scale", nullable = false)
    private Integer feeScale;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "latest_price_id")
    private Long latestPriceId;

    @Column(name = "executed_price", precision = 38, scale = 18)
    private BigDecimal executedPrice;

    @Column(name = "price_source", length = 50)
    private String priceSource;

    @Column(name = "gross_quote_amount", precision = 38, scale = 18)
    private BigDecimal grossQuoteAmount;

    @Column(name = "fee_rate", precision = 18, scale = 10)
    private BigDecimal feeRate;

    @Column(name = "fee_amount", precision = 38, scale = 18)
    private BigDecimal feeAmount;

    @Column(name = "fee_asset", length = 20)
    private String feeAsset;

    @Column(name = "net_quote_amount", precision = 38, scale = 18)
    private BigDecimal netQuoteAmount;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
