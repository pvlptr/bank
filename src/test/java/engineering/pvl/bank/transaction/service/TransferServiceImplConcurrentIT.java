package engineering.pvl.bank.transaction.service;

import engineering.pvl.bank.ServiceRegistry;
import engineering.pvl.bank.account.model.Account;
import engineering.pvl.bank.account.repository.AccountRepository;
import engineering.pvl.bank.transaction.model.Transaction;
import engineering.pvl.bank.transaction.repository.TransactionRepository;
import engineering.pvl.bank.utils.BankOperationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import engineering.pvl.bank.utils.BankAssertions;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;

import static engineering.pvl.bank.utils.CurrencyUtils.EUR;
import static engineering.pvl.bank.utils.CurrencyUtils.USD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransferServiceImplConcurrentIT {

    private static final int ACCOUNT_COUNT = 10;
    private static final int THREAD_COUNT = 20;
    private static final int OPERATION_COUNT_PER_THREAD = 10000;
    private static final int EXPECTED_TRANSACTIONS_COUNT = THREAD_COUNT * OPERATION_COUNT_PER_THREAD;
    private static final BigDecimal ACCOUNT_INITIAL_AMOUNT = BigDecimal.valueOf(1234567.89);
    private static final BigDecimal TOTAL_AMOUNT_OF_MONEY = ACCOUNT_INITIAL_AMOUNT.multiply(BigDecimal.valueOf(ACCOUNT_COUNT));
    private static final List<Currency> CURRENCIES = List.of(EUR, USD);

    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private TransferService transferService;

    @BeforeEach
    void setUp() {
        ServiceRegistry registry = new ServiceRegistry();
        accountRepository = registry.getAccountRepository();
        transactionRepository = registry.getTransactionRepository();
        transferService = registry.getTransferService();
    }

    /**
     * Test to find race conditions concurrently performing bank transfers in multiple threads .
     * <p>
     * Each thread performs random bank transfers from one account to another.
     * Tests checks if expected number of successful transactions is executed and every account has expected amount of money.
     */
    @Test
    void transfer_executed_concurrently_should_preserve_consistency() throws InterruptedException {

        List<Account> accounts = prepareAccounts(CURRENCIES);
        List<Transaction> allTransactions = executeConcurrentTransfers(accounts);

        //check transactions
        assertEquals(EXPECTED_TRANSACTIONS_COUNT, transactionRepository.list().size());
        assertEquals(allTransactions.size(), transactionRepository.list().size());
        // using HashSet.contains, because containsInAnyOrder performs too slow
        assertTrue(new HashSet<>(transactionRepository.list()).containsAll(allTransactions));
        //assertThat(allTransactions, containsInAnyOrder(transactionRepository.list().toArray()));

        //check balance total
        BankAssertions.assertMoneyAmountEquals(TOTAL_AMOUNT_OF_MONEY, totalAccountBalance(accounts),
                "Total amount of money in bank should not changed");

        //check balance for each account
        Map<UUID, BigDecimal> balances = expectedBalances(allTransactions);
        for (Account a : accounts) {
            BankAssertions.assertMoneyAmountEquals(balances.get(a.getId()), a.getBalance(),
                    "Balance does note match for account " + a);
            assertTrue(a.getBalance().compareTo(BigDecimal.ZERO) >= 0,
                    "Balance of account can not be negative");
        }
    }

    private List<Account> prepareAccounts(List<Currency> currencies) {
        List<Account> accounts = new ArrayList<>();
        for (int i = 0; i < ACCOUNT_COUNT; i++) {
            accounts.add(createAccount("account-" + i, pickRandom(currencies)));
        }
        return accounts;
    }

    private Account createAccount(String name, Currency currency) {
        return accountRepository.create(new Account(name, ACCOUNT_INITIAL_AMOUNT, currency));
    }

    private List<Transaction> executeConcurrentTransfers(List<Account> accounts) throws InterruptedException {
        var callableList = prepareCallableList(accounts);
        ExecutorService execSvc = Executors.newFixedThreadPool(THREAD_COUNT);
        var allTransactionsFutures = execSvc.invokeAll(callableList);
        return extractTransactions(allTransactionsFutures);
    }

    private List<Transaction> extractTransactions(List<Future<List<Transaction>>> allTransactionsFutures) {
        List<Transaction> result = new ArrayList<>();
        for (var f : allTransactionsFutures) {
            try {
                result.addAll(f.get());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    private BigDecimal totalAccountBalance(List<Account> accounts) {
        BigDecimal totalBalance = BigDecimal.ZERO;
        for (Account a : accounts) {
            totalBalance = totalBalance.add(a.getBalance());
        }
        return totalBalance;
    }

    private Map<UUID, BigDecimal> expectedBalances(List<Transaction> allTransactions) {
        Map<UUID, BigDecimal> result = new HashMap<>();

        for (Transaction t : allTransactions) {
            result.put(
                    t.getFromAccountId(),
                    result.getOrDefault(t.getFromAccountId(), ACCOUNT_INITIAL_AMOUNT).subtract(t.getAmount()));
            result.put(
                    t.getToAccountId(),
                    result.getOrDefault(t.getToAccountId(), ACCOUNT_INITIAL_AMOUNT).add(t.getAmount()));
        }

        return result;
    }

    private BigDecimal nextRandomAmount() {
        return BigDecimal.valueOf(Math.round(random.nextDouble(ACCOUNT_INITIAL_AMOUNT.doubleValue()) * 100) / 100.0);
    }


    private <T> T pickRandom(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }

    private List<Callable<List<Transaction>>> prepareCallableList(List<Account> accounts) {
        List<Callable<List<Transaction>>> result = new ArrayList<>();
        for (int i = 0; i < THREAD_COUNT; i++) {
            result.add(createCallable(accounts));
        }
        return result;
    }

    private Callable<List<Transaction>> createCallable(List<Account> accounts) {
        return () -> {
            List<Transaction> result = new ArrayList<>();
            int i = 0;
            while (i < OPERATION_COUNT_PER_THREAD) {

                Account from = pickRandom(accounts);
                Account to = pickRandom(accounts);
                Currency currency = pickRandom(CURRENCIES);
                BigDecimal amount = nextRandomAmount();
                try {
                    Transaction transaction = transferService.transfer(new TransferRequest(
                            from.getId(), to.getId(),
                            amount, currency));
                    result.add(transaction);
                    i++;
                } catch (BankOperationException ex) {
                    //ignore
                }

                if (from.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                    throw new RuntimeException("Balance of account " + from + " is negative");
                }
            }

            return result;
        };
    }
}