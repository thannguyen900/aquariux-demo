package com.aquariux.demo.enums;

public enum TickerSymbol {
    BTCUSDT("BTCUSDT"),
    ETHUSDT("ETHUSDT");

    private final String symbol;

    TickerSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

}
