package com.aquariux.demo.dto.response;

import com.aquariux.demo.entity.AggregatedPriceEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LatestPriceResponse {
    private String pair;
    private String bestBidPrice;
    private String bestBidSource;
    private String bestAskPrice;
    private String bestAskSource;
    private LocalDateTime priceTime;

    public static LatestPriceResponse from(AggregatedPriceEntity entity) {
        return LatestPriceResponse.builder()
                .pair(entity.getPairSymbol())
                .bestBidPrice(String.valueOf(entity.getBestBidPrice()))
                .bestBidSource(entity.getBestBidSource())
                .bestAskPrice(String.valueOf(entity.getBestAskPrice()))
                .bestAskSource(entity.getBestAskSource())
                .priceTime(entity.getPriceTime())
                .build();
    }
}
