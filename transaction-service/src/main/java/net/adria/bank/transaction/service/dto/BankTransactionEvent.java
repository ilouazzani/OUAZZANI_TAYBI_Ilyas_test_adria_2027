package net.adria.bank.transaction.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankTransactionEvent {
    private UUID eventId;
    private String eventType;
    private UUID transactionId;
    private UUID accountId;
    private BigDecimal amount;
    private String currency;
    private Instant occurredAt;
}
