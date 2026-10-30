package net.adria.bank.account.service;

import net.adria.bank.account.domain.AccountStatus;
import net.adria.bank.account.service.dto.AccountRequestDTO;
import net.adria.bank.account.service.dto.AccountResponseDTO;
import net.adria.bank.account.service.dto.TransferRequestDTO;

import java.util.List;
import java.util.UUID;

public interface AccountService {
    AccountResponseDTO createAccount(AccountRequestDTO requestDTO);
    AccountResponseDTO getAccountById(UUID id);
    List<AccountResponseDTO> getAllAccounts();
    AccountResponseDTO updateBalance(UUID id, double amount);
    AccountResponseDTO updateStatus(UUID id, AccountStatus status);
    void transfer(TransferRequestDTO transferRequest);
}
