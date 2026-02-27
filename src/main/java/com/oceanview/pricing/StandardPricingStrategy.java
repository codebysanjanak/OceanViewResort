package com.oceanview.pricing;

import java.math.BigDecimal;

public class StandardPricingStrategy implements PricingStrategy {
    @Override
    public BigDecimal calculateTotal(int nights, BigDecimal rate) {
        return rate.multiply(BigDecimal.valueOf(nights));
    }
}