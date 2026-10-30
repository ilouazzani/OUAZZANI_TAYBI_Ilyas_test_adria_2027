package net.adria.bank.account;

import net.adria.bank.account.domain.AccountStatus;
import net.adria.bank.account.service.AccountService;
import net.adria.bank.account.service.dto.AccountRequestDTO;
import net.adria.bank.account.service.dto.AccountResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AccountServiceApplicationTests {

    @Autowired
    private AccountService accountService;

    @Test
    void testCreateAndRetrieveAccount() {
        AccountResponseDTO created = accountService.createAccount(AccountRequestDTO.builder()
                .customerReference("TEST-USER")
                .currency("EUR")
                .balance(1000.0)
                .status(AccountStatus.ACTIVE)
                .build());

        assertNotNull(created.getId());
        assertEquals("TEST-USER", created.getCustomerReference());
        assertEquals(1000.0, created.getBalance());

        AccountResponseDTO retrieved = accountService.getAccountById(created.getId());
        assertEquals(created.getId(), retrieved.getId());
    }

    @Test
    void testUpdateBalance() {
        AccountResponseDTO created = accountService.createAccount(AccountRequestDTO.builder()
                .customerReference("BAL-TEST")
                .currency("EUR")
                .balance(500.0)
                .status(AccountStatus.ACTIVE)
                .build());

        AccountResponseDTO updated = accountService.updateBalance(created.getId(), -200.0);
        assertEquals(300.0, updated.getBalance());
    }
}
