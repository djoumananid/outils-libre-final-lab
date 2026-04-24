package com.lab.pricing;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DiscountCodeTest {

    @Test
    void noneHasZeroRate() {
        assertEquals(0.00, DiscountCode.NONE.getRate(), 0.001);
    }

    @Test
    void save10HasTenPercentRate() {
        assertEquals(0.10, DiscountCode.SAVE10.getRate(), 0.001);
    }

    @Test
    void fromStringParsesValidCode() {
        assertEquals(DiscountCode.SAVE20, DiscountCode.fromString("SAVE20"));
    }

    @Test
    void fromStringIsCaseInsensitive() {
        assertEquals(DiscountCode.SAVE30, DiscountCode.fromString("save30"));
    }

    @Test
    void fromStringReturnsNoneForNull() {
        assertEquals(DiscountCode.NONE, DiscountCode.fromString(null));
    }

    @Test
    void fromStringReturnsNoneForUnknownCode() {
        assertEquals(DiscountCode.NONE, DiscountCode.fromString("BOGUS"));
    }
}
