package net.adria.bank.transaction.mcp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.adria.bank.transaction.service.TransactionService;
import net.adria.bank.transaction.service.dto.TransactionRequestDTO;
import net.adria.bank.transaction.service.dto.TransactionResponseDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Connecteur MCP (Model Context Protocol) pour transaction-service.
 * Expose les capacités de consultation et d'enregistrement aux agents LLM.
 * Délègue à TransactionService selon le pattern architectural.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionMcpTools {

    private final TransactionService transactionService;

    public List<TransactionResponseDTO> getTransactionsForAccount(UUID accountId) {
        log.info("[MCP Tool] Consultation de l'historique des transactions pour le compte {}", accountId);
        return transactionService.getTransactionsByAccountId(accountId);
    }

    public TransactionResponseDTO executePayment(UUID accountId, double amount, String currency, String merchantCategory, String country) {
        log.info("[MCP Tool] Exécution paiement de {} {} chez {}", amount, currency, merchantCategory);
        return transactionService.createTransaction(TransactionRequestDTO.builder()
                .accountId(accountId)
                .amount(BigDecimal.valueOf(amount))
                .currency(currency)
                .merchantCategory(merchantCategory)
                .country(country)
                .build());
    }
}
