package com.lab.pricing;

/**
 * Represents the type of customer placing an order.
 * Each type has a different tax rate and may qualify for additional discounts.
 */
public enum CustomerType {

    /** Standard customer — 8% tax rate, no loyalty discount. */
    REGULAR(0.08, 0.00),

    /** Premium customer — 6% tax rate, extra 5% loyalty discount. */
    VIP(0.06, 0.05);

    private final double taxRate;
    private final double loyaltyDiscount;

    CustomerType(double taxRate, double loyaltyDiscount) {
        this.taxRate = taxRate;
        this.loyaltyDiscount = loyaltyDiscount;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public double getLoyaltyDiscount() {
        return loyaltyDiscount;
    }
}
