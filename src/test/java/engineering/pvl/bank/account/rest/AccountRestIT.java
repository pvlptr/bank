package engineering.pvl.bank.account.rest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import engineering.pvl.bank.ServiceRegistry;
import engineering.pvl.bank.account.model.Account;
import engineering.pvl.bank.account.repository.AccountRepository;
import engineering.pvl.bank.account.service.AccountCreateRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import engineering.pvl.bank.utils.RestApiTestServer;

import java.math.BigDecimal;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.UUID;

import static engineering.pvl.bank.RestApiServer.ErrorResponse;
import static engineering.pvl.bank.utils.BankAssertions.assertMoneyAmountEquals;
import static engineering.pvl.bank.utils.CurrencyUtils.EUR;
import static engineering.pvl.bank.utils.CurrencyUtils.USD;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static engineering.pvl.bank.utils.RestAssertions.assertResponse;

class AccountRestIT {

    private RestApiTestServer testServer;
    private AccountRepository accountRepository;
    private Gson gson;


    @BeforeEach
    void setUp() {
        ServiceRegistry serviceRegistry = new ServiceRegistry();
        testServer = new RestApiTestServer(serviceRegistry, "accounts");
        testServer.start();

        accountRepository = serviceRegistry.getAccountRepository();
        gson = serviceRegistry.getGson();
    }

    @AfterEach
    void tearDown() {
        testServer.stop();
    }

    @Test
    void getSingleAccount() {
        Account account1 = accountRepository.create(new Account(UUID.randomUUID(), "Account 1", BigDecimal.TEN, USD));
        Account account2 = accountRepository.create(new Account(UUID.randomUUID(), "Account 2", BigDecimal.ONE, EUR));

        HttpResponse<String> response = testServer.get("/" + account1.getId());
        assertResponse(gson, 200, account1, response);

        response = testServer.get("/" + account2.getId());
        assertResponse(gson, 200, account2, response);
    }

    @Test
    void getAllAccounts() {
        HttpResponse<String> response = testServer.get();
        assertResponse(gson, 200, List.of(), response);

        Account account1 = accountRepository.create(new Account(UUID.randomUUID(), "Account 1", BigDecimal.TEN, USD));
        Account account2 = accountRepository.create(new Account(UUID.randomUUID(), "Account 2", BigDecimal.ONE, EUR));

        response = testServer.get("");
        assertEquals(200, response.statusCode());
        assertThat(accountListFromBody(response), containsInAnyOrder(account1, account2));
    }

    private List<Account> accountListFromBody(HttpResponse<String> response) {
        return gson.fromJson(response.body(),
                new TypeToken<List<Account>>() {
                }.getType());
    }

    @Test
    void getNotExistingAccount_resourceNotFound() {
        HttpResponse<String> response = testServer.get("/" + UUID.randomUUID());
        assertResponse(gson, 404, new ErrorResponse("Resource not found"), response);
    }

    @Test
    void getByInvalidUUID_invalidOperations() {
        HttpResponse<String> response = testServer.get("/123");
        assertResponse(gson, 400, new ErrorResponse("Invalid operation"), response);
    }

    @Test
    void createAccount() {
        AccountCreateRequest request = new AccountCreateRequest("Account 1", USD);

        HttpResponse<String> response = testServer.post(request);
        Account createdAccount = accountRepository.list().get(0);
        assertResponse(gson, 200, createdAccount, response);

        assertEquals("Account 1", createdAccount.getName());
        assertMoneyAmountEquals(BigDecimal.ZERO, createdAccount.getBalance());
        assertEquals(USD, createdAccount.getCurrency());
    }


}