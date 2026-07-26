import java.io.BufferedReader;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;

/** HTTP API for /chat/api */
public class ChatAPI extends HttpServlet {
    private Bot bot;
    private Chat chat;

    @Override
    public void init() throws ServletException {
        bot = new Bot("chronos", "Backend/ab", "chat");
        chat = new Chat(bot);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }

        MessageHandler handler = new MessageHandler(chat);
        String botResponse = handler.processMessage(buffer.toString());

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(botResponse);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("CHRONOS ChatAPI online. Use POST /chat/api.");
    }
}
