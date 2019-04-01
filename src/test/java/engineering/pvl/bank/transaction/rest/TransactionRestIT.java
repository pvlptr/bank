package engineering.pvl.bank.transaction.rest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import engineering.pvl.bank.ServiceRegistry;
import engineering.pvl.bank.account.model.Account;
import engineering.pvl.bank.account.repository.AccountRepository;
import engineering.pvl.bank.transaction.model.Transaction;
import engineering.pvl.bank.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import engineering.pvl.bank.utils.RestApiTestServer;

import java.math.BigDecimal;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.UUID;

import static engineering.pvl.bank.RestApiServer.ErrorResponse;
import static engineering.pvl.bank.utils.CurrencyUtils.EUR;
import static engineering.pvl.bank.utils.CurrencyUtils.USD;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static engineering.pvl.bank.utils.RestAssertions.assertResponse;

class TransactionRestIT {

    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;

    private RestApiTestServer testServer;

    private Gson gson;

    private Account account1;
    private Account account2;

    @BeforeEach
    void setUp() {
        ServiceRegistry serviceRegistry = new ServiceRegistry();

        accountRepository = serviceRegistry.getAccountRepository();
        transactionRepository = serviceRegistry.getTransactionRepository();

        testServer = new RestApiTestServer(serviceRegistry, "transactions");
        testServer.start();

        gson = serviceRegistry.getGson();

        account1 = accountRepository.create(new Account("Account 1", BigDecimal.valueOf(100), EUR));
        account2 = accountRepository.create(new Account("Account 2", BigDecimal.valueOf(100), EUR));
    }

    @AfterEach
    void tearDown() {
        testServer.stop();
    }

    @Test
    void getSingleTransaction() {

        Transaction transaction1 = transactionRepository.create(new Transaction(
                account1.getId(), account2.getId(),
                BigDecimal.TEN, USD));

        HttpResponse<String> response = testServer.get("/" + transaction1.getId());
        assertResponse(gson, 200, transaction1, response);

        Transaction transaction2 = transactionRepository.create(new Transaction(
                account2.getId(), account1.getId(),
                BigDecimal.TEN, EUR));

        response = testServer.get("/" + transaction2.getId());
        assertResponse(gson, 200, transaction2, response);
    }

    @Test
    void getAllTransactions() {
        HttpResponse<String> response = testServer.get();
        assertResponse(gson, 200, List.of(), response);

        Transaction transaction1 = transactionRepository.create(new Transaction(
                account1.getId(), account2.getId(),
                BigDecimal.TEN, USD));
        Transaction transaction2 = transactionRepository.create(new Transaction(
                account2.getId(), account1.getId(),
                BigDecimal.TEN, EUR));

        response = testServer.get();
        assertEquals(200, response.statusCode());
        assertThat(transactionListFromBody(response), containsInAnyOrder(transaction1, transaction2));
    }

    private List<Account> transactionListFromBody(HttpResponse<String> response) {
        return gson.fromJson(response.body(),
                new TypeToken<List<Transaction>>() {
                }.getType());
    }

    @Test
    void getNotExistingTransaction_resourceNotFound() {
        HttpResponse<String> response = testServer.get("/" + UUID.randomUUID());
        assertResponse(gson, 404, new ErrorResponse("Resource not found"), response);
    }


}