package engineering.pvl.bank.account.repository;

import engineering.pvl.bank.account.model.Account;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static engineering.pvl.bank.utils.BankAssertions.assertMoneyAmountEquals;
import static engineering.pvl.bank.utils.CurrencyUtils.EUR;
import static engineering.pvl.bank.utils.CurrencyUtils.USD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InitialAccountImporterIT {

    //smoke test to check if initial import imports any accounts
    @Test
    void importData_should_import_some_data() {
        AccountRepository accountRepository = new AccountRepositoryInMemory();
        assertEquals(0, accountRepository.list().size());

        InitialAccountImporter importer = new InitialAccountImporter(accountRepository);
        importer.importData();

        Account account = accountRepository.getById(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        assertEquals("Main account", account.getName());
        assertMoneyAmountEquals(BigDecimal.valueOf(25.15), account.getBalance());
        assertEquals(EUR, account.getCurrency());

        account = accountRepository.getById(UUID.fromString("21111111-1111-1111-1111-111111111111"));
        assertEquals("Internet account", account.getName());
        assertMoneyAmountEquals(BigDecimal.valueOf(121.33), account.getBalance());
        assertEquals(EUR, account.getCurrency());

        account = accountRepository.getById(UUID.fromString("31111111-1111-1111-1111-111111111111"));
        assertEquals("Travel account (North America)", account.getName());
        assertMoneyAmountEquals(BigDecimal.valueOf(0.1), account.getBalance());
        assertEquals(USD, account.getCurrency());

        assertTrue(accountRepository.list().size() > 5);
    }
}