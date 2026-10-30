package net.adria.bank.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.adria.bank.account.domain.Account;
import net.adria.bank.account.domain.AccountStatus;
import net.adria.bank.account.repository.AccountRepository;
import net.adria.bank.account.service.dto.AccountRequestDTO;
import net.adria.bank.account.service.dto.AccountResponseDTO;
import net.adria.bank.account.service.dto.TransferRequestDTO;
import net.adria.bank.account.service.exception.AccountInactiveException;
import net.adria.bank.account.service.exception.AccountNotFoundException;
import net.adria.bank.account.service.exception.CurrencyMismatchException;
import net.adria.bank.account.service.exception.InsufficientBalanceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public AccountResponseDTO createAccount(AccountRequestDTO requestDTO) {
        Account account = Account.builder()
                .customerReference(requestDTO.getCustomerReference())
                .status(requestDTO.getStatus() != null ? requestDTO.getStatus() : AccountStatus.ACTIVE)
                .currency(requestDTO.getCurrency().toUpperCase())
                .balance(requestDTO.getBalance())
                .build();
        Account saved = accountRepository.save(account);
        log.info("Created account {} for customer {}", saved.getId(), saved.getCustomerReference());
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponseDTO getAccountById(UUID id) {
        Account account = findAccountOrThrow(id);
        return mapToResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponseDTO> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AccountResponseDTO updateBalance(UUID id, double amount) {
        Account account = findAccountOrThrow(id);
        validateAccountIsActive(account);

        double newBalance = account.getBalance() + amount;
        if (newBalance < 0) {
            throw new InsufficientBalanceException("Solde insuffisant pour le compte " + id + ". Solde actuel: " + account.getBalance());
        }

        account.setBalance(newBalance);
        Account updated = accountRepository.save(account);
        log.info("Updated account {} balance with delta {}: new balance = {}", id, amount, newBalance);
        return mapToResponse(updated);
    }

    @Override
    public AccountResponseDTO updateStatus(UUID id, AccountStatus status) {
        Account account = findAccountOrThrow(id);
        account.setStatus(status);
        Account updated = accountRepository.save(account);
        log.info("Updated account {} status to {}", id, status);
        return mapToResponse(updated);
    }

    @Override
    public void transfer(TransferRequestDTO request) {
        if (request.getSourceAccountId().equals(request.getDestinationAccountId())) {
            throw new IllegalArgumentException("Le compte source et destination doivent être distincts.");
        }

        Account source = findAccountOrThrow(request.getSourceAccountId());
        Account destination = findAccountOrThrow(request.getDestinationAccountId());

        validateAccountIsActive(source);
        validateAccountIsActive(destination);

        if (!source.getCurrency().equalsIgnoreCase(destination.getCurrency())) {
            throw new CurrencyMismatchException("Incompatibilité de devises pour le virement: " 
                    + source.getCurrency() + " vs " + destination.getCurrency());
        }

        if (source.getBalance() < request.getAmount()) {
            throw new InsufficientBalanceException("Solde insuffisant sur le compte source " 
                    + source.getId() + ". Solde: " + source.getBalance() + ", Montant requis: " + request.getAmount());
        }

        source.setBalance(source.getBalance() - request.getAmount());
        destination.setBalance(destination.getBalance() + request.getAmount());

        accountRepository.save(source);
        accountRepository.save(destination);

        log.info("Transferred {} {} from {} to {}", request.getAmount(), source.getCurrency(), source.getId(), destination.getId());
    }

    private Account findAccountOrThrow(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Compte bancaire introuvable avec l'ID: " + id));
    }

    private void validateAccountIsActive(Account account) {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountInactiveException("Le compte bancaire " + account.getId() + " est bloqué ou inactif.");
        }
    }

    private AccountResponseDTO mapToResponse(Account account) {
        return AccountResponseDTO.builder()
                .id(account.getId())
                .customerReference(account.getCustomerReference())
                .status(account.getStatus())
                .currency(account.getCurrency())
                .balance(account.getBalance())
                .build();
    }
}
