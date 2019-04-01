package engineering.pvl.bank.transaction.repository;

import engineering.pvl.bank.transaction.model.Transaction;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository {
    Transaction create(Transaction transaction);

    Transaction getById(UUID id);

    List<Transaction> list();
}
