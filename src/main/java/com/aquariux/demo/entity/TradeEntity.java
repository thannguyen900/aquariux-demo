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
@Table(name = "trades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "client_order_id", length = 64)
    private String clientOrderId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

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

    @Column(name = "executed_price", nullable = false, precision = 38, scale = 18)
    private BigDecimal executedPrice;

    @Column(name = "gross_quote_amount", nullable = false, precision = 38, scale = 18)
    private BigDecimal grossQuoteAmount;

    @Column(name = "fee_rate", nullable = false, precision = 18, scale = 10)
    private BigDecimal feeRate;

    @Column(name = "fee_amount", nullable = false, precision = 38, scale = 18)
    private BigDecimal feeAmount;

    @Column(name = "fee_asset", nullable = false, length = 20)
    private String feeAsset;

    @Column(name = "net_quote_amount", nullable = false, precision = 38, scale = 18)
    private BigDecimal netQuoteAmount;

    @Column(name = "price_source", nullable = false, length = 50)
    private String priceSource;

    @Column(name = "order_status", nullable = false, length = 20)
    private String orderStatus;

    @Column(name = "executed_at", nullable = false)
    private LocalDateTime executedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
