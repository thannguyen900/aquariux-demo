package com.aquariux.demo.external.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class HuobiBookTickerDto {
    private final String symbol;
    private final String bid;
    private final String bidSize;
    private final String ask;
    private final String askSize;
}
