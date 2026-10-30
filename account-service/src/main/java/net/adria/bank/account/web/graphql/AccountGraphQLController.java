package net.adria.bank.account.web.graphql;

import lombok.RequiredArgsConstructor;
import net.adria.bank.account.domain.AccountStatus;
import net.adria.bank.account.service.AccountService;
import net.adria.bank.account.service.dto.AccountRequestDTO;
import net.adria.bank.account.service.dto.AccountResponseDTO;
import net.adria.bank.account.service.dto.TransferRequestDTO;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AccountGraphQLController {

    private final AccountService accountService;

    @QueryMapping
    public List<AccountResponseDTO> accounts() {
        return accountService.getAllAccounts();
    }

    @QueryMapping
    public AccountResponseDTO accountById(@Argument UUID id) {
        return accountService.getAccountById(id);
    }

    @MutationMapping
    public AccountResponseDTO createAccount(@Argument AccountInput request) {
        AccountRequestDTO dto = AccountRequestDTO.builder()
                .customerReference(request.customerReference())
                .status(request.status())
                .currency(request.currency())
                .balance(request.balance())
                .build();
        return accountService.createAccount(dto);
    }

    @MutationMapping
    public AccountResponseDTO updateBalance(@Argument UUID id, @Argument double amount) {
        return accountService.updateBalance(id, amount);
    }

    @MutationMapping
    public Boolean transfer(@Argument UUID sourceAccountId, @Argument UUID destinationAccountId, @Argument double amount) {
        accountService.transfer(TransferRequestDTO.builder()
                .sourceAccountId(sourceAccountId)
                .destinationAccountId(destinationAccountId)
                .amount(amount)
                .build());
        return true;
    }

    public record AccountInput(String customerReference, AccountStatus status, String currency, double balance) {}
}
