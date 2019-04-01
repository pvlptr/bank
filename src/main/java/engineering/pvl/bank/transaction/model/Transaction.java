package engineering.pvl.bank.transaction.model;

import engineering.pvl.bank.utils.DateTimeUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Objects;
import java.util.UUID;

/**
 * Successful bank transfer operation.
 */
public class Transaction {
    private final UUID id;

    private final UUID fromAccountId;
    private final UUID toAccountId;
    private final BigDecimal amount;
    private final Currency currency;
    private final LocalDateTime created;

    public Transaction(UUID fromAccountId, UUID toAccountId, BigDecimal amount, Currency currency) {
        this.id = UUID.randomUUID();
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.currency = currency;
        this.created = DateTimeUtils.nowUTC();
    }

    public Transaction(UUID id, UUID fromAccountId, UUID toAccountId, BigDecimal amount, Currency currency, LocalDateTime created) {
        this.id = id;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.currency = currency;
        this.created = created;
    }

    public UUID getId() {
        return id;
    }

    public UUID getFromAccountId() {
        return fromAccountId;
    }

    public UUID getToAccountId() {
        return toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
