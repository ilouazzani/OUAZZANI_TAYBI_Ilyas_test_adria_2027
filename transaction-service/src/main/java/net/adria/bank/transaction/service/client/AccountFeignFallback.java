package net.adria.bank.transaction.service.client;

import lombok.extern.slf4j.Slf4j;
import net.adria.bank.transaction.service.dto.AccountBalanceUpdateRequestDTO;
import net.adria.bank.transaction.service.dto.AccountDTO;
import net.adria.bank.transaction.service.exception.ServiceUnavailableException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class AccountFeignFallback implements AccountFeignClient {

    @Override
    public AccountDTO getAccountById(UUID id) {
        log.error("Fallback déclenché pour getAccountById sur account-service pour l'ID: {}", id);
        throw new ServiceUnavailableException("Le service de gestion des comptes (account-service) est actuellement indisponible. Veuillez réessayer plus tard.");
    }

    @Override
    public AccountDTO updateBalance(UUID id, AccountBalanceUpdateRequestDTO request) {
        log.error("Fallback déclenché pour updateBalance sur account-service pour l'ID: {}", id);
        throw new ServiceUnavailableException("Impossible de mettre à jour le solde du compte. Le service account-service est indisponible.");
    }
}
