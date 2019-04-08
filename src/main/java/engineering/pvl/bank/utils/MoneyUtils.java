package engineering.pvl.bank.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public class MoneyUtils {

    public static final Currency EUR = Currency.getInstance("EUR");
    public static final Currency USD = Currency.getInstance("USD");
    public static final Currency CHF = Currency.getInstance("CHF");
    public static final Currency RUB = Currency.getInstance("RUB");

    private MoneyUtils() {
    }

    public static BigDecimal normalize(BigDecimal amount) {
        if (amount == null) {
            return null;
        }

        BigDecimal result = amount.stripTrailingZeros();
        if (result.scale() > 2) {
            throw new BankOperationException("Maximum precision for money is two digits to the right of the decimal point. Got " + result);
        }
        result = result.setScale(2, RoundingMode.UNNECESSARY);
        return result;
    }
}
