package engineering.pvl.bank.account.repository;

import engineering.pvl.bank.account.model.Account;
import engineering.pvl.bank.utils.BankOperationException;
import engineering.pvl.bank.utils.ResourceNotFoundException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AccountRepositoryInMemory implements AccountRepository {

    private final Map<UUID, Account> storageMap = new ConcurrentHashMap<>();

    @Override
    public Account create(Account account) {
        Account prev = storageMap.putIfAbsent(account.getId(), account);
        if (prev != null) {
            //service layer should not allow creating existing account
            throw new RuntimeException("Account with id " + prev.getId() + " already exists");
        }
        normalizeBalance(account);
        return account;
    }

    @Override
    public Account getById(UUID id) {
        return getByIdOptional(id).orElseThrow(() -> new ResourceNotFoundException("Account not found by " + id));
    }

    private Optional<Account> getByIdOptional(UUID id) {
        return Optional.ofNullable(storageMap.get(id));
    }

    @Override
    public List<Account> list() {
        return new ArrayList<>(storageMap.values());
    }

    @Override
    public Account addAmount(Account account, BigDecimal amount) {
        //makes operation read and update atomic
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (account) {
            account.setBalance(account.getBalance().add(amount));
            normalizeBalance(account);
            return account;
        }
    }

    @Override
    public Account subtractAmount(Account account, BigDecimal amount) {
        //makes operation read and update atomic
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (account) {
            account.setBalance(account.getBalance().subtract(amount));
            normalizeBalance(account);
            return account;
        }
    }

    private void normalizeBalance(Account account) {
        BigDecimal balance = account.getBalance().stripTrailingZeros();
        if (balance.scale() > 2) {
            throw new BankOperationException("Maximum precision is two digits to the right of the decimal point");
        }
        balance = balance.setScale(2, RoundingMode.UNNECESSARY);
        account.setBalance(balance);
    }
}
