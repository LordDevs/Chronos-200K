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
    void testAimlAstartesKitRouting() {
        String result = messageHandler.processMessage("ACTIVATE ASTARTES KIT");
        assertNotNull(result);
        assertTrue(result.contains("ASTARTES KIT"), result);
    }

    @Test
    void testAimlSpeciation200k() {
        String result = messageHandler.processMessage("PREDICT EVOLUTION 200K");
        assertNotNull(result);
        assertTrue(result.contains("SPECIATION"), result);
    }
}
