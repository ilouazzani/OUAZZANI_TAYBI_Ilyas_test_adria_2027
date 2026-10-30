package net.adria.bank.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.adria.bank.ai.client.AccountMcpClient;
import net.adria.bank.ai.client.TransactionMcpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiAgentService {

    private final AccountMcpClient accountMcpClient;
    private final TransactionMcpClient transactionMcpClient;

    @Value("${openai.api-key:demo-key}")
    private String openAiApiKey;

    private static final Pattern UUID_PATTERN = Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("(\\d+(\\.\\d+)?)");

    public Map<String, Object> processUserPrompt(String userPrompt) {
        log.info("[AI Agent] Message reçu de l'utilisateur: {}", userPrompt);

        String promptLower = userPrompt.toLowerCase();
        String selectedTool;
        Object toolResult;
        String conversationalResponse;

        // Intent 1: Consultation de solde / compte
        if (promptLower.contains("solde") || promptLower.contains("balance") || promptLower.contains("compte")) {
            List<UUID> uuids = extractUuids(userPrompt);
            if (!uuids.isEmpty()) {
                selectedTool = "get_account_by_id";
                toolResult = accountMcpClient.getAccountDetails(uuids.get(0));
                conversationalResponse = "Voici les informations de votre compte bancaire : " + toolResult;
            } else {
                selectedTool = "list_all_accounts";
                toolResult = accountMcpClient.listAllAccounts();
                conversationalResponse = "Voici la liste de vos comptes bancaires actifs : " + toolResult;
            }
        }
        // Intent 2: Virement d'argent
        else if (promptLower.contains("virement") || promptLower.contains("transfér") || promptLower.contains("transfer")) {
            List<UUID> uuids = extractUuids(userPrompt);
            double amount = extractAmount(userPrompt);

            if (uuids.size() >= 2 && amount > 0) {
                selectedTool = "transfer_money";
                toolResult = accountMcpClient.transferMoney(uuids.get(0), uuids.get(1), amount);
                conversationalResponse = "Virement effectué avec succès : " + toolResult;
            } else {
                selectedTool = "transfer_money (validation manquante)";
                toolResult = Map.of("error", "Précisez le compte source (UUID), le compte destinataire (UUID) et le montant.");
                conversationalResponse = "Pour effectuer un virement, veuillez renseigner l'UUID source, l'UUID destination et le montant souhaité.";
            }
        }
        // Intent 3: Historique des transactions
        else if (promptLower.contains("transaction") || promptLower.contains("historique") || promptLower.contains("dépense")) {
            List<UUID> uuids = extractUuids(userPrompt);
            if (!uuids.isEmpty()) {
                selectedTool = "get_account_transactions";
                toolResult = transactionMcpClient.getAccountTransactions(uuids.get(0));
                conversationalResponse = "Voici l'historique récent de vos transactions : " + toolResult;
            } else {
                selectedTool = "get_account_transactions (UUID manquant)";
                toolResult = Map.of("error", "Veuillez fournir l'identifiant du compte.");
                conversationalResponse = "Veuillez spécifier l'identifiant (UUID) de votre compte pour consulter vos transactions.";
            }
        }
        // Default: Salutations / Aide
        else {
            selectedTool = "help_assistant";
            toolResult = Map.of("capabilities", List.of("Consultation de solde", "Historique de transactions", "Virements bancaires"));
            conversationalResponse = "Bonjour ! Je suis votre assistant bancaire intelligent ADRIA. Je peux consulter votre solde, lister vos transactions ou exécuter vos virements via nos serveurs MCP sécurisés. Comment puis-je vous aider aujourd'hui ?";
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("prompt", userPrompt);
        response.put("executedTool", selectedTool);
        response.put("toolData", toolResult);
        response.put("response", conversationalResponse);
        response.put("timestamp", new Date());

        return response;
    }

    private List<UUID> extractUuids(String text) {
        List<UUID> list = new ArrayList<>();
        Matcher matcher = UUID_PATTERN.matcher(text);
        while (matcher.find()) {
            try {
                list.add(UUID.fromString(matcher.group()));
            } catch (Exception ignored) {}
        }
        return list;
    }

    private double extractAmount(String text) {
        Matcher matcher = AMOUNT_PATTERN.matcher(text);
        while (matcher.find()) {
            try {
                double val = Double.parseDouble(matcher.group());
                if (val > 0) return val;
            } catch (Exception ignored) {}
        }
        return 0.0;
    }
}
