import evolution.EnvironmentProfile;
import evolution.EvolutionEngine;
import evolution.EvolutionReport;
import evolution.SpeciationVaultStore;
import evolution.VaultEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.json.JSONObject;

/**
 * Chronos Observatory Channel — Deep Scan, Compare, Speciation Vault.
 * Source of truth: NASA TAP pscomppars. Assumed fields are disclosed.
 */
public class ObservatoryService {

    public static final double EARTH_TEQ_K = 255.0;
    public static final double DEFAULT_WATER = 50.0;
    public static final String DEFAULT_ATMOSPHERE = "N2-O2";
    public static final int DEFAULT_GENERATIONS = 1000;
    public static final double DEFAULT_TEMP_C = 15.0;

    private final NasaTapClient tap;
    private final EvolutionEngine evolutionEngine;
    private final SpeciationVaultStore vault;
    private final ObservatoryActivityLog activity;

    public ObservatoryService() {
        this(new NasaTapClient(), new EvolutionEngine(), SpeciationVaultStore.getInstance(),
                ObservatoryActivityLog.getInstance());
    }

    public ObservatoryService(NasaTapClient tap, EvolutionEngine evolutionEngine, SpeciationVaultStore vault) {
        this(tap, evolutionEngine, vault, ObservatoryActivityLog.getInstance());
    }

    public ObservatoryService(
            NasaTapClient tap,
            EvolutionEngine evolutionEngine,
            SpeciationVaultStore vault,
            ObservatoryActivityLog activity) {
        this.tap = tap != null ? tap : new NasaTapClient();
        this.evolutionEngine = evolutionEngine != null ? evolutionEngine : new EvolutionEngine();
        this.vault = vault != null ? vault : SpeciationVaultStore.getInstance();
        this.activity = activity != null ? activity : ObservatoryActivityLog.getInstance();
    }

    public String deepScan(String planetQuery) {
        JSONObject row = tap.fetchPlanet(planetQuery);
        if (row == null) {
            return notFound(planetQuery);
        }
        String report = formatDeepScan(row);
        activity.record("deepscan", NasaTapClient.optString(row, "pl_name"), report);
        return report;
    }

