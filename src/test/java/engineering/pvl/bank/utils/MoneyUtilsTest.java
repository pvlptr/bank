package engineering.pvl.bank.utils;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

class MoneyUtilsTest {
    @Test
    void normalizeAmount_should_leave_two_digits_after_decimal_separator() {
        assertEquals(BigDecimal.TEN.setScale(2, RoundingMode.UNNECESSARY),
                MoneyUtils.normalize(BigDecimal.TEN));
        assertEquals(BigDecimal.valueOf(1.5).setScale(2, RoundingMode.UNNECESSARY),
                MoneyUtils.normalize(BigDecimal.valueOf(1.5)));
        assertEquals(BigDecimal.valueOf(1.51),
                MoneyUtils.normalize(BigDecimal.valueOf(1.51)));
    }

    @Test
    void normalizeAmount_for_too_big_precision_should_fail() {
        try {
            MoneyUtils.normalize(BigDecimal.valueOf(1.511));
            fail();
        } catch (BankOperationException ex) {
            //ok
        }
    }

    @Test
    void normalizeAmount_should_strip_trailing_zeros() {
        assertEquals(BigDecimal.valueOf(1.51),
                MoneyUtils.normalize(BigDecimal.valueOf(1.51).setScale(5, RoundingMode.UNNECESSARY)));
        assertEquals(BigDecimal.valueOf(1.5).setScale(2, RoundingMode.UNNECESSARY),
                MoneyUtils.normalize(BigDecimal.valueOf(1.5).setScale(5, RoundingMode.UNNECESSARY)));
    }

    @Test
    void normalizeAmount_for_null_should_return_null() {
        assertNull(MoneyUtils.normalize(null));
    }

}