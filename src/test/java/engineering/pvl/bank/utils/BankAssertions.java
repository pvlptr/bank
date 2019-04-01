package engineering.pvl.bank.utils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BankAssertions {

    private BankAssertions() {
    }

    static public void assertAmountEquals(double expectedAmount, BigDecimal actualAmount, String message) {
        assertAmountEquals(BigDecimal.valueOf(expectedAmount), actualAmount, message);
    }

    static public void assertAmountEquals(double expectedAmount, BigDecimal actualAmount) {
        assertAmountEquals(BigDecimal.valueOf(expectedAmount), actualAmount);
    }

    static public void assertAmountEquals(BigDecimal expectedAmount, BigDecimal actualAmount, String message) {
        assertEquals(MoneyUtils.normalize(expectedAmount), actualAmount, message);
    }

    static public void assertAmountEquals(BigDecimal expectedAmount, BigDecimal actualAmount) {
        assertEquals(MoneyUtils.normalize(expectedAmount), actualAmount);
    }
}
