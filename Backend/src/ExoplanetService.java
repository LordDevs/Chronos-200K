import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Fetches exoplanet parameters from the NASA Exoplanet Archive TAP service (no API key).
 */
public class ExoplanetService {

    private static final Logger LOGGER = Logger.getLogger(ExoplanetService.class.getName());
    private static final String TAP_BASE =
            "https://exoplanetarchive.ipac.caltech.edu/TAP/sync";
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    private final String planetQuery;

    public ExoplanetService(String planetQuery) {
        this.planetQuery = planetQuery == null ? "" : planetQuery.trim();
    }

    public String formatReport() {
        JSONObject row = fetchPlanet();
        if (row == null) {
            return "Capitão, nenhum exoplaneta correspondente a <strong>"
                    + escape(planetQuery)
                    + "</strong> na NASA Exoplanet Archive. Verifique o nome (ex.: kepler-442 b).";
        }

        String name = optString(row, "pl_name");
        String host = optString(row, "hostname");
        Double eqt = optDouble(row, "pl_eqt");
        Double radius = optDouble(row, "pl_rade");
        Double mass = optDouble(row, "pl_bmasse");
        Double orbsmax = optDouble(row, "pl_orbsmax");
        Double dist = optDouble(row, "sy_dist");
        Double gravity = estimateGravity(mass, radius);
        String habitability = classifyHabitability(eqt);

        StringBuilder sb = new StringBuilder();
        sb.append("SCAN // NASA TAP — <strong>").append(escape(name)).append("</strong><br>");
        if (!host.isEmpty()) {
            sb.append("Host star: ").append(escape(host)).append("<br>");
        }
        sb.append("Equilíbrio térmico: ").append(fmt(eqt, "K")).append("<br>");
        sb.append("Raio: ").append(fmt(radius, " R⊕")).append("<br>");
        sb.append("Massa: ").append(fmt(mass, " M⊕")).append("<br>");
        sb.append("Gravidade estimada: ").append(fmt(gravity, " g")).append("<br>");
        sb.append("Semi-eixo orbital: ").append(fmt(orbsmax, " AU")).append("<br>");
        sb.append("Distância do sistema: ").append(fmt(dist, " pc")).append("<br>");
        sb.append("Zona habitável (proxy T_eq): <strong>").append(habitability).append("</strong><br>");
        sb.append("<em>Capitão, analisando coordenadas… dados prontos para EVOLVE.</em>");
        return sb.toString();
    }

    public JSONObject fetchPlanet() {
        if (planetQuery.isEmpty()) {
            return null;
        }
        try {
            String like = sanitizeForLike(planetQuery).toLowerCase(Locale.ROOT);
            String adql = "select top 1 pl_name,hostname,pl_eqt,pl_rade,pl_bmasse,pl_orbsmax,sy_dist "
                    + "from pscomppars where lower(pl_name) like '%" + like + "%'";
            String url = TAP_BASE
                    + "?query=" + URLEncoder.encode(adql, StandardCharsets.UTF_8)
                    + "&format=json";

            LOGGER.info(() -> "NASA TAP request: " + url);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                LOGGER.warning("NASA TAP HTTP " + response.statusCode() + ": " + response.body());
                return null;
            }

            JSONArray arr = new JSONArray(response.body());
            if (arr.length() == 0) {
                return null;
            }
            return arr.getJSONObject(0);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to query NASA Exoplanet Archive", e);
            return null;
        }
    }

    static Double estimateGravity(Double massEarth, Double radiusEarth) {
        if (massEarth == null || radiusEarth == null || radiusEarth == 0) {
            return null;
        }
        return massEarth / (radiusEarth * radiusEarth);
    }

    static String classifyHabitability(Double eqtKelvin) {
        if (eqtKelvin == null) {
            return "dados insuficientes";
        }
        if (eqtKelvin >= 180 && eqtKelvin <= 310) {
            return "candidato à zona habitável";
        }
        if (eqtKelvin < 180) {
            return "demasiado frio (fora da ZH conservadora)";
        }
        return "demasiado quente (fora da ZH conservadora)";
    }

    private static String sanitizeForLike(String raw) {
        return raw.replace("'", "").replace(";", "").replace("--", "").trim();
    }

    private static String optString(JSONObject row, String key) {
        try {
            if (!row.has(key) || row.isNull(key)) {
                return "";
            }
            return String.valueOf(row.get(key));
        } catch (Exception e) {
            return "";
        }
    }

    private static Double optDouble(JSONObject row, String key) {
        try {
            if (!row.has(key) || row.isNull(key)) {
                return null;
            }
            return row.getDouble(key);
        } catch (Exception e) {
            return null;
        }
    }

    private static String fmt(Double value, String unit) {
        if (value == null) {
            return "n/d";
        }
        return String.format(Locale.US, "%.2f%s", value, unit);
    }

    private static String escape(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("<", "&lt;").replace(">", "&gt;");
    }
}
