package engineering.pvl.bank.transaction.repository;

import engineering.pvl.bank.transaction.model.Transaction;
import engineering.pvl.bank.utils.DateTimeUtils;
import engineering.pvl.bank.utils.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static engineering.pvl.bank.utils.MoneyUtils.USD;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;

class TransactionRepositoryInMemoryTest {

    private final TransactionRepository repository = new TransactionRepositoryInMemory();

    @Test
    void getById_should_return_created_transaction() {
        Transaction transaction1 = makeTransaction();
        Transaction transaction2 = makeTransaction();

        repository.create(transaction1);
        repository.create(transaction2);

        assertEquals(2, repository.list().size());
        assertSame(transaction1, repository.getById(transaction1.getId()));
        assertSame(transaction2, repository.getById(transaction2.getId()));
    }

    @Test
    void create_transaction_with_existing_id_should_fail() {

        Transaction orig = makeTransaction();
        Transaction cloneWithSameId = makeTransaction(orig.getId());

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
    void getById_of_not_existing_transaction_should_fail() {
        // all try, fail, catch tests should be rewritten using assertThrows
        assertThrows(ResourceNotFoundException.class,
                () -> repository.getById(UUID.randomUUID()));
    }

    @Test
    void list_should_return_all_created_transactions() {
        assertEquals(0, repository.list().size());

        List<Transaction> transactions = List.of(
                repository.create(makeTransaction()),
                repository.create(makeTransaction()),
                repository.create(makeTransaction()),
                repository.create(makeTransaction()));

        assertEquals(4, repository.list().size());
        assertThat(repository.list(), containsInAnyOrder(transactions.toArray()));
    }

    @Test
    void list_should_ignore_newly_created_transactions() {
        assertEquals(0, repository.list().size());

        List<Transaction> transactions = List.of(repository.create(makeTransaction()));

        List<Transaction> actualList = repository.list();
        assertEquals(1, actualList.size());
        assertThat(actualList, containsInAnyOrder(transactions.toArray()));

        repository.create(makeTransaction());
        assertEquals(1, actualList.size());
    }

    private Transaction makeTransaction() {
        return new Transaction(
                UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, USD);
    }

    private Transaction makeTransaction(UUID id) {
        return new Transaction(
                id, UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, USD, DateTimeUtils.nowUTC());
    }

}