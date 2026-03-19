package com.aquariux.demo.service;

import com.aquariux.demo.config.AppProperties;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class RoundingPolicyService {

    private final AppProperties appProperties;

    public RoundingPolicyService(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public BigDecimal normalizeQuantity(BigDecimal value) {
        return scale(value, appProperties.getPrecision().getQuantityScale());
    }

    public BigDecimal normalizePrice(BigDecimal value) {
        return scale(value, appProperties.getPrecision().getPriceScale());
    }

    public BigDecimal normalizeQuote(BigDecimal value) {
        return scale(value, appProperties.getPrecision().getQuoteScale());
    }

    public BigDecimal normalizeFee(BigDecimal value) {
        return scale(value, appProperties.getPrecision().getFeeScale());
    }

    public RoundingMode roundingMode() {
        return RoundingMode.valueOf(appProperties.getPrecision().getRoundingMode());
    }

    public int quantityScale() {
        return appProperties.getPrecision().getQuantityScale();
    }

    public int priceScale() {
        return appProperties.getPrecision().getPriceScale();
    }

    public int quoteScale() {
        return appProperties.getPrecision().getQuoteScale();
    }

    public int feeScale() {
        return appProperties.getPrecision().getFeeScale();
    }

    private BigDecimal scale(BigDecimal value, int scale) {
        return value.setScale(scale, roundingMode());
    }
}
