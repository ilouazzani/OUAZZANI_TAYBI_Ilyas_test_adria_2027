package net.adria.bank.account.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestDTO {

    @NotNull(message = "sourceAccountId is required")
    private UUID sourceAccountId;

    @NotNull(message = "destinationAccountId is required")
    private UUID destinationAccountId;

    @NotNull(message = "amount is required")
    @Positive(message = "amount must be strictly positive")
    private Double amount;
}
