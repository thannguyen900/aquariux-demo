package com.aquariux.demo.external.client;

import com.aquariux.demo.config.AppProperties;
import com.aquariux.demo.enums.TickerSymbol;
import com.aquariux.demo.external.dto.ExchangePrice;
import com.aquariux.demo.external.dto.HuobiBookTickerDto;
import com.aquariux.demo.external.dto.HuobiTickerResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class HuobiClient {

    private static final String SOURCE = "HUOBI";

    private final RestTemplate restTemplate;

    private final AppProperties appProperties;

    public List<ExchangePrice> fetchBookTickers() {
        try {
            ResponseEntity<HuobiTickerResponseDto> response =
                    restTemplate.getForEntity(appProperties.getExternal().getHuobiUrl(), HuobiTickerResponseDto.class);

            HuobiTickerResponseDto body = response.getBody();
            if (Objects.isNull(body)|| Objects.isNull(body.getData()) || body.getData().isEmpty()) {
                log.warn("Huobi returned empty response");
                return Collections.emptyList();
            }

            return body.getData().stream()
                    .filter(this::isSupportedPair)
                    .map(this::toExchangePrice)
                    .toList();

        } catch (Exception ex) {
            log.warn("Failed to fetch quotes from Huobi: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    private boolean isSupportedPair(HuobiBookTickerDto dto) {
        if (Objects.isNull(dto) || Objects.isNull(dto.getSymbol())) {
            return false;
        }

        String symbol = dto.getSymbol();
        return TickerSymbol.BTCUSDT.getSymbol().equalsIgnoreCase(symbol)
                || TickerSymbol.ETHUSDT.getSymbol().equalsIgnoreCase(symbol);
    }

    private ExchangePrice toExchangePrice(HuobiBookTickerDto tickerDto) {
        return new ExchangePrice(
                SOURCE,
                tickerDto.getSymbol().toUpperCase(),
                new BigDecimal(tickerDto.getBid()),
                new BigDecimal(tickerDto.getAsk())
        );
    }
}
