package com.lab.pricing;

import java.util.List;
import java.util.Objects;

/**
 * REFACTORED PricingEngine — clean, testable, well-separated.
 *
 * Responsibilities:
 *   1. Calculate the raw subtotal from a list of {@link OrderItem}s.
 *   2. Apply a promo {@link DiscountCode} discount (flat rate off subtotal).
 *   3. Apply a loyalty discount for VIP {@link CustomerType}.
 *   4. Compute tax based on the customer type.
 *   5. Return a full {@link PriceBreakdown}.
 *
 * All amounts are rounded to 2 decimal places.
 */
public class PricingEngine {

    // ---------------------------------------------------------------
    // Public API
    // ---------------------------------------------------------------

    /**
     * Calculate the full price breakdown for an order.
     *
     * @param items        non-null, non-empty list of order items
     * @param customerType the type of customer (REGULAR or VIP)
     * @param discountCode promo code to apply (use {@code DiscountCode.NONE} for no promo)
     * @return a complete {@link PriceBreakdown}
     */
    public PriceBreakdown calculate(List<OrderItem> items,
                                    CustomerType customerType,
                                    DiscountCode discountCode) {
        validateInputs(items, customerType, discountCode);

        double subtotal        = computeSubtotal(items);
        double promoDiscount   = computePromoDiscount(subtotal, discountCode);
        double afterPromo      = subtotal - promoDiscount;
        double loyaltyDiscount = computeLoyaltyDiscount(afterPromo, customerType);
        double totalDiscount   = promoDiscount + loyaltyDiscount;
        double taxableAmount   = subtotal - totalDiscount;
        double taxAmount       = computeTax(taxableAmount, customerType);
        double finalPrice      = taxableAmount + taxAmount;

        return new PriceBreakdown(
            round(subtotal),
            round(totalDiscount),
            round(taxableAmount),
            round(taxAmount),
            round(finalPrice)
        );
    }

    // ---------------------------------------------------------------
    // Private helpers — each does exactly one thing
    // ---------------------------------------------------------------

    private void validateInputs(List<OrderItem> items,
                                 CustomerType customerType,
                                 DiscountCode discountCode) {
        Objects.requireNonNull(items,        "Items list must not be null.");
        Objects.requireNonNull(customerType, "CustomerType must not be null.");
        Objects.requireNonNull(discountCode, "DiscountCode must not be null.");
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Items list must not be empty.");
        }
    }

    /** Sum all line totals. */
    private double computeSubtotal(List<OrderItem> items) {
        return items.stream()
                    .mapToDouble(OrderItem::getLineTotal)
                    .sum();
    }

    /** Apply the flat promo-code percentage to the subtotal. */
    private double computePromoDiscount(double subtotal, DiscountCode code) {
        return subtotal * code.getRate();
    }

    /** Apply the customer-type loyalty discount to the post-promo amount. */
    private double computeLoyaltyDiscount(double afterPromo, CustomerType customerType) {
        return afterPromo * customerType.getLoyaltyDiscount();
    }

    /** Apply the customer-type tax rate to the taxable amount. */
    private double computeTax(double taxableAmount, CustomerType customerType) {
        return taxableAmount * customerType.getTaxRate();
    }

    /** Round to 2 decimal places. */
    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
