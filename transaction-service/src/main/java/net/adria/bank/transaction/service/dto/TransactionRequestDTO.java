package net.adria.bank.transaction.service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequestDTO {

    @NotNull(message = "accountId is required")
    private UUID accountId;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "amount must be strictly positive")
    private BigDecimal amount;

    @NotBlank(message = "currency is required")
    @Size(min = 3, max = 3, message = "currency must be 3 characters")
    private String currency;

    @NotBlank(message = "merchantCategory is required")
    private String merchantCategory;

    @NotBlank(message = "country is required")
    @Size(min = 2, max = 2, message = "country must be 2 characters ISO code")
    private String country;
}
