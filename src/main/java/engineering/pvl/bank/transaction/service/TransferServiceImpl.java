package engineering.pvl.bank.transaction.service;

import engineering.pvl.bank.account.model.Account;
import engineering.pvl.bank.account.repository.AccountRepository;
import engineering.pvl.bank.transaction.model.Transaction;
import engineering.pvl.bank.transaction.repository.TransactionRepository;
import engineering.pvl.bank.utils.BankOperationException;
import engineering.pvl.bank.utils.MoneyUtils;

import java.math.BigDecimal;
import java.util.Currency;

public class TransferServiceImpl implements TransferService {

    private static final BigDecimal MAX_ACCOUNT_BALANCE = BigDecimal.valueOf(10_000_000_000_000.0);
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransferServiceImpl(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Transaction transfer(TransferRequest request) {

        validateRequest(request);

        Account fromAccount = accountRepository.getById(request.getFromAccountId());
        Account toAccount = accountRepository.getById(request.getToAccountId());
        BigDecimal amount = MoneyUtils.normalize(request.getAmount());
        Currency currency = request.getCurrency();

        updateAccounts(fromAccount, toAccount, amount, currency);

        Transaction transaction = new Transaction(
                fromAccount.getId(), toAccount.getId(),
                amount, currency);
        return transactionRepository.create(transaction);
    }


    private void validateRequest(TransferRequest request) {
        if (request.getFromAccountId() == null) {
            throw new BankOperationException("Account from is required");
        }
        if (request.getToAccountId() == null) {
            throw new BankOperationException("Account to is required");
        }
        BigDecimal amount = request.getAmount();
        if (amount == null) {
            throw new BankOperationException("Amount is required");
        }
        if (request.getCurrency() == null) {
            throw new BankOperationException("Currency is required");
        }

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BankOperationException("Amount must be greater then zero");
        }

        if (request.getFromAccountId().equals(request.getToAccountId())) {
            throw new BankOperationException("Account to same account is forbidden");
        }
    }

    private void updateAccounts(Account fromAccount, Account toAccount, BigDecimal amount, Currency currency) {
        //to prevent deadlock, first lock account with smaller id first
        Account first;
        Account second;
        if (fromAccount.getId().compareTo(toAccount.getId()) > 0) {
            first = toAccount;
            second = fromAccount;
        } else {
            first = fromAccount;
            second = toAccount;
        }

        //locking only two accounts (operations with all other account still can be run concurrently)
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (first) {
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (second) {
                validateTransfer(fromAccount, toAccount, amount, currency);
                accountRepository.subtractAmount(fromAccount, amount);
                accountRepository.addAmount(toAccount, amount);
            }
        }
    }

    private void validateTransfer(Account from, Account to, BigDecimal amount, Currency currency) {
        if (!from.getCurrency().equals(currency)) {
            throw new BankOperationException("Currency conversion is not possible");
        }
        if (!to.getCurrency().equals(currency)) {
            throw new BankOperationException("Currency conversion is not possible");
        }
        if (from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }
        if (to.getBalance().add(amount).compareTo(MAX_ACCOUNT_BALANCE) >= 0) {
            throw new BankOperationException("Max amount in one account should be less then " + MAX_ACCOUNT_BALANCE);
        }

    }


}
