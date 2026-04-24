package com.lab.pricing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PricingEngine Tests")
class PricingEngineTest {

    private PricingEngine engine;

    @BeforeEach
    void setUp() {
        engine = new PricingEngine();
    }

    // ---------------------------------------------------------------
    // Subtotal calculations
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Subtotal: single item")
    void subtotalSingleItem() {
        List<OrderItem> items = List.of(new OrderItem(50.00, 2));
        PriceBreakdown result = engine.calculate(items, CustomerType.REGULAR, DiscountCode.NONE);
        assertEquals(100.00, result.getSubtotal(), 0.01);
    }

    @Test
    @DisplayName("Subtotal: multiple items summed correctly")
    void subtotalMultipleItems() {
        List<OrderItem> items = List.of(
            new OrderItem(10.00, 3),   // 30
            new OrderItem(5.00,  4)    // 20
        );
        PriceBreakdown result = engine.calculate(items, CustomerType.REGULAR, DiscountCode.NONE);
        assertEquals(50.00, result.getSubtotal(), 0.01);
    }

    // ---------------------------------------------------------------
    // Discount calculations
    // ---------------------------------------------------------------

    @Test
    @DisplayName("No promo code → zero discount for REGULAR")
    void noDiscountForRegularWithNoCode() {
        List<OrderItem> items = List.of(new OrderItem(100.00, 1));
        PriceBreakdown result = engine.calculate(items, CustomerType.REGULAR, DiscountCode.NONE);
        assertEquals(0.00, result.getDiscountAmount(), 0.01);
    }

    @Test
    @DisplayName("SAVE10 applies 10% off subtotal")
    void save10DiscountOnRegular() {
        List<OrderItem> items = List.of(new OrderItem(100.00, 1));
        PriceBreakdown result = engine.calculate(items, CustomerType.REGULAR, DiscountCode.SAVE10);
        assertEquals(10.00, result.getDiscountAmount(), 0.01);
    }

    @Test
    @DisplayName("SAVE20 applies 20% off subtotal")
    void save20DiscountOnRegular() {
        List<OrderItem> items = List.of(new OrderItem(200.00, 1));
        PriceBreakdown result = engine.calculate(items, CustomerType.REGULAR, DiscountCode.SAVE20);
        assertEquals(40.00, result.getDiscountAmount(), 0.01);
    }

    @Test
    @DisplayName("VIP customer gets 5% loyalty discount on top of promo")
    void vipLoyaltyDiscountCombinesWithPromo() {
        List<OrderItem> items = List.of(new OrderItem(100.00, 1));
        // SAVE10 → $10 off → $90 after promo → VIP 5% off $90 = $4.50
        // total discount = $14.50
        PriceBreakdown result = engine.calculate(items, CustomerType.VIP, DiscountCode.SAVE10);
        assertEquals(14.50, result.getDiscountAmount(), 0.01);
    }

    @Test
    @DisplayName("VIP with no promo still gets loyalty discount")
    void vipGetsLoyaltyDiscountWithNoPromo() {
        List<OrderItem> items = List.of(new OrderItem(100.00, 1));
        // VIP 5% of $100 = $5
        PriceBreakdown result = engine.calculate(items, CustomerType.VIP, DiscountCode.NONE);
        assertEquals(5.00, result.getDiscountAmount(), 0.01);
    }

    // ---------------------------------------------------------------
    // Tax calculations
    // ---------------------------------------------------------------

    @Test
    @DisplayName("REGULAR tax rate is 8%")
    void regularCustomerTax() {
        List<OrderItem> items = List.of(new OrderItem(100.00, 1));
        PriceBreakdown result = engine.calculate(items, CustomerType.REGULAR, DiscountCode.NONE);
        assertEquals(8.00, result.getTaxAmount(), 0.01);
    }

    @Test
    @DisplayName("VIP tax rate is 6%")
    void vipCustomerTax() {
        List<OrderItem> items = List.of(new OrderItem(100.00, 1));
        // VIP loyalty = 5%, taxable = $95, tax = 6% of $95 = $5.70
        PriceBreakdown result = engine.calculate(items, CustomerType.VIP, DiscountCode.NONE);
        assertEquals(5.70, result.getTaxAmount(), 0.01);
    }

    // ---------------------------------------------------------------
    // Final price
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Final price: REGULAR, no promo — subtotal + 8% tax")
    void finalPriceRegularNoPromo() {
        List<OrderItem> items = List.of(new OrderItem(100.00, 1));
        PriceBreakdown result = engine.calculate(items, CustomerType.REGULAR, DiscountCode.NONE);
        assertEquals(108.00, result.getFinalPrice(), 0.01);
    }

    @Test
    @DisplayName("Final price: VIP, SAVE20 — full calculation")
    void finalPriceVipSave20() {
        List<OrderItem> items = List.of(new OrderItem(200.00, 1));
        // subtotal=200, promo=40, after promo=160, VIP loyalty=8, taxable=152, tax=9.12, final=161.12
        PriceBreakdown result = engine.calculate(items, CustomerType.VIP, DiscountCode.SAVE20);
        assertEquals(161.12, result.getFinalPrice(), 0.01);
    }

    @Test
    @DisplayName("Final price: REGULAR, SAVE30")
    void finalPriceRegularSave30() {
        List<OrderItem> items = List.of(new OrderItem(100.00, 1));
        // subtotal=100, discount=30, taxable=70, tax=5.60, final=75.60
        PriceBreakdown result = engine.calculate(items, CustomerType.REGULAR, DiscountCode.SAVE30);
        assertEquals(75.60, result.getFinalPrice(), 0.01);
    }

    // ---------------------------------------------------------------
    // Validation / edge cases
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Null items list throws NullPointerException")
    void nullItemsThrows() {
        assertThrows(NullPointerException.class,
            () -> engine.calculate(null, CustomerType.REGULAR, DiscountCode.NONE));
    }

    @Test
    @DisplayName("Empty items list throws IllegalArgumentException")
    void emptyItemsThrows() {
        assertThrows(IllegalArgumentException.class,
            () -> engine.calculate(Collections.emptyList(), CustomerType.REGULAR, DiscountCode.NONE));
    }

    @Test
    @DisplayName("Null customerType throws NullPointerException")
    void nullCustomerTypeThrows() {
        List<OrderItem> items = List.of(new OrderItem(10.00, 1));
        assertThrows(NullPointerException.class,
            () -> engine.calculate(items, null, DiscountCode.NONE));
    }

    @Test
    @DisplayName("Null discountCode throws NullPointerException")
    void nullDiscountCodeThrows() {
        List<OrderItem> items = List.of(new OrderItem(10.00, 1));
        assertThrows(NullPointerException.class,
            () -> engine.calculate(items, CustomerType.REGULAR, null));
    }
}
