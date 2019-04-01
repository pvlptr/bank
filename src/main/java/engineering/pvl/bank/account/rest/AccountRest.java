package engineering.pvl.bank.account.rest;

import com.google.gson.Gson;
import engineering.pvl.bank.account.model.Account;
import engineering.pvl.bank.account.repository.AccountRepository;
import engineering.pvl.bank.account.service.AccountCreateRequest;
import engineering.pvl.bank.account.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;

import java.util.List;
import java.util.UUID;

public class AccountRest {

    private static final Logger log = LoggerFactory.getLogger(AccountRest.class);

    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final Gson gson;

    public AccountRest(AccountRepository accountRepository,
                       AccountService accountService,
                       Gson gson) {
        this.accountRepository = accountRepository;
        this.accountService = accountService;
        this.gson = gson;
    }

    public List<Account> listAll() {
        log.debug("Listing all accounts");
        return accountRepository.list();
    }

    public Account getById(UUID id) {
        log.debug("Getting account by id: {}", id);
        Account account = accountRepository.getById(id);
        log.trace("Account by id: {}", account);
        return account;
    }

    public Account create(Request request) {
        log.debug("Creating account", request);
        Account account = accountService.create(gson.fromJson(request.body(), AccountCreateRequest.class));
        log.trace("Created account: {}", account);
        return account;
    }

}
