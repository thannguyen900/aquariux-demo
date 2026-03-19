package com.aquariux.demo.service;

import com.aquariux.demo.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class FeePolicyService {

    private final AppProperties appProperties;
    private final RoundingPolicyService roundingPolicyService;

    public BigDecimal feeRate() {
        return appProperties.getFee().getRate();
    }

    public String feeAsset() {
        return appProperties.getFee().getAsset();
    }

    public BigDecimal calculateFee(BigDecimal grossQuoteAmount) {
        return roundingPolicyService.normalizeFee(grossQuoteAmount.multiply(feeRate()));
    }
}
