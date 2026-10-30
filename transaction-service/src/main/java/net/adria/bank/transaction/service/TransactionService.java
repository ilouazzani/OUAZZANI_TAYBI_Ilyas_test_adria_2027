package net.adria.bank.transaction.service;

import net.adria.bank.transaction.service.dto.TransactionRequestDTO;
import net.adria.bank.transaction.service.dto.TransactionResponseDTO;

import java.util.List;
import java.util.UUID;

public interface TransactionService {
    TransactionResponseDTO createTransaction(TransactionRequestDTO request);
    TransactionResponseDTO getTransactionById(UUID id);
    List<TransactionResponseDTO> getTransactionsByAccountId(UUID accountId);
    List<TransactionResponseDTO> getAllTransactions();
}
