package com.lab.pricing;

/**
 * Immutable value object representing a single line item in an order.
 */
public final class OrderItem {

    private final double unitPrice;
    private final int quantity;

    public OrderItem(double unitPrice, int quantity) {
        if (unitPrice < 0) throw new IllegalArgumentException("Unit price cannot be negative.");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be at least 1.");
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public double getUnitPrice() { return unitPrice; }
    public int getQuantity()     { return quantity; }

    /** Convenience: price × quantity for this line. */
    public double getLineTotal() {
        return unitPrice * quantity;
    }

    @Override
    public String toString() {
        return String.format("OrderItem{unitPrice=%.2f, quantity=%d, lineTotal=%.2f}",
                unitPrice, quantity, getLineTotal());
    }
}
