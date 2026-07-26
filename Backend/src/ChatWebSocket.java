import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/chat")
public class ChatWebSocket {

    private static final Logger LOGGER = Logger.getLogger(ChatWebSocket.class.getName());

    private MessageHandler msgHND;

    @OnOpen
    public void onOpen(Session session) {
        LOGGER.info(() -> String.format("%n%s%nCHRONOS WEBSOCKET OPEN. ID: %s%n%s%n",
                "#".repeat(24), session.getId(), "#".repeat(24)));
        Bot bot = new Bot("chronos", "Backend/ab", "chat");
        bot.writeAIMLIFFiles();
        Chat chat = new Chat(bot);
        msgHND = new MessageHandler(chat);
        try {
            session.getBasicRemote().sendText(
                    "CHRONOS online. Canal tático estabelecido. ANALYZE PLANET ou EVOLVE quando quiser, Capitão.");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize websocket session", e);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) throws Exception {
        LOGGER.info(() -> "Message received: " + message);
        String result = msgHND.processMessage(message);
        session.getBasicRemote().sendText(result);
    }

    @OnClose
    public void onClose(Session session) {
        LOGGER.info(() -> "WebSocket closed: " + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        LOGGER.log(Level.SEVERE, "WebSocket error", throwable);
    }
}
