package net.adria.bank.transaction.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.adria.bank.transaction.service.TransactionService;
import net.adria.bank.transaction.service.dto.TransactionRequestDTO;
import net.adria.bank.transaction.service.dto.TransactionResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Enregistrement et consultation des transactions bancaires")
public class TransactionRestController {

    private final TransactionService transactionService;

    @PostMapping
    @Operation(summary = "Enregistrer une nouvelle transaction bancaire")
    public ResponseEntity<TransactionResponseDTO> createTransaction(@Valid @RequestBody TransactionRequestDTO request) {
        return new ResponseEntity<>(transactionService.createTransaction(request), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Lister toutes les transactions")
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter une transaction par son identifiant unique")
    public ResponseEntity<TransactionResponseDTO> getTransactionById(@PathVariable UUID id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @GetMapping("/account/{accountId}")
    @Operation(summary = "Lister les transactions associées à un compte")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionsByAccountId(@PathVariable UUID accountId) {
        return ResponseEntity.ok(transactionService.getTransactionsByAccountId(accountId));
    }
}
