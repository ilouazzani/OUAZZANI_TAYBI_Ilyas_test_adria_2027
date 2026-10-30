package net.adria.bank.transaction;

import net.adria.bank.transaction.domain.Transaction;
import net.adria.bank.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransactionServiceApplicationTests {

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void testSaveAndFindTransaction() {
        UUID accountId = UUID.randomUUID();
        Transaction transaction = Transaction.builder()
                .accountId(accountId)
                .amount(BigDecimal.valueOf(150.00))
                .currency("EUR")
                .merchantCategory("SUPERMARKET")
                .country("FR")
                .createdAt(Instant.now())
                .build();

        Transaction saved = transactionRepository.save(transaction);
        assertNotNull(saved.getId());
        assertEquals(accountId, saved.getAccountId());
        assertEquals(BigDecimal.valueOf(150.00), saved.getAmount());
    }
}
