package com.lab.pricing;

/**
 * Immutable result object containing the full price breakdown of an order.
 */
public final class PriceBreakdown {

    private final double subtotal;
    private final double discountAmount;
    private final double taxableAmount;
    private final double taxAmount;
    private final double finalPrice;

    public PriceBreakdown(double subtotal,
                          double discountAmount,
                          double taxableAmount,
                          double taxAmount,
                          double finalPrice) {
        this.subtotal        = subtotal;
        this.discountAmount  = discountAmount;
        this.taxableAmount   = taxableAmount;
        this.taxAmount       = taxAmount;
        this.finalPrice      = finalPrice;
    }

    public double getSubtotal()       { return subtotal; }
    public double getDiscountAmount() { return discountAmount; }
    public double getTaxableAmount()  { return taxableAmount; }
    public double getTaxAmount()      { return taxAmount; }
    public double getFinalPrice()     { return finalPrice; }

@Override
public String toString() {
    return String.format(
        "+----------------+-----------+\n" +
        "| Field          | Value     |\n" +
        "+----------------+-----------+\n" +
        "| Subtotal       | %8.2f |\n" +
        "| Discount       | %8.2f |\n" +
        "| Taxable Amount | %8.2f |\n" +
        "| Tax Amount     | %8.2f |\n" +
        "| Final Price    | %8.2f |\n" +
        "+----------------+-----------+",
        subtotal,
        discountAmount,
        taxableAmount,
        taxAmount,
        finalPrice
    );
}
}
