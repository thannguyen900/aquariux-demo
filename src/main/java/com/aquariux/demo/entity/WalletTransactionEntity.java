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
@Table(name = "wallet_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletTransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "asset", nullable = false, length = 20)
    private String asset;

    @Column(name = "change_amount", nullable = false, precision = 38, scale = 18)
    private BigDecimal changeAmount;

    @Column(name = "balance_before", nullable = false, precision = 38, scale = 18)
    private BigDecimal balanceBefore;

    @Column(name = "balance_after", nullable = false, precision = 38, scale = 18)
    private BigDecimal balanceAfter;

    @Column(name = "reference_type", nullable = false, length = 30)
    private String referenceType;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
