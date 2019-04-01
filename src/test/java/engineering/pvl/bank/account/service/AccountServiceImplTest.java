package engineering.pvl.bank.account.service;

import engineering.pvl.bank.account.model.Account;
import engineering.pvl.bank.account.repository.AccountRepository;
import engineering.pvl.bank.account.repository.AccountRepositoryInMemory;
import engineering.pvl.bank.utils.BankOperationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static engineering.pvl.bank.utils.BankAssertions.assertAmountEquals;
import static engineering.pvl.bank.utils.MoneyUtils.EUR;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

class AccountServiceImplTest {

    private AccountRepository accountRepository;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountRepository = new AccountRepositoryInMemory();
        accountService = new AccountServiceImpl(accountRepository);
    }

    @Test
    void create() {
        Account account = accountService.create(new AccountCreateRequest("name", EUR));
        assertSame(account, accountRepository.getById(account.getId()));
        assertAmountEquals(0, account.getBalance());
    }

    @Test
    void create_without_name_should_fail() {
        try {
            accountService.create(new AccountCreateRequest(null, EUR));
            fail();
        } catch (BankOperationException ex) {
            //ok
        }
        try {
            accountService.create(new AccountCreateRequest("", EUR));
            fail();
        } catch (BankOperationException ex) {
            //ok
        }
    }

    @Test
    void create_without_currency_should_fail() {
        try {
            accountService.create(new AccountCreateRequest("name", null));
            fail();
        } catch (BankOperationException ex) {
            //ok
        }
    }

}
