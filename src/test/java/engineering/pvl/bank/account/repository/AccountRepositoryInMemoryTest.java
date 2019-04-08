package engineering.pvl.bank.account.repository;

import engineering.pvl.bank.account.model.Account;
import engineering.pvl.bank.utils.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static engineering.pvl.bank.utils.BankAssertions.assertAmountEquals;
import static engineering.pvl.bank.utils.MoneyUtils.USD;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;

class AccountRepositoryInMemoryTest {

    private final AccountRepositoryInMemory repository = new AccountRepositoryInMemory();

    @Test
    void getById_should_return_created_account() {

        Account account1 = makeAccount();
        Account account2 = makeAccount();

        repository.create(account1);
        repository.create(account2);

        assertEquals(2, repository.list().size());
        assertSame(account1, repository.getById(account1.getId()));
        assertSame(account2, repository.getById(account2.getId()));
    }

    @Test
    void create_account_with_existing_id_should_fail() {
        Account orig = makeAccount();
        Account cloneWithSameId = makeAccount(orig.getId());

        repository.create(orig);
        try {
            repository.create(cloneWithSameId);
            fail();
        } catch (RuntimeException ex) {
            //ok
        }
        assertEquals(1, repository.list().size());
        assertSame(orig, repository.getById(orig.getId()));
    }

    @Test
    void subtractAmount_should_subtract_money_from_balance() {
        Account account = makeAccount();
        account.setBalance(BigDecimal.valueOf(100));
        repository.subtractAmount(account, BigDecimal.valueOf(5));
        assertAmountEquals(95, account.getBalance());
    }

    @Test
    void addAmount_should_add_money_to_balance() {
        Account account = makeAccount();
        account.setBalance(BigDecimal.valueOf(100));
        repository.addAmount(account, BigDecimal.valueOf(5));
        assertAmountEquals(105, account.getBalance());
    }

    @Test
    void getOne_by_not_existing_id_should_fail() {
        try {
            repository.getById(UUID.randomUUID());
            fail();
        } catch (ResourceNotFoundException ex) {
            //ok
        }
    }

    @Test
    void list_should_return_all_accounts() {

        assertEquals(0, repository.list().size());

        List<Account> accounts = List.of(
                repository.create(makeAccount()),
                repository.create(makeAccount()),
                repository.create(makeAccount()));

        assertEquals(3, repository.list().size());
        assertThat(repository.list(), containsInAnyOrder(accounts.toArray()));
    }

    private Account makeAccount() {
        return new Account("name", BigDecimal.TEN, USD);
    }

    private Account makeAccount(UUID id) {
        return new Account(id, "name", BigDecimal.TEN, USD);
    }
}