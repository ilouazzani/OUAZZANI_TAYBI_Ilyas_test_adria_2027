package net.adria.bank.transaction.mcp;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/mcp")
@RequiredArgsConstructor
public class TransactionMcpController {

    private final TransactionMcpTools transactionMcpTools;

    @GetMapping("/tools")
    public ResponseEntity<List<Map<String, Object>>> getDeclaredTools() {
        List<Map<String, Object>> tools = List.of(
                Map.of(
                        "name", "get_account_transactions",
                        "description", "Obtenir l'historique complet des transactions d'un compte bancaire",
                        "parameters", Map.of("accountId", "UUID de compte")
                ),
                Map.of(
                        "name", "execute_card_payment",
                        "description", "Exécuter une transaction ou paiement par carte",
                        "parameters", Map.of(
                                "accountId", "UUID du compte débité",
                                "amount", "Montant (double)",
                                "currency", "Devise ISO (ex: EUR)",
                                "merchantCategory", "Catégorie commerçant",
                                "country", "Code pays (ex: FR, MA)"
                        )
                )
        );
        return ResponseEntity.ok(tools);
    }

    @PostMapping("/execute")
    public ResponseEntity<Object> executeTool(@RequestBody Map<String, Object> request) {
        String toolName = (String) request.get("tool");
        Map<String, Object> args = (Map<String, Object>) request.getOrDefault("arguments", Collections.emptyMap());

        return switch (toolName) {
            case "get_account_transactions" -> {
                UUID accountId = UUID.fromString((String) args.get("accountId"));
                yield ResponseEntity.ok(transactionMcpTools.getTransactionsForAccount(accountId));
            }
            case "execute_card_payment" -> {
                UUID accountId = UUID.fromString((String) args.get("accountId"));
                double amount = Double.parseDouble(args.get("amount").toString());
                String currency = (String) args.get("currency");
                String merchantCategory = (String) args.get("merchantCategory");
                String country = (String) args.get("country");
                yield ResponseEntity.ok(transactionMcpTools.executePayment(accountId, amount, currency, merchantCategory, country));
            }
            default -> ResponseEntity.badRequest().body(Map.of("error", "Unknown MCP tool: " + toolName));
        };
    }
}
