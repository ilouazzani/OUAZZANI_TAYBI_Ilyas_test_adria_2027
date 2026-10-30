package net.adria.bank.account.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.adria.bank.account.domain.AccountStatus;
import net.adria.bank.account.service.AccountService;
import net.adria.bank.account.service.dto.AccountRequestDTO;
import net.adria.bank.account.service.dto.AccountResponseDTO;
import net.adria.bank.account.service.dto.BalanceUpdateRequestDTO;
import net.adria.bank.account.service.dto.TransferRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Gestion des comptes bancaires")
public class AccountRestController {

    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "Créer un nouveau compte bancaire")
    public ResponseEntity<AccountResponseDTO> createAccount(@Valid @RequestBody AccountRequestDTO request) {
        return new ResponseEntity<>(accountService.createAccount(request), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Lister tous les comptes bancaires")
    public ResponseEntity<List<AccountResponseDTO>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un compte bancaire par son ID")
    public ResponseEntity<AccountResponseDTO> getAccountById(@PathVariable UUID id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @PatchMapping("/{id}/balance")
    @Operation(summary = "Mettre à jour le solde d'un compte (débit ou crédit)")
    public ResponseEntity<AccountResponseDTO> updateBalance(
            @PathVariable UUID id,
            @Valid @RequestBody BalanceUpdateRequestDTO request) {
        return ResponseEntity.ok(accountService.updateBalance(id, request.getAmount()));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Mettre à jour le statut d'un compte (ACTIVE, BLOCKED)")
    public ResponseEntity<AccountResponseDTO> updateStatus(
            @PathVariable UUID id,
            @RequestParam AccountStatus status) {
        return ResponseEntity.ok(accountService.updateStatus(id, status));
    }

    @PostMapping("/transfer")
    @Operation(summary = "Effectuer un virement inter-comptes")
    public ResponseEntity<Void> transfer(@Valid @RequestBody TransferRequestDTO request) {
        accountService.transfer(request);
        return ResponseEntity.ok().build();
    }
}
