package net.adria.bank.ai.adapter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.adria.bank.ai.service.AiAgentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/ai/webhook")
@RequiredArgsConstructor
@Tag(name = "Messaging Connectors", description = "Connecteurs Telegram et Discord pour le Chatbot")
public class MessengerWebhookController {

    private final AiAgentService aiAgentService;

    @PostMapping("/telegram")
    @Operation(summary = "Webhook Telegram pour recevoir les messages et répondre")
    public ResponseEntity<Map<String, Object>> handleTelegramUpdate(@RequestBody Map<String, Object> update) {
        log.info("[Telegram Webhook] Mise à jour reçue: {}", update);
        String text = "Bonjour";
        if (update.containsKey("message")) {
            Map<String, Object> msg = (Map<String, Object>) update.get("message");
            text = (String) msg.getOrDefault("text", "Bonjour");
        }
        Map<String, Object> agentResult = aiAgentService.processUserPrompt(text);
        return ResponseEntity.ok(Map.of(
                "method", "sendMessage",
                "chat_id", 123456,
                "text", agentResult.get("response")
        ));
    }

    @PostMapping("/discord")
    @Operation(summary = "Webhook Discord (Interactions API)")
    public ResponseEntity<Map<String, Object>> handleDiscordInteraction(@RequestBody Map<String, Object> interaction) {
        log.info("[Discord Webhook] Interaction reçue: {}", interaction);
        String content = (String) interaction.getOrDefault("content", "Bonjour");
        Map<String, Object> agentResult = aiAgentService.processUserPrompt(content);
        return ResponseEntity.ok(Map.of(
                "type", 4, // CHANNEL_MESSAGE_WITH_SOURCE
                "data", Map.of("content", agentResult.get("response"))
        ));
    }
}
