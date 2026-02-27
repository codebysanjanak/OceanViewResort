package com.oceanview.pricing;

import java.math.BigDecimal;

public class SeasonalPricingStrategy implements PricingStrategy {
    @Override
    public BigDecimal calculateTotal(int nights, BigDecimal rate) {
        BigDecimal base = rate.multiply(BigDecimal.valueOf(nights));
        return base.multiply(new BigDecimal("1.10")); // example +10%
    }
}