import static org.junit.jupiter.api.Assertions.*;

import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MessageHandlerTest {
    private MessageHandler messageHandler;

    @BeforeEach
    void setup() {
        Bot bot = new Bot("chronos", "Backend/ab", "chat");
        bot.writeAIMLIFFiles();
        Chat chat = new Chat(bot);
        messageHandler = new MessageHandler(chat);
    }

    @Test
    void testGreeting() {
        String result = messageHandler.processMessage("HI");
        assertNotNull(result);
        assertTrue(result.toUpperCase().contains("CHRONOS") || result.toLowerCase().contains("capit"),
                result);
    }

    @Test
    void testDirectExoplanetCommand() {
        String result = messageHandler.processMessage("exoplanet:kepler-442 b");
        assertNotNull(result);
        assertTrue(result.toLowerCase().contains("kepler") || result.toLowerCase().contains("nasa")
                        || result.toLowerCase().contains("capitão")
                        || result.toLowerCase().contains("capitao"),
                result);
    }

    @Test
    void testDirectEvolveCommand() {
        String result = messageHandler.processMessage("evolve:2g:80:1000");
        assertNotNull(result);
        assertTrue(result.toLowerCase().contains("evolution")
                        || result.toLowerCase().contains("adapta"),
                result);
    }

    @Test
    void testAimlOceanPlanetRouting() {
        String result = messageHandler.processMessage("ANALYZE OCEAN PLANET");
        assertNotNull(result);
        assertTrue(result.contains("LOGISTICS"), result);
    }

    @Test
    void testAimlApexProtocolRouting() {
        String result = messageHandler.processMessage("ACTIVATE APEX PROTOCOL");
        assertNotNull(result);
        assertTrue(result.contains("APEX PROTOCOL"), result);
    }

    @Test
    void testAimlSpeciation200k() {
        String result = messageHandler.processMessage("PREDICT EVOLUTION 200K");
        assertNotNull(result);
        assertTrue(result.contains("SPECIATION"), result);
    }

    @Test
    void testVoicePortugueseOceanPlanet() {
        String result = messageHandler.processMessage("ANALISAR PLANETA OCEANO");
        assertNotNull(result);
        assertTrue(result.contains("LOGISTICS"), result);
    }

    @Test
    void testVoicePortugueseApexProtocol() {
        String result = messageHandler.processMessage("ATIVAR PROTOCOLO APEX");
        assertNotNull(result);
        assertTrue(result.contains("APEX PROTOCOL"), result);
    }

    @Test
    void testVoicePortugueseShipStatus() {
        String result = messageHandler.processMessage("ESTADO DA NAVE");
        assertNotNull(result);
        assertTrue(result.toUpperCase().contains("TELEMETRIA") || result.toUpperCase().contains("OXIG"),
                result);
    }

    @Test
    void testVoicePortugueseOxygen() {
        String result = messageHandler.processMessage("VERIFICAR OXIGENIO");
        assertNotNull(result);
        assertTrue(result.toUpperCase().contains("O2") || result.toUpperCase().contains("OXIG"),
                result);
    }

    @Test
    void testVoicePortugueseExoplanet() {
        String result = messageHandler.processMessage("ANALISAR PLANETA kepler-442 b");
        assertNotNull(result);
        assertTrue(result.toLowerCase().contains("kepler") || result.toLowerCase().contains("nasa")
                        || result.toLowerCase().contains("capit"),
                result);
    }

    @Test
    void testVoiceEvolution200Mil() {
        String result = messageHandler.processMessage("PREVER EVOLUCAO 200 MIL");
        assertNotNull(result);
        assertTrue(result.contains("SPECIATION"), result);
    }

    @Test
    void testLearningModeSaveAndEvolve() {
        String saved = messageHandler.processMessage(
                "LEARNING MODE SAVE test-habitat gravity 1.5g water 60 temp 20 generations 800");
        assertNotNull(saved);
        assertTrue(saved.contains("COLONY ARCHIVE"), saved);

        String evolved = messageHandler.processMessage("LEARNING MODE EVOLVE test-habitat");
        assertNotNull(evolved);
        assertTrue(evolved.contains("EVOLUTION FORECAST") || evolved.contains("LEARNING MODE v2"), evolved);

        messageHandler.processMessage("LEARNING MODE DELETE test-habitat");
    }

    @Test
    void testLearningModeList() {
        String result = messageHandler.processMessage("LEARNING MODE LIST");
        assertNotNull(result);
        assertTrue(result.contains("COLONY ARCHIVE"), result);
    }
}
