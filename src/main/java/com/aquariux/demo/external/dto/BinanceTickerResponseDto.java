package com.aquariux.demo.external.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class BinanceTickerResponseDto {
    private final String symbol;
    private final String bidPrice;
    private final String bidQty;
    private final String askPrice;
    private final String askQty;
}
