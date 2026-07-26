ackage com.chatbot;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.alicebot.ab.*;

// Initialize API for /chat/api
public class ChatAPI extends HttpServlet {
    private Bot bot;
    private Chat chat;

    /// Server initialization
    @Override
    public void init() throws ServletException {
        /// Creates bot
        bot = new Bot("jarvis", "Backend/ab", "chat");
        /// Chat handler with bot
        chat = new Chat(bot);
    }

    /// POST method
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        /// Read request body
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }

        String requestBody = buffer.toString();

        MessageHandler handler = new MessageHandler(chat);
        String botResponse = handler.processMessage(requestBody);

        /// Send response back to client
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(botResponse);
    }

    /// GET Method
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("ChatServlet API is active. Use POST to chat.");
    }
}
