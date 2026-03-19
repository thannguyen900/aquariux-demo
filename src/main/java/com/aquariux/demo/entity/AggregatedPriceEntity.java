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
@Table(name = "aggregated_prices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AggregatedPriceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pair_symbol", nullable = false, length = 20)
    private String pairSymbol;

    @Column(name = "best_bid_price", nullable = false, precision = 38, scale = 18)
    private BigDecimal bestBidPrice;

    @Column(name = "best_bid_source", nullable = false, length = 50)
    private String bestBidSource;

    @Column(name = "best_ask_price", nullable = false, precision = 38, scale = 18)
    private BigDecimal bestAskPrice;

    @Column(name = "best_ask_source", nullable = false, length = 50)
    private String bestAskSource;

    @Column(name = "price_time", nullable = false)
    private LocalDateTime priceTime;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

}
