package net.adria.bank.transaction.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.adria.bank.transaction.domain.Transaction;
import net.adria.bank.transaction.messaging.TransactionEventPublisher;
import net.adria.bank.transaction.repository.TransactionRepository;
import net.adria.bank.transaction.service.client.AccountFeignClient;
import net.adria.bank.transaction.service.dto.*;
import net.adria.bank.transaction.service.exception.ServiceUnavailableException;
import net.adria.bank.transaction.service.exception.TransactionExceedsLimitException;
import net.adria.bank.transaction.service.exception.TransactionValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountFeignClient accountFeignClient;
    private final TransactionEventPublisher eventPublisher;

    @Value("${bank.transaction.max-amount:1000.0}")
    private double maxTransactionAmount;

    @Override
    @Transactional
    public TransactionResponseDTO createTransaction(TransactionRequestDTO request) {
        log.info("Traitement de la transaction pour le compte {} d'un montant de {} {}",
                request.getAccountId(), request.getAmount(), request.getCurrency());

        // 1. Règle métier : Montant strictement positif et respect du plafond
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransactionValidationException("Le montant de la transaction doit être strictement positif.");
        }
        if (request.getAmount().doubleValue() > maxTransactionAmount) {
            throw new TransactionExceedsLimitException(
                    "Le montant (" + request.getAmount() + ") dépasse le plafond configuré de " + maxTransactionAmount);
        }

        // 2. Règle métier : Vérification du compte distant via OpenFeign + Resilience4J
        AccountDTO account = fetchAccountWithResilience(request.getAccountId());
        if (account == null) {
            throw new TransactionValidationException("Compte bancaire non trouvé: " + request.getAccountId());
        }

        // 3. Règle métier : Le compte doit être actif
        if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
            throw new TransactionValidationException("Le compte " + account.getId() + " n'est pas actif (statut: " + account.getStatus() + ").");
        }

        // 4. Règle métier : La devise de la transaction doit correspondre à celle du compte
        if (!account.getCurrency().equalsIgnoreCase(request.getCurrency())) {
            throw new TransactionValidationException("La devise de la transaction (" + request.getCurrency() 
                    + ") ne correspond pas à la devise du compte (" + account.getCurrency() + ").");
        }

        // 5. Règle métier : Consultation du solde et vérification de la provision suffisante
        if (account.getBalance() < request.getAmount().doubleValue()) {
            throw new TransactionValidationException("Solde insuffisant (" + account.getBalance() 
                    + " " + account.getCurrency() + ") pour effectuer ce débit de " + request.getAmount());
        }

        // 6. Mise à jour du solde du compte dans account-service (débit)
        updateAccountBalanceWithResilience(account.getId(), -request.getAmount().doubleValue());

        // 7. Persistance de la transaction
        Transaction transaction = Transaction.builder()
                .accountId(request.getAccountId())
                .amount(request.getAmount())
                .currency(request.getCurrency().toUpperCase())
                .merchantCategory(request.getMerchantCategory())
                .country(request.getCountry().toUpperCase())
                .createdAt(Instant.now())
                .build();

        Transaction saved = transactionRepository.save(transaction);
        log.info("Transaction {} persistée avec succès.", saved.getId());

        // 8. Communication Asynchrone : Publication de l'événement Kafka
        BankTransactionEvent event = BankTransactionEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType("TransactionCreated")
                .transactionId(saved.getId())
                .accountId(saved.getAccountId())
                .amount(saved.getAmount())
                .currency(saved.getCurrency())
                .occurredAt(saved.getCreatedAt())
                .build();

        eventPublisher.publishTransactionCreated(event);

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponseDTO getTransactionById(UUID id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionValidationException("Transaction introuvable avec l'ID: " + id));
        return mapToResponse(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getTransactionsByAccountId(UUID accountId) {
        return transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @CircuitBreaker(name = "accountServiceCB", fallbackMethod = "fetchAccountFallback")
    @Retry(name = "accountServiceRetry")
    public AccountDTO fetchAccountWithResilience(UUID accountId) {
        return accountFeignClient.getAccountById(accountId);
    }

    public AccountDTO fetchAccountFallback(UUID accountId, Throwable t) {
        log.error("Resilience4J Fallback: impossible de joindre account-service pour le compte {}. Cause: {}", 
                accountId, t.getMessage());
        throw new ServiceUnavailableException("Le service compte est temporairement indisponible (Resilience4J CircuitBreaker). " + t.getMessage());
    }

    @CircuitBreaker(name = "accountServiceCB", fallbackMethod = "updateBalanceFallback")
    public void updateAccountBalanceWithResilience(UUID accountId, double deltaAmount) {
        accountFeignClient.updateBalance(accountId, new AccountBalanceUpdateRequestDTO(deltaAmount));
    }

    public void updateBalanceFallback(UUID accountId, double deltaAmount, Throwable t) {
        log.error("Resilience4J Fallback: échec de mise à jour du solde du compte {}. Cause: {}", accountId, t.getMessage());
        throw new ServiceUnavailableException("Impossible de mettre à jour le solde sur le compte distant (Circuit Breaker actif).");
    }

    private TransactionResponseDTO mapToResponse(Transaction transaction) {
        return TransactionResponseDTO.builder()
                .id(transaction.getId())
                .accountId(transaction.getAccountId())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .merchantCategory(transaction.getMerchantCategory())
                .country(transaction.getCountry())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
