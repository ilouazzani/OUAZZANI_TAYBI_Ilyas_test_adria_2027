package net.adria.bank.transaction.web.graphql;

import lombok.RequiredArgsConstructor;
import net.adria.bank.transaction.service.TransactionService;
import net.adria.bank.transaction.service.dto.TransactionRequestDTO;
import net.adria.bank.transaction.service.dto.TransactionResponseDTO;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class TransactionGraphQLController {

    private final TransactionService transactionService;

    @QueryMapping
    public List<TransactionResponseDTO> transactions() {
        return transactionService.getAllTransactions();
    }

    @QueryMapping
    public TransactionResponseDTO transactionById(@Argument UUID id) {
        return transactionService.getTransactionById(id);
    }

    @QueryMapping
    public List<TransactionResponseDTO> transactionsByAccount(@Argument UUID accountId) {
        return transactionService.getTransactionsByAccountId(accountId);
    }

    @MutationMapping
    public TransactionResponseDTO createTransaction(@Argument TransactionInput request) {
        TransactionRequestDTO dto = TransactionRequestDTO.builder()
                .accountId(request.accountId())
                .amount(BigDecimal.valueOf(request.amount()))
                .currency(request.currency())
                .merchantCategory(request.merchantCategory())
                .country(request.country())
                .build();
        return transactionService.createTransaction(dto);
    }

    public record TransactionInput(UUID accountId, double amount, String currency, String merchantCategory, String country) {}
}
