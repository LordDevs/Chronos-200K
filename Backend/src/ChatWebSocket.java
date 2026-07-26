import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.alicebot.ab.*;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/chat")
public class ChatWebSocket{

    private static final Logger LOGGER = Logger.getLogger(ChatWebSocket.class.getName());

    private Bot bot;
    private Chat chat;
    private MessageHandler msgHND;

    //////////////////////////////////////////////////////// WEB SOCKET HANDLING ////////////////////////////////////////////////////////
    @OnOpen
    public void onOpen(Session session){
        LOGGER.info(() -> String.format("%n%s%nNEW WEBSOCKET ESTABLISHED. ID: %s%n%s%n", "#".repeat(24), session.getId(), "#".repeat(24)));
        bot = new Bot("jarvis", "Backend/ab", "chat");
        bot.writeAIMLIFFiles();
        chat = new Chat(bot);
        try{
            session.getBasicRemote().sendText("Hello, how can I help you today?");
            msgHND = new MessageHandler(chat);
        }catch(IOException e){
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
    public void onClose(Session session){
        LOGGER.info(() -> "WebSocket closed: " + session.getId());
    }
    @OnError
    public void onError(Session session, Throwable throwable){
        LOGGER.log(Level.SEVERE, "WebSocket error", throwable);
    }

//////////////////////////////////////////////////////// END WEB SOCKET HANDLING ////////////////////////////////////////////////////////
}
