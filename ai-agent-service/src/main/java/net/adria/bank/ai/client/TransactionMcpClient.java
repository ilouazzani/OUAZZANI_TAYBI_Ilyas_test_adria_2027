package net.adria.bank.ai.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionMcpClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${services.transaction-service.url:http://localhost:8082}")
    private String transactionServiceUrl;

    public Object getAccountTransactions(UUID accountId) {
        String url = transactionServiceUrl + "/api/mcp/execute";
        Map<String, Object> body = Map.of(
                "tool", "get_account_transactions",
                "arguments", Map.of("accountId", accountId.toString())
        );
        log.info("[AI Agent -> Transaction MCP] Invocation de get_account_transactions pour {}", accountId);
        try {
            ResponseEntity<Object> response = restTemplate.postForEntity(url, body, Object.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("[AI Agent -> Transaction MCP] Erreur: {}", e.getMessage());
            return Map.of("error", "Impossible de récupérer l'historique des transactions: " + e.getMessage());
        }
    }
}
