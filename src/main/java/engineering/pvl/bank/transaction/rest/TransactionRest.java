package engineering.pvl.bank.transaction.rest;

import engineering.pvl.bank.transaction.model.Transaction;
import engineering.pvl.bank.transaction.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

public class TransactionRest {
    private static final Logger log = LoggerFactory.getLogger(TransactionRest.class);

    private final TransactionRepository transactionRepository;

    public TransactionRest(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction getById(UUID id) {
        log.debug("Getting transaction by id: {}", id);
        Transaction transaction = transactionRepository.getById(id);
        log.trace("Transaction by id: {}", transaction);
        return transaction;
    }

    public List<Transaction> list() {
        log.debug("Listing all accounts");
        return transactionRepository.list();
    }

}
