package engineering.pvl.bank.account.service;

import java.util.Currency;

public class AccountCreateRequest {
    private final String name;
    private final Currency currency;

    public AccountCreateRequest(String name, Currency currency) {
        this.name = name;
        this.currency = currency;
    }

    String getName() {
        return name;
    }

    Currency getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return "AccountCreateRequest{" +
                "name='" + name + '\'' +
                ", currency=" + currency +
                '}';
    }
}


