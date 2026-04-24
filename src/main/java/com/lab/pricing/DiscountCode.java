package com.lab.pricing;

/**
 * Discount codes that can be applied to an order.
 * Each code maps to a fixed percentage off the subtotal.
 */
public enum DiscountCode {

    NONE(0.00),
    SAVE10(0.10),
    SAVE20(0.20),
    SAVE30(0.30);

    private final double rate;

    DiscountCode(double rate) {
        this.rate = rate;
    }

    public double getRate() {
        return rate;
    }

    /**
     * Safely parse a discount code string, returning NONE for null/unknown values.
     */
    public static DiscountCode fromString(String code) {
        if (code == null || code.isBlank()) return NONE;
        try {
            return DiscountCode.valueOf(code.toUpperCase());
        } catch (IllegalArgumentException e) {
            return NONE;
        }
    }
}
