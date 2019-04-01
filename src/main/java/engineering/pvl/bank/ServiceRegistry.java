package engineering.pvl.bank;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import engineering.pvl.bank.account.repository.AccountRepository;
import engineering.pvl.bank.account.repository.AccountRepositoryInMemory;
import engineering.pvl.bank.account.repository.InitialAccountImporter;
import engineering.pvl.bank.account.service.AccountService;
import engineering.pvl.bank.account.service.AccountServiceImpl;
import engineering.pvl.bank.transaction.repository.TransactionRepository;
import engineering.pvl.bank.transaction.repository.TransactionRepositoryInMemory;
import engineering.pvl.bank.transaction.service.TransferService;
import engineering.pvl.bank.transaction.service.TransferServiceImpl;
import engineering.pvl.bank.utils.LocalDateTimeTypeAdapter;

import java.time.LocalDateTime;

public class ServiceRegistry {

    private final Gson gson;

    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final TransactionRepository transactionRepository;
    private final TransferService transferService;
    private final InitialAccountImporter accountImporter;


    public ServiceRegistry() {
        //utils
        gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter()).create();

        //accounts
        accountRepository = new AccountRepositoryInMemory();
        accountService = new AccountServiceImpl(accountRepository);
        accountImporter = new InitialAccountImporter(accountRepository);

        //transactions
        transactionRepository = new TransactionRepositoryInMemory();
        transferService = new TransferServiceImpl(accountRepository, transactionRepository);
    }

    public Gson getGson() {
        return gson;
    }

    public AccountRepository getAccountRepository() {
        return accountRepository;
    }

    public AccountService getAccountService() {
        return accountService;
    }

    public TransactionRepository getTransactionRepository() {
        return transactionRepository;
    }

    public TransferService getTransferService() {
        return transferService;
    }

    public InitialAccountImporter getAccountImporter() {
        return accountImporter;
    }
}
