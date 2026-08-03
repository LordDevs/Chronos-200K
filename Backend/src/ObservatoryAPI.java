import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.json.JSONObject;

/** Recent Observatory Channel activity for the HUD panel. */
public class ObservatoryAPI extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JSONObject root = new JSONObject();
        try {
            root.put("recent", ObservatoryActivityLog.getInstance().toJsonArray());
        } catch (Exception e) {
            throw new ServletException(e);
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(root.toString());
    }
}
