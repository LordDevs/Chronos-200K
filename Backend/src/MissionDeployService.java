import evolution.ColonyProfile;
import evolution.EnvironmentProfile;
import evolution.LearningStore;
import java.util.Locale;
import org.json.JSONObject;

/**
 * Mission Brief / Colony Deploy — NASA TAP → suggested colonial profile (+ optional archive save).
 */
public class MissionDeployService {

    private final NasaTapClient tap;
    private final LearningStore learningStore;
    private final ObservatoryActivityLog activity;

    public MissionDeployService() {
        this(new NasaTapClient(), LearningStore.getInstance(), ObservatoryActivityLog.getInstance());
    }

    public MissionDeployService(
            NasaTapClient tap,
            LearningStore learningStore,
            ObservatoryActivityLog activity) {
        this.tap = tap != null ? tap : new NasaTapClient();
        this.learningStore = learningStore != null ? learningStore : LearningStore.getInstance();
        this.activity = activity != null ? activity : ObservatoryActivityLog.getInstance();
    }

    /**
     * Payload: {@code <planet>} or {@code <planet>:save}
     */
    public String handle(String payload) {
        if (payload == null || payload.isBlank()) {
            return "Formato: DEPLOY COLONY &lt;planeta&gt; [SAVE] — ex.: deploy:kepler-442 b:save";
        }
        String p = payload.trim();
        boolean save = false;
        String lower = p.toLowerCase(Locale.ROOT);
        if (lower.endsWith(":save")) {
            save = true;
            p = p.substring(0, p.length() - ":save".length()).trim();
        } else if (lower.endsWith(" save")) {
            save = true;
            p = p.substring(0, p.length() - " save".length()).trim();
        }
        return deploy(p, save);
    }

    public String deploy(String planetQuery, boolean saveToArchive) {
        JSONObject row = tap.fetchPlanet(planetQuery);
        if (row == null) {
            return "Capitão, nenhum exoplaneta correspondente a <strong>"
                    + NasaTapClient.escape(planetQuery)
                    + "</strong> na NASA Exoplanet Archive.";
        }

        String planetName = NasaTapClient.optString(row, "pl_name");
        if (planetName.isBlank()) {
            planetName = planetQuery.trim();
        }
        String colonyName = ColonyProfile.normalizeName(planetName);

        Double mass = NasaTapClient.optDouble(row, "pl_bmasse");
        Double radius = NasaTapClient.optDouble(row, "pl_rade");
        Double gravity = NasaTapClient.estimateGravity(mass, radius);
        if (gravity == null) {
            return "MISSION BRIEF falhou: massa ou raio ausentes em TAP para <strong>"
                    + NasaTapClient.escape(planetName)
                    + "</strong> — g ≈ M/R² requer ambos.";
        }

        Double eqt = NasaTapClient.optDouble(row, "pl_eqt");
        boolean tempDefaulted = eqt == null;
        double tempC = tempDefaulted
                ? ObservatoryService.DEFAULT_TEMP_C
                : (eqt - 273.15);
        Double dist = NasaTapClient.optDouble(row, "sy_dist");
        String host = NasaTapClient.optString(row, "hostname");
        String hz = NasaTapClient.classifyHabitability(eqt);

        EnvironmentProfile env = new EnvironmentProfile(
                gravity,
                ObservatoryService.DEFAULT_WATER,
                tempC,
                ObservatoryService.DEFAULT_ATMOSPHERE,
                ObservatoryService.DEFAULT_GENERATIONS);

        StringBuilder sb = new StringBuilder();
        sb.append("MISSION BRIEF // NASA-anchored deploy — <strong>")
                .append(NasaTapClient.escape(planetName)).append("</strong><br>");
        if (!host.isEmpty()) {
            sb.append("Host: ").append(NasaTapClient.escape(host)).append("<br>");
        }
        sb.append("<br><strong>Telemetria TAP</strong><br>");
        sb.append("T_eq: ").append(NasaTapClient.fmt(eqt, " K")).append("<br>");
        sb.append("Massa / Raio: ").append(NasaTapClient.fmt(mass, " M⊕"))
                .append(" / ").append(NasaTapClient.fmt(radius, " R⊕")).append("<br>");
        sb.append("g estimado (M/R²): ").append(NasaTapClient.fmt(gravity, " g")).append("<br>");
        sb.append("Distância: ").append(NasaTapClient.fmt(dist, " pc")).append("<br>");
        sb.append("HZ proxy: <strong>").append(hz).append("</strong><br>");

        sb.append("<br><strong>Perfil colonial sugerido</strong> — <code>")
                .append(NasaTapClient.escape(colonyName)).append("</code><br>");
        sb.append(String.format(Locale.US,
                "g=%.2f · água=%.0f%% · temp=%.0f°C · %s · %d gerações<br>",
                env.getGravityG(), env.getWaterPercent(), env.getTemperatureC(),
                env.getAtmosphere(), env.getGenerations()));
        sb.append("<em>TAP → g, T_eq");
        if (tempDefaulted) {
            sb.append(" n/d — default 15°C");
        } else {
            sb.append(" → °C");
        }
        sb.append(". Assumidos: água, atmosfera N2-O2, gerações 1000.</em><br>");

        sb.append("<br><strong>Próximos protocolos</strong><br>");
        if (gravity >= 1.4 || (eqt != null && (eqt < 180 || eqt > 310))) {
            sb.append("• CAP recomendado: <code>ACTIVATE APEX PROTOCOL</code><br>");
        } else {
            sb.append("• CAP opcional: <code>ACTIVATE APEX PROTOCOL</code><br>");
        }
        sb.append("• Vault: <code>VAULT ARCHIVE ").append(NasaTapClient.escape(planetName))
                .append("</code><br>");
        sb.append("• Evolve: <code>LEARNING MODE EVOLVE ").append(NasaTapClient.escape(colonyName))
                .append("</code><br>");

        if (saveToArchive) {
            learningStore.save(new ColonyProfile(colonyName, env));
            sb.append("<br>COLONY ARCHIVE // perfil <strong>")
                    .append(NasaTapClient.escape(colonyName))
                    .append("</strong> guardado a partir de TAP.");
        } else {
            sb.append("<br>Para guardar: <code>DEPLOY COLONY ")
                    .append(NasaTapClient.escape(planetName))
                    .append(" SAVE</code>");
        }

        activity.record("deploy", planetName, sb.toString());
        return sb.toString();
    }
}
