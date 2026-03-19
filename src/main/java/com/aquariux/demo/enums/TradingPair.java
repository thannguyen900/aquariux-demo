package com.aquariux.demo.enums;

import java.util.Arrays;

public enum TradingPair {
    BTCUSDT("BTC", "USDT"),
    ETHUSDT("ETH", "USDT");

    private final String baseAsset;
    private final String quoteAsset;

    TradingPair(String baseAsset, String quoteAsset) {
        this.baseAsset = baseAsset;
        this.quoteAsset = quoteAsset;
    }

    public String getBaseAsset() {
        return baseAsset;
    }

    public String getQuoteAsset() {
        return quoteAsset;
    }

    public static TradingPair from(String value) {
        return Arrays.stream(values())
                .filter(pair -> pair.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported pair: " + value));
    }
}
