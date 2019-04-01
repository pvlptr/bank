package engineering.pvl.bank.account.service;

import engineering.pvl.bank.account.model.Account;

public interface AccountService {
    Account create(AccountCreateRequest request);
}
