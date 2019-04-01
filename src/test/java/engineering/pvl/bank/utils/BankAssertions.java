package engineering.pvl.bank.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BankAssertions {
    public static void assertMoneyAmountEquals(BigDecimal expected, BigDecimal actual) {
        assertMoneyAmountEquals(expected, actual, null);
    }

    public static void assertMoneyAmountEquals(BigDecimal expected, BigDecimal actual, String msg) {
        expected = expected != null ? expected.setScale(2, RoundingMode.UNNECESSARY) : null;
        actual = actual != null ? actual.setScale(2, RoundingMode.UNNECESSARY) : null;
        assertEquals(expected, actual, msg);
    }

}
