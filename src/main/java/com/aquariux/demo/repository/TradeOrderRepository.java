package com.aquariux.demo.repository;

import com.aquariux.demo.entity.TradeOrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TradeOrderRepository extends JpaRepository<TradeOrderEntity, Long> {

    Optional<TradeOrderEntity> findByClientOrderId(String clientOrderId);

    Page<TradeOrderEntity> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

}
