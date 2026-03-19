package com.aquariux.demo.external.client;

import com.aquariux.demo.config.AppProperties;
import com.aquariux.demo.enums.TickerSymbol;
import com.aquariux.demo.external.dto.BinanceTickerResponseDto;
import com.aquariux.demo.external.dto.ExchangePrice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinanceClient {

    private static final String SOURCE = "BINANCE";

    private final RestTemplate restTemplate;

    private final AppProperties appProperties;

    public List<ExchangePrice> fetchBookTickers() {
        try {
            ResponseEntity<BinanceTickerResponseDto[]> response =
                    restTemplate.getForEntity(appProperties.getExternal().getBinanceUrl(), BinanceTickerResponseDto[].class);

            BinanceTickerResponseDto[] body = response.getBody();
            if (Objects.isNull(body) || body.length == 0) {
                log.warn("Binance returned empty response");
                return Collections.emptyList();
            }

            return Arrays.stream(body)
                    .filter(this::isSupportedPair)
                    .map(this::toExchangePrice)
                    .toList();

        } catch (Exception ex) {
            log.warn("Failed to fetch quotes from Binance: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    private boolean isSupportedPair(BinanceTickerResponseDto tickerResponse) {
        if (Objects.isNull(tickerResponse) || Objects.isNull(tickerResponse.getSymbol())) {
            return false;
        }
        return TickerSymbol.BTCUSDT.getSymbol().equalsIgnoreCase(tickerResponse.getSymbol())
                || TickerSymbol.ETHUSDT.getSymbol().equalsIgnoreCase(tickerResponse.getSymbol());
    }

    private ExchangePrice toExchangePrice(BinanceTickerResponseDto tickerResponse) {
        return new ExchangePrice(
                SOURCE,
                tickerResponse.getSymbol().toUpperCase(),
                new BigDecimal(tickerResponse.getBidPrice()),
                new BigDecimal(tickerResponse.getAskPrice())
        );
    }
}
