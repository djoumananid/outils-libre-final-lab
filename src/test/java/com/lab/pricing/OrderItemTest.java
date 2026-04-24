package com.lab.pricing;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    @Test
    void lineTotalIsUnitPriceTimesQuantity() {
        OrderItem item = new OrderItem(10.00, 3);
        assertEquals(30.00, item.getLineTotal(), 0.001);
    }

    @Test
    void negativePriceThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new OrderItem(-1.00, 1));
    }

    @Test
    void zeroQuantityThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new OrderItem(10.00, 0));
    }

    @Test
    void zeroPriceIsAllowed() {
        // Free items are valid (e.g., promotions)
        OrderItem item = new OrderItem(0.00, 5);
        assertEquals(0.00, item.getLineTotal(), 0.001);
    }
}
