package net.adria.bank.transaction.messaging;

import lombok.extern.slf4j.Slf4j;
import net.adria.bank.transaction.service.dto.BankTransactionEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransactionEventConsumer {

    @KafkaListener(topics = "bank.transactions.created", groupId = "transaction-audit-group")
    public void consumeTransactionEvent(
            @Payload BankTransactionEvent event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("==================== [KAFKA EVENT CONSUMED] ====================");
        log.info("Partition: {}, Offset: {}, Partition Key: {}", partition, offset, key);
        log.info("Event ID: {}", event.getEventId());
        log.info("Type d'événement: {}", event.getEventType());
        log.info("Transaction ID: {}", event.getTransactionId());
        log.info("Account ID: {}", event.getAccountId());
        log.info("Montant: {} {}", event.getAmount(), event.getCurrency());
        log.info("Horodatage: {}", event.getOccurredAt());
        log.info("Statut: Audit journalisé avec succès (Données bancaires intactes)");
        log.info("===============================================================");
    }
}
