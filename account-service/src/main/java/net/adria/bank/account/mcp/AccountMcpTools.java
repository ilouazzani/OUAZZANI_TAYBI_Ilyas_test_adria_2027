package net.adria.bank.account.mcp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.adria.bank.account.domain.AccountStatus;
import net.adria.bank.account.service.AccountService;
import net.adria.bank.account.service.dto.AccountRequestDTO;
import net.adria.bank.account.service.dto.AccountResponseDTO;
import net.adria.bank.account.service.dto.TransferRequestDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Connecteur MCP (Model Context Protocol) pour account-service.
 * Expose les capacités métier sous forme d'outils invocables par un agent LLM.
 * Délègue directement à AccountService selon les exigences d'architecture.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AccountMcpTools {

    private final AccountService accountService;

    public AccountResponseDTO getAccount(UUID accountId) {
        log.info("[MCP Tool] Consultation du compte {}", accountId);
        return accountService.getAccountById(accountId);
    }

    public List<AccountResponseDTO> listAccounts() {
        log.info("[MCP Tool] Liste des comptes bancaires");
        return accountService.getAllAccounts();
    }

    public AccountResponseDTO createAccount(String customerReference, String currency, double initialBalance) {
        log.info("[MCP Tool] Création de compte pour {}", customerReference);
        return accountService.createAccount(AccountRequestDTO.builder()
                .customerReference(customerReference)
                .currency(currency)
                .balance(initialBalance)
                .status(AccountStatus.ACTIVE)
                .build());
    }

    public String transferMoney(UUID sourceAccountId, UUID destinationAccountId, double amount) {
        log.info("[MCP Tool] Virement de {} depuis {} vers {}", amount, sourceAccountId, destinationAccountId);
        accountService.transfer(TransferRequestDTO.builder()
                .sourceAccountId(sourceAccountId)
                .destinationAccountId(destinationAccountId)
                .amount(amount)
                .build());
        return "Virement de " + amount + " effectué avec succès de " + sourceAccountId + " vers " + destinationAccountId;
    }
}
