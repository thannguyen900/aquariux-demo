package com.aquariux.demo.external.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ExchangePrice {
    private final String source;
    private final String symbol;
    private final BigDecimal bidPrice;
    private final BigDecimal askPrice;
}
