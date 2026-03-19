package com.aquariux.demo.repository;

import com.aquariux.demo.entity.AggregatedPriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AggregatedPriceRepository extends JpaRepository<AggregatedPriceEntity, Long> {
}
