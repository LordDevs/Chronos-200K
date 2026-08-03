import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Shared NASA Exoplanet Archive TAP client (pscomppars). Injectable for tests.
 */
public class NasaTapClient {

    private static final Logger LOGGER = Logger.getLogger(NasaTapClient.class.getName());
    private static final String TAP_BASE =
            "https://exoplanetarchive.ipac.caltech.edu/TAP/sync";
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(12);
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    /** Full field set for Deep Scan / Compare / Vault. */
    public static final String SELECT_FIELDS =
            "pl_name,hostname,pl_eqt,pl_rade,pl_bmasse,pl_orbsmax,sy_dist,"
                    + "pl_orbper,pl_dens,pl_insol,discoverymethod,disc_year,disc_facility,"
                    + "st_teff,st_spectype";

    private final Function<String, JSONObject> fetcher;

    public NasaTapClient() {
        this.fetcher = this::fetchFromNetwork;
    }

    public NasaTapClient(Function<String, JSONObject> fetcher) {
        this.fetcher = fetcher != null ? fetcher : this::fetchFromNetwork;
    }

    public JSONObject fetchPlanet(String planetQuery) {
        if (planetQuery == null || planetQuery.isBlank()) {
            return null;
        }
        return fetcher.apply(planetQuery.trim());
    }

    private JSONObject fetchFromNetwork(String planetQuery) {
        try {
            String like = sanitizeForLike(planetQuery).toLowerCase(Locale.ROOT);
            String adql = "select top 1 " + SELECT_FIELDS
                    + " from pscomppars where lower(pl_name) like '%" + like + "%'";
            String url = TAP_BASE
                    + "?query=" + URLEncoder.encode(adql, StandardCharsets.UTF_8)
                    + "&format=json";

            LOGGER.info(() -> "NASA TAP request: " + url);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(REQUEST_TIMEOUT)
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

    public static Double estimateGravity(Double massEarth, Double radiusEarth) {
        if (massEarth == null || radiusEarth == null || radiusEarth == 0) {
            return null;
        }
        return massEarth / (radiusEarth * radiusEarth);
    }

    public static String classifyHabitability(Double eqtKelvin) {
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

    public static String sanitizeForLike(String raw) {
        return raw.replace("'", "").replace(";", "").replace("--", "").trim();
    }

    public static String optString(JSONObject row, String key) {
        try {
            if (row == null || !row.has(key) || row.isNull(key)) {
                return "";
            }
            return String.valueOf(row.get(key));
        } catch (Exception e) {
            return "";
        }
    }

    public static Double optDouble(JSONObject row, String key) {
        try {
            if (row == null || !row.has(key) || row.isNull(key)) {
                return null;
            }
            return row.getDouble(key);
        } catch (Exception e) {
            return null;
        }
    }

    public static Integer optInt(JSONObject row, String key) {
        try {
            if (row == null || !row.has(key) || row.isNull(key)) {
                return null;
            }
            return row.getInt(key);
        } catch (Exception e) {
            Double d = optDouble(row, key);
            return d == null ? null : d.intValue();
        }
    }

    public static String fmt(Double value, String unit) {
        if (value == null) {
            return "n/d";
        }
        return String.format(Locale.US, "%.2f%s", value, unit);
    }

    public static String fmtInt(Integer value) {
        return value == null ? "n/d" : String.valueOf(value);
    }

    public static String escape(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("<", "&lt;").replace(">", "&gt;");
    }

    public static String displayOrNd(String value) {
        return value == null || value.isBlank() ? "n/d" : escape(value);
    }
}
