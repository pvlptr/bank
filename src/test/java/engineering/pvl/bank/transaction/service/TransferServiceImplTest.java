package engineering.pvl.bank.transaction.service;

import engineering.pvl.bank.account.model.Account;
import engineering.pvl.bank.account.repository.AccountRepository;
import engineering.pvl.bank.account.repository.AccountRepositoryInMemory;
import engineering.pvl.bank.transaction.model.Transaction;
import engineering.pvl.bank.transaction.repository.TransactionRepository;
import engineering.pvl.bank.transaction.repository.TransactionRepositoryInMemory;
import engineering.pvl.bank.utils.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Month;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import static engineering.pvl.bank.utils.BankAssertions.assertAmountEquals;
import static engineering.pvl.bank.utils.MoneyUtils.EUR;
import static engineering.pvl.bank.utils.MoneyUtils.USD;
import static org.junit.jupiter.api.Assertions.*;

class TransferServiceImplTest {

    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;

    private TransferService transferService;

    @BeforeEach
    void setUp() {
        accountRepository = new AccountRepositoryInMemory();
        transactionRepository = new TransactionRepositoryInMemory();
        transferService = new TransferServiceImpl(accountRepository, transactionRepository);

        DateTimeUtils.setCurrentFixed(2019, Month.MARCH, 1, 20, 39, 59);
    }

    @AfterEach
    void tearDown() {
        DateTimeUtils.setCurrentSystem();
    }

    @Test
    void transfer_should_create_transaction_and_move_money() {
        Account from = createAccount(100.10, EUR);
        Account to = createAccount(50.55, EUR);

        transferService.transfer(new TransferRequest(
                from.getId(), to.getId(), BigDecimal.valueOf(15.33), EUR));

        List<Transaction> transactions = transactionRepository.list();
        assertEquals(1, transactions.size());

        //all transaction details
        Transaction transaction = transactions.get(0);
        assertNotNull(transaction.getId());
        assertEquals(from.getId(), transaction.getFromAccountId());
        assertEquals(to.getId(), transaction.getToAccountId());
        assertAmountEquals(15.33, transaction.getAmount());
        assertEquals(EUR, transaction.getCurrency());
        assertEquals(DateTimeUtils.nowUTC(), transaction.getCreated());

        assertAmountEquals(84.77, from.getBalance());
        assertAmountEquals(65.88, to.getBalance());
    }

    @Test
    void transfer_can_move_all_money_from_account() {
        Account from = createAccount(100.11, EUR);
        Account to = createAccount(0, EUR);

        transferService.transfer(new TransferRequest(
                from.getId(), to.getId(), BigDecimal.valueOf(100.11), EUR));

        List<Transaction> transactions = transactionRepository.list();
        assertEquals(1, transactions.size());

        Transaction transaction = transactions.get(0);
        assertAmountEquals(100.11, transaction.getAmount());

        assertAmountEquals(0, from.getBalance());
        assertAmountEquals(100.11, to.getBalance());
    }

    @Test
    void transfer_with_big_numbers_should_work() {
        Account from = createAccount(8_888_888_888_888.88, EUR);
        Account to = createAccount(7_777_777_777_777.77, EUR);

        transferService.transfer(new TransferRequest(
                from.getId(), to.getId(), BigDecimal.valueOf(2_222_222_222_222.22), EUR));

        List<Transaction> transactions = transactionRepository.list();
        assertEquals(1, transactions.size());

        Transaction transaction = transactions.get(0);
        assertAmountEquals(2_222_222_222_222.22, transaction.getAmount());

        assertAmountEquals(6_666_666_666_666.66, from.getBalance());
        assertAmountEquals(9_999_999_999_999.99, to.getBalance());
    }

    @Test
    void transfer_with_too_big_amounts_should_fail() {
        Account from = createAccount(8_888_888_888_888.88, EUR);
        Account to = createAccount(7_777_777_777_777.77, EUR);

        try {
            transferService.transfer(new TransferRequest(
                    from.getId(), to.getId(), BigDecimal.valueOf(2_222_222_222_222.23), EUR));
            fail();
        } catch (BankOperationException ex) {
            //ok
        }

        assertEquals(0, transactionRepository.list().size());
    }


