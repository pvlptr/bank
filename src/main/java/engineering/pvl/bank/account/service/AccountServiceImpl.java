package engineering.pvl.bank.account.service;

import engineering.pvl.bank.account.model.Account;
import engineering.pvl.bank.account.repository.AccountRepository;
import engineering.pvl.bank.utils.BankOperationException;
import spark.utils.StringUtils;

public final class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    @Override
    public Account create(AccountCreateRequest request) {
        validateRequest(request);
        Account account = new Account(request.getName(), request.getCurrency());
        return accountRepository.create(account);
    }

    private void validateRequest(AccountCreateRequest request) {
        if (StringUtils.isBlank(request.getName())) {
            throw new BankOperationException("Account name is required");
        }
        if (request.getCurrency() == null) {
            throw new BankOperationException("Currency is required");
        }

    }

}
