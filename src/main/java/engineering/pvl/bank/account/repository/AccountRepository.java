package engineering.pvl.bank.account.repository;

import engineering.pvl.bank.account.model.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface AccountRepository {

    Account create(Account account);

    Account getById(UUID id);

    List<Account> list();

    Account addAmount(Account account, BigDecimal amount);

    Account subtractAmount(Account account, BigDecimal amount);

}