    public String compare(String rawNames) {
        List<String> names = parseCompareNames(rawNames);
        if (names.size() < 2) {
            return "COMPARE PLANETS exige 2–3 nomes: COMPARE PLANETS a AND b [AND c]";
        }
        if (names.size() > 3) {
            names = names.subList(0, 3);
        }

        List<JSONObject> rows = new ArrayList<>();
        List<String> missing = new ArrayList<>();
        for (String name : names) {
            JSONObject row = tap.fetchPlanet(name);
            if (row == null) {
                missing.add(name);
            } else {
                rows.add(row);
            }
        }
        if (rows.isEmpty()) {
            return "Capitão, nenhum dos planetas foi encontrado na NASA TAP: "
                    + NasaTapClient.escape(String.join(", ", missing));
        }

        rows.sort(Comparator.comparingDouble((JSONObject r) -> {
            Double eqt = NasaTapClient.optDouble(r, "pl_eqt");
            if (eqt == null) {
                return Double.MAX_VALUE;
            }
            return Math.abs(eqt - EARTH_TEQ_K);
        }));

        StringBuilder sb = new StringBuilder();
        sb.append("OBSERVATORY COMPARE // NASA TAP (ranking aid: |T_eq − 255 K| — proxy)<br>");
        if (!missing.isEmpty()) {
            sb.append("<em>Não encontrados: ").append(NasaTapClient.escape(String.join(", ", missing)))
                    .append("</em><br>");
        }
        sb.append("<table border='1' cellpadding='4' cellspacing='0' style='border-collapse:collapse;font-size:0.9em'>");
        sb.append("<tr><th>Planeta</th><th>g (est.)</th><th>T_eq</th><th>Raio</th><th>Massa</th>")
                .append("<th>Dist.</th><th>HZ proxy</th></tr>");

        for (int i = 0; i < rows.size(); i++) {
            JSONObject row = rows.get(i);
            String name = NasaTapClient.optString(row, "pl_name");
            Double mass = NasaTapClient.optDouble(row, "pl_bmasse");
            Double radius = NasaTapClient.optDouble(row, "pl_rade");
            Double eqt = NasaTapClient.optDouble(row, "pl_eqt");
            Double dist = NasaTapClient.optDouble(row, "sy_dist");
            Double g = NasaTapClient.estimateGravity(mass, radius);
            String hz = NasaTapClient.classifyHabitability(eqt);
            String highlight = i == 0 ? " style='background:#1a3a2a'" : "";
            sb.append("<tr").append(highlight).append(">");
            sb.append("<td>").append(NasaTapClient.escape(name)).append(i == 0 ? " ★" : "").append("</td>");
            sb.append("<td>").append(NasaTapClient.fmt(g, " g")).append("</td>");
            sb.append("<td>").append(NasaTapClient.fmt(eqt, " K")).append("</td>");
            sb.append("<td>").append(NasaTapClient.fmt(radius, " R⊕")).append("</td>");
            sb.append("<td>").append(NasaTapClient.fmt(mass, " M⊕")).append("</td>");
            sb.append("<td>").append(NasaTapClient.fmt(dist, " pc")).append("</td>");
            sb.append("<td>").append(hz).append("</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        sb.append("<br><em>★ = T_eq mais próximo de 255 K (Terra, proxy). g ≈ M/R² (estimado).</em>");
        String report = sb.toString();
        String titles = rows.stream()
                .map(r -> NasaTapClient.optString(r, "pl_name"))
                .filter(s -> !s.isBlank())
                .reduce((a, b) -> a + " vs " + b)
                .orElse("compare");
        activity.record("compare", titles, report);
        return report;
    }

    public String vaultArchive(String planetQuery) {
        JSONObject row = tap.fetchPlanet(planetQuery);
        if (row == null) {
            return notFound(planetQuery);
        }

        String name = NasaTapClient.optString(row, "pl_name");
        if (name.isBlank()) {
            name = planetQuery.trim();
        }

        Double mass = NasaTapClient.optDouble(row, "pl_bmasse");
        Double radius = NasaTapClient.optDouble(row, "pl_rade");
        Double gravity = NasaTapClient.estimateGravity(mass, radius);
        if (gravity == null) {
            return "VAULT ARCHIVE falhou: massa ou raio ausentes em TAP para <strong>"
                    + NasaTapClient.escape(name)
                    + "</strong> — g ≈ M/R² requer ambos.";
        }

        Double eqt = NasaTapClient.optDouble(row, "pl_eqt");
        boolean tempDefaulted = eqt == null;
        double tempC = tempDefaulted ? DEFAULT_TEMP_C : (eqt - 273.15);

        EnvironmentProfile profile = new EnvironmentProfile(
                gravity, DEFAULT_WATER, tempC, DEFAULT_ATMOSPHERE, DEFAULT_GENERATIONS);
        EvolutionReport report = evolutionEngine.simulate(profile);

        VaultEntry entry = new VaultEntry(
                name,
                row,
                gravity,
                tempC,
                tempDefaulted,
                DEFAULT_WATER,
                DEFAULT_ATMOSPHERE,
                DEFAULT_GENERATIONS,
                report.getTraits(),
                report.getSurvivalProbability(),
                report.toHtml(),
                System.currentTimeMillis());
        vault.save(entry);

        StringBuilder sb = new StringBuilder();
        sb.append("SPECIATION VAULT // NASA-anchored — arquivado: <strong>")
                .append(NasaTapClient.escape(name)).append("</strong><br>");
        sb.append(formatTapVsAssumed(entry));
        sb.append("<br>").append(report.toHtml());
        String html = sb.toString();
        activity.record("vault", name, html);
        return html;
    }

    public String vaultList() {
        List<VaultEntry> list = vault.list();
        StringBuilder sb = new StringBuilder();
        sb.append("SPECIATION VAULT // NASA-anchored — entradas: ").append(list.size()).append("<br>");
        if (list.isEmpty()) {
            sb.append("<em>Vazio. Use VAULT ARCHIVE &lt;planeta&gt;.</em>");
            return sb.toString();
        }
        for (VaultEntry e : list) {
            sb.append("• ").append(NasaTapClient.escape(e.getPlanetName()))
                    .append(" — g ").append(String.format(Locale.US, "%.2f", e.getGravityG()))
                    .append(" · sobrevivência ")
                    .append(String.format(Locale.US, "%.0f%%", e.getSurvivalProbability() * 100))
                    .append("<br>");
        }
        return sb.toString();
    }

    public String vaultShow(String planetQuery) {
        Optional<VaultEntry> opt = vault.get(planetQuery);
        if (opt.isEmpty()) {
            // try resolve via TAP name then lookup
            JSONObject row = tap.fetchPlanet(planetQuery);
            if (row != null) {
                String name = NasaTapClient.optString(row, "pl_name");
                opt = vault.get(name);
            }
        }
        if (opt.isEmpty()) {
            return "SPECIATION VAULT // sem entrada para <strong>"
                    + NasaTapClient.escape(planetQuery)
                    + "</strong>. Use VAULT ARCHIVE primeiro.";
        }
        VaultEntry e = opt.get();
        StringBuilder sb = new StringBuilder();
        sb.append("SPECIATION VAULT // NASA-anchored — SHOW <strong>")
                .append(NasaTapClient.escape(e.getPlanetName())).append("</strong><br>");
        sb.append(formatTapVsAssumed(e));
        sb.append("<br><strong>Snapshot TAP:</strong><br>");
        sb.append(formatTapSnapshotBrief(e.getTapSnapshot()));
        sb.append("<br>").append(e.getForecastHtml());
        return sb.toString();
    }

    public String vaultDelete(String planetQuery) {
        Optional<VaultEntry> existing = vault.get(planetQuery);
        String key = planetQuery;
        if (existing.isEmpty()) {
            JSONObject row = tap.fetchPlanet(planetQuery);
            if (row != null) {
                key = NasaTapClient.optString(row, "pl_name");
            }
        } else {
            key = existing.get().getPlanetName();
        }
        boolean removed = vault.delete(key);
        if (!removed) {
            return "SPECIATION VAULT // nada a apagar para <strong>"
                    + NasaTapClient.escape(planetQuery) + "</strong>.";
        }
        return "SPECIATION VAULT // entrada removida: <strong>"
                + NasaTapClient.escape(key) + "</strong>.";
    }

    public String handleVault(String payload) {
        if (payload == null || payload.isBlank()) {
            return "VAULT: ARCHIVE|LIST|SHOW|DELETE — ex.: vault:archive:kepler-442 b";
        }
        String p = payload.trim();
        String lower = p.toLowerCase(Locale.ROOT);
        if (lower.equals("list") || lower.startsWith("list:")) {
            return vaultList();
        }
        int colon = p.indexOf(':');
        if (colon < 0) {
            return "Formato: vault:archive:&lt;planeta&gt; | vault:list | vault:show:&lt;planeta&gt; | vault:delete:&lt;planeta&gt;";
        }
        String action = p.substring(0, colon).trim().toLowerCase(Locale.ROOT);
        String rest = p.substring(colon + 1).trim();
        return switch (action) {
            case "archive" -> vaultArchive(rest);
            case "show" -> vaultShow(rest);
            case "delete" -> vaultDelete(rest);
            case "list" -> vaultList();
            default -> "Ação vault desconhecida: " + NasaTapClient.escape(action);
        };
    }

    static List<String> parseCompareNames(String raw) {
        List<String> out = new ArrayList<>();
        if (raw == null || raw.isBlank()) {
            return out;
        }
        String s = raw.trim();
        if (s.contains("|")) {
            for (String part : s.split("\\|")) {
                String t = part.trim();
                if (!t.isEmpty()) {
                    out.add(t);
                }
            }
            return out;
        }
        // "a AND b AND c" or "a and b"
        String[] parts = s.split("(?i)\\s+and\\s+");
        for (String part : parts) {
            String t = part.trim();
            if (!t.isEmpty()) {
                out.add(t);
            }
        }
        return out;
    }

    private String formatDeepScan(JSONObject row) {
        String name = NasaTapClient.optString(row, "pl_name");
        String host = NasaTapClient.optString(row, "hostname");
        Double eqt = NasaTapClient.optDouble(row, "pl_eqt");
        Double radius = NasaTapClient.optDouble(row, "pl_rade");
        Double mass = NasaTapClient.optDouble(row, "pl_bmasse");
        Double orbsmax = NasaTapClient.optDouble(row, "pl_orbsmax");
        Double dist = NasaTapClient.optDouble(row, "sy_dist");
        Double orbper = NasaTapClient.optDouble(row, "pl_orbper");
        Double dens = NasaTapClient.optDouble(row, "pl_dens");
        Double insol = NasaTapClient.optDouble(row, "pl_insol");
        String method = NasaTapClient.optString(row, "discoverymethod");
        Integer year = NasaTapClient.optInt(row, "disc_year");
        String facility = NasaTapClient.optString(row, "disc_facility");
        Double steff = NasaTapClient.optDouble(row, "st_teff");
        String spectype = NasaTapClient.optString(row, "st_spectype");
        Double gravity = NasaTapClient.estimateGravity(mass, radius);
        String habitability = NasaTapClient.classifyHabitability(eqt);

        StringBuilder sb = new StringBuilder();
        sb.append("OBSERVATORY DEEP SCAN // Chronos Observatory Channel — <strong>")
                .append(NasaTapClient.escape(name)).append("</strong><br>");
        if (!host.isEmpty()) {
            sb.append("Host: ").append(NasaTapClient.escape(host)).append("<br>");
        }
        sb.append("<br><strong>Parâmetros planetários (TAP)</strong><br>");
        sb.append("T_eq: ").append(NasaTapClient.fmt(eqt, " K")).append("<br>");
        sb.append("Raio: ").append(NasaTapClient.fmt(radius, " R⊕")).append("<br>");
        sb.append("Massa: ").append(NasaTapClient.fmt(mass, " M⊕")).append("<br>");
        sb.append("Densidade: ").append(NasaTapClient.fmt(dens, " g/cm³")).append("<br>");
        sb.append("Insolação: ").append(NasaTapClient.fmt(insol, " S⊕")).append("<br>");
        sb.append("Período orbital: ").append(NasaTapClient.fmt(orbper, " d")).append("<br>");
        sb.append("Semi-eixo: ").append(NasaTapClient.fmt(orbsmax, " AU")).append("<br>");
        sb.append("Distância: ").append(NasaTapClient.fmt(dist, " pc")).append("<br>");
        sb.append("g estimado (M/R²): ").append(NasaTapClient.fmt(gravity, " g")).append("<br>");
        sb.append("HZ proxy (T_eq): <strong>").append(habitability).append("</strong><br>");

        sb.append("<br><strong>Descoberta / estrela (TAP)</strong><br>");
        sb.append("Método: ").append(NasaTapClient.displayOrNd(method)).append("<br>");
        sb.append("Ano: ").append(NasaTapClient.fmtInt(year)).append("<br>");
        sb.append("Instalação: ").append(NasaTapClient.displayOrNd(facility)).append("<br>");
        sb.append("Teff estrela: ").append(NasaTapClient.fmt(steff, " K")).append("<br>");
        sb.append("Tipo espectral: ").append(NasaTapClient.displayOrNd(spectype)).append("<br>");
        sb.append("<br><em>Campos em falta = n/d. g e HZ são estimados/proxy — não inventados fora de TAP.</em>");
        return sb.toString();
    }

    private String formatTapVsAssumed(VaultEntry e) {
        StringBuilder sb = new StringBuilder();
        sb.append("<em>TAP → g (est. M/R²), T_eq");
        if (e.isTemperatureDefaulted()) {
            sb.append(" n/d — default 15°C usado");
        } else {
            sb.append(" → °C");
        }
        sb.append(". Assumidos (não observados): água ")
                .append(String.format(Locale.US, "%.0f%%", e.getWaterPercent()))
                .append(", atmosfera ").append(NasaTapClient.escape(e.getAtmosphere()))
                .append(", gerações ").append(e.getGenerations())
                .append(".</em><br>");
        return sb.toString();
    }

    private String formatTapSnapshotBrief(JSONObject row) {
        Double eqt = NasaTapClient.optDouble(row, "pl_eqt");
        Double mass = NasaTapClient.optDouble(row, "pl_bmasse");
        Double radius = NasaTapClient.optDouble(row, "pl_rade");
        return "T_eq " + NasaTapClient.fmt(eqt, " K")
                + " · M " + NasaTapClient.fmt(mass, " M⊕")
                + " · R " + NasaTapClient.fmt(radius, " R⊕")
                + "<br>";
    }

    private String notFound(String planetQuery) {
        return "Capitão, nenhum exoplaneta correspondente a <strong>"
                + NasaTapClient.escape(planetQuery)
                + "</strong> na NASA Exoplanet Archive.";
    }
}
