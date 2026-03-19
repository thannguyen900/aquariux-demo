package com.aquariux.demo.scheduler;

import com.aquariux.demo.config.AppProperties;
import com.aquariux.demo.entity.AggregatedPriceEntity;
import com.aquariux.demo.exception.BusinessException;
import com.aquariux.demo.external.client.BinanceClient;
import com.aquariux.demo.external.client.HuobiClient;
import com.aquariux.demo.external.dto.ExchangePrice;
import com.aquariux.demo.repository.AggregatedPriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceAggregationScheduler {

    private final BinanceClient binanceClient;
    private final HuobiClient huobiClient;
    private final AggregatedPriceRepository aggregatedPriceRepository;
    private final AppProperties appProperties;

    @Scheduled(fixedRateString = "${trading.scheduler.fixed-rate-ms}")
    public void scheduledAggregatePrices() {
        aggregatePrices();
    }

    public void priceAggregationProcess() {
        aggregatePrices();
    }

    private void aggregatePrices() {
        List<ExchangePrice> prices = new ArrayList<>();
        prices.addAll(binanceClient.fetchBookTickers());
        prices.addAll(huobiClient.fetchBookTickers());

        if (prices.isEmpty()) {
            log.warn("No market prices retrieved from any source");
            return;
        }

        for (String pair : appProperties.getSupportedPairs()) {
            try {
                saveBestPrice(pair, prices);
            } catch (Exception ex) {
                log.warn("Failed to aggregate price for pair {}: {}", pair, ex.getMessage());
            }
        }
    }

    private void saveBestPrice(String pair, List<ExchangePrice> prices) {
        List<ExchangePrice> pairPrices = prices.stream()
                .filter(q -> pair.equalsIgnoreCase(q.getSymbol()))
                .toList();

        if (pairPrices.isEmpty()) {
            throw new BusinessException("No prices found for pair " + pair);
        }

        ExchangePrice bestBid = pairPrices.stream()
                .max(Comparator.comparing(ExchangePrice::getBidPrice))
                .orElseThrow();

        ExchangePrice bestAsk = pairPrices.stream()
                .min(Comparator.comparing(ExchangePrice::getAskPrice))
                .orElseThrow();

        LocalDateTime now = LocalDateTime.now();
        aggregatedPriceRepository.save(AggregatedPriceEntity.builder()
                .pairSymbol(pair.toUpperCase())
                .bestBidPrice(bestBid.getBidPrice())
                .bestBidSource(bestBid.getSource())
                .bestAskPrice(bestAsk.getAskPrice())
                .bestAskSource(bestAsk.getSource())
                .priceTime(now)
                .createdAt(now)
                .build());
    }
}
