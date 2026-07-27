import evolution.ColonyProfile;
import evolution.LearningStore;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.json.JSONArray;

/** JSON list of saved colony profiles (Learning Mode v2). */
public class ColoniesAPI extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JSONArray arr = new JSONArray();
        for (ColonyProfile profile : LearningStore.getInstance().list()) {
            arr.put(profile.toJson());
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(arr.toString());
    }
}
