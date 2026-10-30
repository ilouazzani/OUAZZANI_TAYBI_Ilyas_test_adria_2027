package net.adria.bank.account.mcp;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/mcp")
@RequiredArgsConstructor
public class AccountMcpController {

    private final AccountMcpTools accountMcpTools;

    @GetMapping("/tools")
    public ResponseEntity<List<Map<String, Object>>> getDeclaredTools() {
        List<Map<String, Object>> tools = List.of(
                Map.of(
                        "name", "get_account_by_id",
                        "description", "Consulter le solde et les détails d'un compte bancaire par son ID",
                        "parameters", Map.of("accountId", "UUID (identifiant du compte)")
                ),
                Map.of(
                        "name", "list_all_accounts",
                        "description", "Lister tous les comptes bancaires existants",
                        "parameters", Map.of()
                ),
                Map.of(
                        "name", "transfer_money",
                        "description", "Effectuer un virement bancaire entre deux comptes",
                        "parameters", Map.of(
                                "sourceAccountId", "UUID compte émetteur",
                                "destinationAccountId", "UUID compte destinataire",
                                "amount", "Montant du virement (double)"
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
            case "get_account_by_id" -> {
                UUID id = UUID.fromString((String) args.get("accountId"));
                yield ResponseEntity.ok(accountMcpTools.getAccount(id));
            }
            case "list_all_accounts" -> ResponseEntity.ok(accountMcpTools.listAccounts());
            case "transfer_money" -> {
                UUID src = UUID.fromString((String) args.get("sourceAccountId"));
                UUID dst = UUID.fromString((String) args.get("destinationAccountId"));
                double amount = Double.parseDouble(args.get("amount").toString());
                yield ResponseEntity.ok(Map.of("result", accountMcpTools.transferMoney(src, dst, amount)));
            }
            default -> ResponseEntity.badRequest().body(Map.of("error", "Unknown MCP tool: " + toolName));
        };
    }
}
