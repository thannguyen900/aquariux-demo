package com.aquariux.demo.repository;

import com.aquariux.demo.entity.WalletTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletTransactionRepository extends JpaRepository<WalletTransactionEntity, Long> {
}
