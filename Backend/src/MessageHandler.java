import evolution.EvolutionEngine;
import evolution.LearningModeService;
import org.alicebot.ab.Chat;
import simulation.CommandRouter;

public class MessageHandler {
    private Chat chat;
    private final EvolutionEngine evolutionEngine = new EvolutionEngine();
    private final LearningModeService learningModeService = new LearningModeService();
    private final CommandRouter commandRouter = new CommandRouter();

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

        // Direct command bypass (API tests / scripted frontend)
        String routed = tryRoute(trimmed);
        if (routed != null) {
            return routed;
        }

        if (chat == null) {
            return "⚠️ CHRONOS offline — sessão AIML não inicializada.";
        }

        String botResponse = chat.multisentenceRespond(trimmed);
        if (botResponse == null) {
            return "⚠️ Estou com interferência no canal. Tente novamente, Capitão.";
        }

        routed = tryRoute(botResponse);
        if (routed != null) {
            return routed;
        }

        return botResponse;
    }

    /**
     * Routes AIML tokens and direct prefixes to Java services.
     */
    private String tryRoute(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        String lower = text.trim().toLowerCase();

        if (lower.startsWith("exoplanet:")) {
            return handleExoplanet(text.substring("exoplanet:".length()).trim());
        }
        if (lower.startsWith("evolve:")) {
            return handleEvolve(text.substring("evolve:".length()).trim());
        }
        if (lower.startsWith("learn:")) {
            return handleLearn(text.substring("learn:".length()).trim());
        }

        String simulation = commandRouter.route(text.trim());
        if (simulation != null) {
            return simulation;
        }

        return null;
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

    private String handleLearn(String payload) {
        if (payload == null || payload.isBlank()) {
            return learningModeService.handle("", "");
        }
        int colon = payload.indexOf(':');
        if (colon < 0) {
            return learningModeService.handle(payload.trim(), "");
        }
        String action = payload.substring(0, colon).trim();
        String rest = payload.substring(colon + 1).trim();
        return learningModeService.handle(action, rest);
    }
}
