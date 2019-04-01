package engineering.pvl.bank.transaction.rest;

import com.google.gson.Gson;
import engineering.pvl.bank.ServiceRegistry;
import engineering.pvl.bank.account.model.Account;
import engineering.pvl.bank.account.repository.AccountRepository;
import engineering.pvl.bank.transaction.model.Transaction;
import engineering.pvl.bank.transaction.repository.TransactionRepository;
import engineering.pvl.bank.transaction.service.TransferRequest;
import engineering.pvl.bank.utils.RestApiTestServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.net.http.HttpResponse;

import static engineering.pvl.bank.RestApiServer.ErrorResponse;
import static engineering.pvl.bank.utils.BankAssertions.assertAmountEquals;
import static engineering.pvl.bank.utils.MoneyUtils.EUR;
import static engineering.pvl.bank.utils.MoneyUtils.USD;
import static engineering.pvl.bank.utils.RestAssertions.assertResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TransferRestIT {

    private TransactionRepository transactionRepository;
    private RestApiTestServer testServer;
    private Gson gson;

    private Account account1;
    private Account account2;

    @BeforeEach
    void setUp() {
        ServiceRegistry serviceRegistry = new ServiceRegistry();

        AccountRepository accountRepository = serviceRegistry.getAccountRepository();
        transactionRepository = serviceRegistry.getTransactionRepository();

        testServer = new RestApiTestServer(serviceRegistry, "transfers");
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
    void transfer() {
        TransferRequest request = new TransferRequest(
                account1.getId(), account2.getId(),
                BigDecimal.TEN, EUR);

        HttpResponse<String> response = testServer.post(request);
        Transaction createdTransaction = transactionRepository.list().get(0);
        assertResponse(gson, 200, createdTransaction, response);

        assertNotNull(createdTransaction.getId());
        assertAmountEquals(10, createdTransaction.getAmount());
        assertEquals(EUR, createdTransaction.getCurrency());
        assertEquals(account1.getId(), createdTransaction.getFromAccountId());
        assertEquals(account2.getId(), createdTransaction.getToAccountId());
        assertNotNull(createdTransaction.getCreated());

        assertAmountEquals(90, account1.getBalance());
        assertAmountEquals(110, account2.getBalance());
    }

    @Test
    void transfer_insufficientFunds() {
        TransferRequest request = new TransferRequest(
                account1.getId(), account2.getId(),
                BigDecimal.valueOf(101), EUR);

        HttpResponse<String> response = testServer.post(request);
        assertResponse(gson, 400, new ErrorResponse("Insufficient funds"), response);
    }

    @Test
    void transfer_invalidCurrency() {
        TransferRequest request = new TransferRequest(
                account1.getId(), account2.getId(),
                BigDecimal.valueOf(1), USD);

        HttpResponse<String> response = testServer.post(request);
        assertResponse(gson, 400, new ErrorResponse("Invalid operation"), response);
    }

    @Test
    void transfer_invalidHttpMethod() {
        HttpResponse<String> response = testServer.get();
        assertResponse(gson, 404, new ErrorResponse("Resource not found"), response);
    }
}