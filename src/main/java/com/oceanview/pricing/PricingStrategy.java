package com.oceanview.pricing;

import java.math.BigDecimal;

public interface PricingStrategy {
    BigDecimal calculateTotal(int nights, BigDecimal rate);
}