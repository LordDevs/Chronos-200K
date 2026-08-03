import org.json.JSONObject;

/**
 * Fetches exoplanet parameters from the NASA Exoplanet Archive TAP service (no API key).
 * Short scan for ANALYZE PLANET; Deep Scan is handled by ObservatoryService.
 */
public class ExoplanetService {

    private final String planetQuery;
    private final NasaTapClient tap;

    public ExoplanetService(String planetQuery) {
        this(planetQuery, new NasaTapClient());
    }

    public ExoplanetService(String planetQuery, NasaTapClient tap) {
        this.planetQuery = planetQuery == null ? "" : planetQuery.trim();
        this.tap = tap != null ? tap : new NasaTapClient();
    }

    public String formatReport() {
        JSONObject row = fetchPlanet();
        if (row == null) {
            return "Capitão, nenhum exoplaneta correspondente a <strong>"
                    + NasaTapClient.escape(planetQuery)
                    + "</strong> na NASA Exoplanet Archive. Verifique o nome (ex.: kepler-442 b).";
        }

        String name = NasaTapClient.optString(row, "pl_name");
        String host = NasaTapClient.optString(row, "hostname");
        Double eqt = NasaTapClient.optDouble(row, "pl_eqt");
        Double radius = NasaTapClient.optDouble(row, "pl_rade");
        Double mass = NasaTapClient.optDouble(row, "pl_bmasse");
        Double orbsmax = NasaTapClient.optDouble(row, "pl_orbsmax");
        Double dist = NasaTapClient.optDouble(row, "sy_dist");
        Double gravity = estimateGravity(mass, radius);
        String habitability = classifyHabitability(eqt);

        StringBuilder sb = new StringBuilder();
        sb.append("SCAN // NASA TAP — <strong>").append(NasaTapClient.escape(name)).append("</strong><br>");
        if (!host.isEmpty()) {
            sb.append("Host star: ").append(NasaTapClient.escape(host)).append("<br>");
        }
        sb.append("Equilíbrio térmico: ").append(NasaTapClient.fmt(eqt, "K")).append("<br>");
        sb.append("Raio: ").append(NasaTapClient.fmt(radius, " R⊕")).append("<br>");
        sb.append("Massa: ").append(NasaTapClient.fmt(mass, " M⊕")).append("<br>");
        sb.append("Gravidade estimada: ").append(NasaTapClient.fmt(gravity, " g")).append("<br>");
        sb.append("Semi-eixo orbital: ").append(NasaTapClient.fmt(orbsmax, " AU")).append("<br>");
        sb.append("Distância do sistema: ").append(NasaTapClient.fmt(dist, " pc")).append("<br>");
        sb.append("Zona habitável (proxy T_eq): <strong>").append(habitability).append("</strong><br>");
        sb.append("<em>Capitão, analisando coordenadas… dados prontos para EVOLVE / DEEP SCAN / VAULT ARCHIVE.</em>");
        return sb.toString();
    }

    public JSONObject fetchPlanet() {
        return tap.fetchPlanet(planetQuery);
    }

    static Double estimateGravity(Double massEarth, Double radiusEarth) {
        return NasaTapClient.estimateGravity(massEarth, radiusEarth);
    }

    static String classifyHabitability(Double eqtKelvin) {
        return NasaTapClient.classifyHabitability(eqtKelvin);
    }
}
