package evolution;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Archived NASA-anchored speciation forecast for one planet.
 */
public class VaultEntry {
    private final String planetName;
    private final JSONObject tapSnapshot;
    private final double gravityG;
    private final double temperatureC;
    private final boolean temperatureDefaulted;
    private final double waterPercent;
    private final String atmosphere;
    private final int generations;
    private final List<String> traits;
    private final double survivalProbability;
    private final String forecastHtml;
    private final long savedAt;

    public VaultEntry(
            String planetName,
            JSONObject tapSnapshot,
            double gravityG,
            double temperatureC,
            boolean temperatureDefaulted,
            double waterPercent,
            String atmosphere,
            int generations,
            List<String> traits,
            double survivalProbability,
            String forecastHtml,
            long savedAt) {
        this.planetName = normalizeKey(planetName);
        this.tapSnapshot = copyJson(tapSnapshot);
        this.gravityG = gravityG;
        this.temperatureC = temperatureC;
        this.temperatureDefaulted = temperatureDefaulted;
        this.waterPercent = waterPercent;
        this.atmosphere = atmosphere == null ? "N2-O2" : atmosphere;
        this.generations = generations;
        this.traits = traits == null ? List.of() : List.copyOf(traits);
        this.survivalProbability = survivalProbability;
        this.forecastHtml = forecastHtml == null ? "" : forecastHtml;
        this.savedAt = savedAt;
    }

    public static String normalizeKey(String name) {
        if (name == null) {
            return "";
        }
        return name.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }

    public String getPlanetName() {
        return planetName;
    }

    public JSONObject getTapSnapshot() {
        return copyJson(tapSnapshot);
    }

    public double getGravityG() {
        return gravityG;
    }

    public double getTemperatureC() {
        return temperatureC;
    }

    public boolean isTemperatureDefaulted() {
        return temperatureDefaulted;
    }

    public double getWaterPercent() {
        return waterPercent;
    }

    public String getAtmosphere() {
        return atmosphere;
    }

    public int getGenerations() {
        return generations;
    }

    public List<String> getTraits() {
        return traits;
    }

    public double getSurvivalProbability() {
        return survivalProbability;
    }

    public String getForecastHtml() {
        return forecastHtml;
    }

    public long getSavedAt() {
        return savedAt;
    }

    public JSONObject toJson() {
        JSONObject o = new JSONObject();
        try {
            o.put("planetName", planetName);
            o.put("tapSnapshot", tapSnapshot);
            o.put("gravityG", gravityG);
            o.put("temperatureC", temperatureC);
            o.put("temperatureDefaulted", temperatureDefaulted);
            o.put("waterPercent", waterPercent);
            o.put("atmosphere", atmosphere);
            o.put("generations", generations);
            o.put("traits", new JSONArray(traits));
            o.put("survivalProbability", survivalProbability);
            o.put("forecastHtml", forecastHtml);
            o.put("savedAt", savedAt);
        } catch (Exception e) {
            throw new IllegalStateException("Could not serialize vault entry", e);
        }
        return o;
    }

    public static VaultEntry fromJson(JSONObject o) {
        List<String> traits = new ArrayList<>();
        JSONArray arr = o.optJSONArray("traits");
        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                traits.add(arr.optString(i, ""));
            }
        }
        return new VaultEntry(
                o.optString("planetName", ""),
                o.optJSONObject("tapSnapshot"),
                o.optDouble("gravityG", 1.0),
                o.optDouble("temperatureC", 15.0),
                o.optBoolean("temperatureDefaulted", false),
                o.optDouble("waterPercent", 50.0),
                o.optString("atmosphere", "N2-O2"),
                o.optInt("generations", 1000),
                traits,
                o.optDouble("survivalProbability", 0.0),
                o.optString("forecastHtml", ""),
                o.optLong("savedAt", System.currentTimeMillis()));
    }

    private static JSONObject copyJson(JSONObject src) {
        if (src == null) {
            return new JSONObject();
        }
        try {
            return new JSONObject(src.toString());
        } catch (Exception e) {
            return new JSONObject();
        }
    }
}
