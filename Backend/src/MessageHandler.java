import evolution.EvolutionEngine;
import org.alicebot.ab.Chat;

public class MessageHandler {
    private Chat chat;
    private final EvolutionEngine evolutionEngine = new EvolutionEngine();

    public MessageHandler() {
    }

    public MessageHandler(Chat chat) {
        this.chat = chat;
    }

    public String processMessage(String message) {
        if (message == null || message.isBlank()) {
            return "Capitão, canal de voz/texto vazio. Reformule o comando.";
        }

        String trimmed = message.trim();
        String lower = trimmed.toLowerCase();

        // Direct command bypass (same pattern as legacy weather:)
        if (lower.startsWith("exoplanet:")) {
            return handleExoplanet(trimmed.substring("exoplanet:".length()).trim());
        }
        if (lower.startsWith("evolve:")) {
            return handleEvolve(trimmed.substring("evolve:".length()).trim());
        }

        if (chat == null) {
            return "⚠️ CHRONOS offline — sessão AIML não inicializada.";
        }

        String botResponse = chat.multisentenceRespond(trimmed);
        if (botResponse == null) {
            return "⚠️ Estou com interferência no canal. Tente novamente, Capitão.";
        }

        String responseLower = botResponse.toLowerCase();
        if (responseLower.startsWith("exoplanet:")) {
            return handleExoplanet(botResponse.substring("exoplanet:".length()).trim());
        }
        if (responseLower.startsWith("evolve:")) {
            return handleEvolve(botResponse.substring("evolve:".length()).trim());
        }

        return botResponse;
    }

    private String handleExoplanet(String planetName) {
        if (planetName == null || planetName.isBlank()) {
            return "Especifique o corpo: ANALYZE PLANET kepler-442 b";
        }
        return new ExoplanetService(planetName).formatReport();
    }

    private String handleEvolve(String tokens) {
        if (tokens == null || tokens.isBlank()) {
            return "Formato: EVOLVE gravity 2g water 80 generations 1000";
        }
        return evolutionEngine.simulateFromRaw(tokens);
    }
}
