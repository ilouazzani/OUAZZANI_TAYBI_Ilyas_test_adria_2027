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
public class AccountMcpClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${services.account-service.url:http://localhost:8081}")
    private String accountServiceUrl;

    public Object getAccountDetails(UUID accountId) {
        String url = accountServiceUrl + "/api/mcp/execute";
        Map<String, Object> body = Map.of(
                "tool", "get_account_by_id",
                "arguments", Map.of("accountId", accountId.toString())
        );
        log.info("[AI Agent -> Account MCP] Invocation de get_account_by_id pour {}", accountId);
        try {
            ResponseEntity<Object> response = restTemplate.postForEntity(url, body, Object.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("[AI Agent -> Account MCP] Erreur: {}", e.getMessage());
            return Map.of("error", "Impossible de joindre account-service MCP: " + e.getMessage());
        }
    }

    public Object listAllAccounts() {
        String url = accountServiceUrl + "/api/mcp/execute";
        Map<String, Object> body = Map.of(
                "tool", "list_all_accounts",
                "arguments", Map.of()
        );
        log.info("[AI Agent -> Account MCP] Invocation de list_all_accounts");
        try {
            ResponseEntity<Object> response = restTemplate.postForEntity(url, body, Object.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("[AI Agent -> Account MCP] Erreur: {}", e.getMessage());
            return Map.of("error", "Impossible de lister les comptes: " + e.getMessage());
        }
    }

    public Object transferMoney(UUID src, UUID dst, double amount) {
        String url = accountServiceUrl + "/api/mcp/execute";
        Map<String, Object> body = Map.of(
                "tool", "transfer_money",
                "arguments", Map.of(
                        "sourceAccountId", src.toString(),
                        "destinationAccountId", dst.toString(),
                        "amount", amount
                )
        );
        log.info("[AI Agent -> Account MCP] Invocation de transfer_money de {} vers {}", src, dst);
        try {
            ResponseEntity<Object> response = restTemplate.postForEntity(url, body, Object.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("[AI Agent -> Account MCP] Erreur de virement: {}", e.getMessage());
            return Map.of("error", "Échec du virement: " + e.getMessage());
        }
    }
}
