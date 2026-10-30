package net.adria.bank.account.service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceUpdateRequestDTO {
    @NotNull(message = "amount is required")
    private Double amount; // positive to credit, negative to debit
}
