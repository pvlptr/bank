package engineering.pvl.bank.transaction.service;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

public class TransferRequest {
    private final UUID fromAccountId;
    private final UUID toAccountId;
    private final BigDecimal amount;
    private final Currency currency;

    public TransferRequest(UUID fromAccountId, UUID toAccountId,
                           BigDecimal amount, Currency currency) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.currency = currency;
    }

    UUID getFromAccountId() {
        return fromAccountId;
    }

    UUID getToAccountId() {
        return toAccountId;
    }

    BigDecimal getAmount() {
        return amount;
    }

    Currency getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return "TransferRequest{" +
                "fromAccountId=" + fromAccountId +
                ", toAccountId=" + toAccountId +
                ", amount=" + amount +
                ", currency=" + currency +
                '}';
    }
}
