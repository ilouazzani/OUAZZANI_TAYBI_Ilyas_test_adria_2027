package net.adria.bank.account.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.adria.bank.account.domain.AccountStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequestDTO {

    @NotBlank(message = "customerReference is required")
    private String customerReference;

    private AccountStatus status;

    @NotBlank(message = "currency is required")
    @Size(min = 3, max = 3, message = "currency must be 3 characters ISO code (e.g. EUR, MAD, USD)")
    private String currency;

    @PositiveOrZero(message = "initial balance must be positive or zero")
    private double balance;
}
