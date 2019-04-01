package engineering.pvl.bank.account.model;

import engineering.pvl.bank.utils.MoneyUtils;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;
import java.util.UUID;

/**
 * Single bank account.
 */
public final class Account {

    private final UUID id;
    private final String name;
    private BigDecimal balance;
    private final Currency currency;

    public Account(String name, Currency currency) {
        this(UUID.randomUUID(), name, BigDecimal.ZERO, currency);
    }

    public Account(String name, BigDecimal balance, Currency currency) {
        this(UUID.randomUUID(), name, balance, currency);
    }

    public Account(UUID id, String name, BigDecimal balance, Currency currency) {
        this.id = id;
        this.name = name;
        this.balance = MoneyUtils.normalize(balance);
        this.currency = currency;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // All threads are sharing same instances of model object (repositories returns original objects).
    // So it is necessary to synchronize accessor to mutable fields.
    //
    // Balance is only mutable field of our model.
    public synchronized BigDecimal getBalance() {
        return balance;
    }

    public synchronized void setBalance(BigDecimal balance) {
        this.balance = MoneyUtils.normalize(balance);
    }

    public Currency getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", balance=" + balance +
                ", currency=" + currency +
                '}';
    }
}
