package net.adria.bank.account.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.adria.bank.account.domain.AccountStatus;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDTO {
    private UUID id;
    private String customerReference;
    private AccountStatus status;
    private String currency;
    private double balance;
}
