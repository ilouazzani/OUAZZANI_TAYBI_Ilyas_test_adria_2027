package net.adria.bank.transaction.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.adria.bank.transaction.service.dto.BankTransactionEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionEventPublisher {

    public static final String TOPIC = "bank.transactions.created";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishTransactionCreated(BankTransactionEvent event) {
        String key = event.getAccountId().toString();
        log.info("[Kafka Producer] Publication de l'événement {} dans le topic {} avec la clé {}", 
                event.getEventId(), TOPIC, key);
        try {
            kafkaTemplate.send(TOPIC, key, event);
        } catch (Exception e) {
            log.warn("[Kafka Producer] Impossible de publier dans Kafka (Kafka offline?): {}", e.getMessage());
        }
    }
}
