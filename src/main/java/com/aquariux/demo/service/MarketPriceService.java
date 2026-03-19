package com.aquariux.demo.service;

import com.aquariux.demo.dto.response.LatestPriceResponse;
import com.aquariux.demo.entity.AggregatedPriceEntity;
import com.aquariux.demo.enums.TradingPair;
import com.aquariux.demo.exception.BusinessException;
import com.aquariux.demo.repository.AggregatedPriceRepository;
import com.aquariux.demo.scheduler.PriceAggregationScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarketPriceService {

    private final PriceAggregationScheduler priceAggregationScheduler;
    private final AggregatedPriceRepository aggregatedPriceRepository;

    @Transactional
    public LatestPriceResponse getLatestPrice(String pair) {
        String tradingPair = TradingPair.from(pair).name();

        AggregatedPriceEntity entity = aggregatedPriceRepository
                .findTopByPairSymbolOrderByPriceTimeDesc(tradingPair)
                .orElseGet(() -> {
                    priceAggregationScheduler.priceAggregationProcess();
                    return aggregatedPriceRepository
                            .findTopByPairSymbolOrderByPriceTimeDesc(tradingPair)
                            .orElseThrow(() -> new BusinessException("Latest price not available for pair " + tradingPair));
                });

        return LatestPriceResponse.from(entity);
    }
}
