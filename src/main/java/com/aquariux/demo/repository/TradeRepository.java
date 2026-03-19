package com.aquariux.demo.repository;

import com.aquariux.demo.entity.TradeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<TradeEntity, Long> {

    Page<TradeEntity> findByUserIdOrderByExecutedAtDesc(Long userId, Pageable pageable);

}
