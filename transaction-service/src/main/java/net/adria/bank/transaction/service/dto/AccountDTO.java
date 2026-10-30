package net.adria.bank.transaction.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    private UUID id;
    private String customerReference;
    private String status; // ACTIVE, BLOCKED
    private String currency;
    private double balance;
}