    @Test
    void transfer_of_different_currencies_should_fail() {
        Account eurAccount1 = createAccount(100, EUR);
        Account eurAccount2 = createAccount(100, EUR);
        Account usdAccount3 = createAccount(10, USD);

        try {
            transferService.transfer(new TransferRequest(
                    eurAccount1.getId(), eurAccount2.getId(),
                    BigDecimal.TEN, USD));
            fail();
        } catch (BankOperationException ex) {
            //ok
        }

        try {
            transferService.transfer(new TransferRequest(
                    usdAccount3.getId(), eurAccount2.getId(),
                    BigDecimal.TEN, EUR));
            fail();
        } catch (BankOperationException ex) {
            //ok
        }

        try {
            transferService.transfer(new TransferRequest(
                    eurAccount1.getId(), usdAccount3.getId(),
                    BigDecimal.TEN, EUR));
            fail();
        } catch (BankOperationException ex) {
            //ok
        }

        assertEquals(0, transactionRepository.list().size());
    }

    @Test
    void transfer_with_too_big_precision_should_fail() {
        Account from = createAccount(100, EUR);
        Account to = createAccount(100, EUR);

        try {
            transferService.transfer(new TransferRequest(
                    from.getId(), to.getId(),
                    BigDecimal.valueOf(10.001), EUR));
            fail();
        } catch (BankOperationException ex) {
            //ok
        }

        assertEquals(0, transactionRepository.list().size());
    }

    @Test
    void transfer_of_insufficient_funds_should_fail() {
        Account from = createAccount(100, EUR);
        Account to = createAccount(200, EUR);

        try {
            transferService.transfer(new TransferRequest(
                    from.getId(), to.getId(),
                    BigDecimal.valueOf(100.01), EUR));
            fail();
        } catch (InsufficientFundsException ex) {
            //ok
        }

        assertEquals(0, transactionRepository.list().size());
    }

    @Test
    void transfer_negative_amount_should_fail() {
        Account from = createAccount(100, EUR);
        Account to = createAccount(100, EUR);

        try {
            transferService.transfer(new TransferRequest(
                    from.getId(), to.getId(),
                    BigDecimal.valueOf(-1), EUR));
            fail();
        } catch (BankOperationException ex) {
            //ok
        }

        assertEquals(0, transactionRepository.list().size());
    }

    @Test
    void transfer_for_not_existing_account_should_fail() {
        Account account = createAccount(100, EUR);
        try {
            transferService.transfer(new TransferRequest(
                    UUID.randomUUID(), account.getId(),
                    BigDecimal.ONE, EUR));
            fail();
        } catch (ResourceNotFoundException ex) {
            //ok
        }

        try {
            transferService.transfer(new TransferRequest(
                    account.getId(), UUID.randomUUID(),
                    BigDecimal.ONE, EUR));
            fail();
        } catch (ResourceNotFoundException ex) {
            //ok
        }

        try {
            transferService.transfer(new TransferRequest(
                    UUID.randomUUID(), UUID.randomUUID(),
                    BigDecimal.ONE, EUR));
            fail();
        } catch (ResourceNotFoundException ex) {
            //ok
        }

        assertEquals(0, transactionRepository.list().size());
    }

    @Test
    void transfer_to_same_account_should_fail() {
        Account account = createAccount(100, EUR);

        try {
            transferService.transfer(new TransferRequest(
                    account.getId(), account.getId(),
                    BigDecimal.valueOf(1), EUR));
            fail();
        } catch (BankOperationException ex) {
            //ok
        }

        assertEquals(0, transactionRepository.list().size());
    }

    @Test
    void transfer_with_zero_amount_should_fail() {
        Account account = createAccount(100, EUR);

        try {
            transferService.transfer(new TransferRequest(
                    account.getId(), account.getId(),
                    BigDecimal.ZERO, EUR));
            fail();
        } catch (BankOperationException ex) {
            //ok
        }

        assertEquals(0, transactionRepository.list().size());
    }

    @Test
    void transfer_with_negative_amount_should_fail() {
        Account account = createAccount(100, EUR);

        try {
            transferService.transfer(new TransferRequest(
                    account.getId(), account.getId(),
                    BigDecimal.valueOf(-1), EUR));
            fail();
        } catch (BankOperationException ex) {
            //ok
        }

        assertEquals(0, transactionRepository.list().size());
    }

    private Account createAccount(double amount, Currency currency) {
        return createAccount(BigDecimal.valueOf(amount), currency);
    }

    private Account createAccount(BigDecimal amount, Currency currency) {
        return accountRepository.create(new Account("Account title", amount, currency));
    }

}