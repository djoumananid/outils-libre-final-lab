package com.lab.pricing;

import java.util.List;

/**
 * Demo entry point — shows the engine in action with sample data.
 * Run with: ./gradlew run
 */
public class Main {

    public static void main(String[] args) {
        PricingEngine engine = new PricingEngine();

        // --- Example 1: Regular customer, no promo code ---
        List<OrderItem> order1 = List.of(
            new OrderItem(29.99, 2),   // 2× $29.99
            new OrderItem(9.50,  3)    // 3× $9.50
        );
        PriceBreakdown breakdown1 = engine.calculate(order1, CustomerType.REGULAR, DiscountCode.NONE);
        System.out.println("=== REGULAR customer, no promo ===");
        System.out.println(breakdown1);

        // --- Example 2: VIP customer, SAVE20 promo code ---
        List<OrderItem> order2 = List.of(
            new OrderItem(100.00, 1),
            new OrderItem(50.00,  2)
        );
        PriceBreakdown breakdown2 = engine.calculate(order2, CustomerType.VIP, DiscountCode.SAVE20);
        System.out.println("\n=== VIP customer, SAVE20 promo ===");
        System.out.println(breakdown2);
    }
}
