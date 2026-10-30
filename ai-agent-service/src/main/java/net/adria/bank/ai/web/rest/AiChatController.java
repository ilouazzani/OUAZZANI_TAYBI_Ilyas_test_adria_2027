package net.adria.bank.ai.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.adria.bank.ai.service.AiAgentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI Chatbot", description = "Agent conversationnel bancaire intelligent (Spring AI / MCP)")
public class AiChatController {

    private final AiAgentService aiAgentService;

    @PostMapping("/chat")
    @Operation(summary = "Interagir avec le Chatbot bancaire (déclenche les tools MCP)")
    public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, String> request) {
        String prompt = request.getOrDefault("message", "Bonjour");
        return ResponseEntity.ok(aiAgentService.processUserPrompt(prompt));
    }

    @GetMapping("/tools")
    @Operation(summary = "Lister les capacités et outils MCP connectés à l'agent")
    public ResponseEntity<List<Map<String, String>>> listConnectedMcpTools() {
        return ResponseEntity.ok(List.of(
                Map.of("tool", "get_account_by_id", "targetService", "account-service", "description", "Consultation de solde"),
                Map.of("tool", "list_all_accounts", "targetService", "account-service", "description", "Liste globale des comptes"),
                Map.of("tool", "transfer_money", "targetService", "account-service", "description", "Exécution de virements"),
                Map.of("tool", "get_account_transactions", "targetService", "transaction-service", "description", "Historique de transactions")
        ));
    }
}
