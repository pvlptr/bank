package engineering.pvl.bank.transaction.repository;

import engineering.pvl.bank.transaction.model.Transaction;
import engineering.pvl.bank.utils.ResourceNotFoundException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionRepositoryInMemory implements TransactionRepository {

    private final Map<UUID, Transaction> storageMap = new ConcurrentHashMap<>();

    @Override
    public Transaction create(Transaction transaction) {
        Transaction prev = storageMap.putIfAbsent(transaction.getId(), transaction);
        if (prev != null) {
            //service layer should prevent creating existing transactions
            throw new RuntimeException("Transaction with id " + prev.getId() + " already exists");
        }
        return transaction;
    }

    @Override
    public Transaction getById(UUID id) {
        return getByIdOptional(id).orElseThrow(() ->
                new ResourceNotFoundException("Transaction not found by id " + id));
    }

    private Optional<Transaction> getByIdOptional(UUID id) {
        return Optional.ofNullable(storageMap.get(id));
    }

    @Override
    public List<Transaction> list() {
        return new ArrayList<>(storageMap.values());
    }

}
