package engineering.pvl.bank.account.repository;

import engineering.pvl.bank.account.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static engineering.pvl.bank.utils.MoneyUtils.*;

public class InitialAccountImporter {
    private static final Logger log = LoggerFactory.getLogger(InitialAccountImporter.class);

    private final AccountRepository accountRepository;

    public InitialAccountImporter(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void importData() {
        log.debug("Importing initial accounts");
        List.of(
                new Account(
                        UUID.fromString("11111111-1111-1111-1111-111111111111"),
                        "Main account", BigDecimal.valueOf(25.15), EUR),
                new Account(
                        UUID.fromString("21111111-1111-1111-1111-111111111111"),
                        "Internet account", BigDecimal.valueOf(121.33), EUR),
                new Account(
                        UUID.fromString("31111111-1111-1111-1111-111111111111"),
                        "Travel account (North America)", BigDecimal.valueOf(0.1), USD),
                new Account("Travel account (Russia)", BigDecimal.valueOf(100), RUB),
                new Account("Travel account (Switzerland)", BigDecimal.valueOf(100), CHF),
                new Account("Savings", BigDecimal.valueOf(1000.99), EUR),
                new Account("Family", BigDecimal.valueOf(0.99), EUR)
        ).forEach(accountRepository::create);
        log.info("Imported {} initial accounts", accountRepository.list().size());
    }
}
