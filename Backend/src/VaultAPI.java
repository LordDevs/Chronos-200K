import evolution.SpeciationVaultStore;
import evolution.VaultEntry;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.json.JSONArray;

/** JSON list of Speciation Vault entries. */
public class VaultAPI extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JSONArray arr = new JSONArray();
        for (VaultEntry entry : SpeciationVaultStore.getInstance().list()) {
            arr.put(entry.toJson());
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(arr.toString());
    }
}
