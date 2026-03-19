package com.aquariux.demo.repository;

import com.aquariux.demo.entity.AggregatedPriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AggregatedPriceRepository extends JpaRepository<AggregatedPriceEntity, Long> {

    Optional<AggregatedPriceEntity> findTopByPairSymbolOrderByPriceTimeDesc(String pairSymbol);

}
