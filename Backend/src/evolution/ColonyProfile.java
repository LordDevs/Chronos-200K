package evolution;

import java.time.Instant;
import org.json.JSONObject;

/**
 * Named terraforming profile persisted in Learning Mode v2.
 */
public class ColonyProfile {
    private final String name;
    private final EnvironmentProfile environment;
    private final Instant savedAt;

    public ColonyProfile(String name, EnvironmentProfile environment, Instant savedAt) {
        this.name = name == null ? "" : name.trim().toLowerCase();
        this.environment = environment;
        this.savedAt = savedAt == null ? Instant.now() : savedAt;
    }

    public ColonyProfile(String name, EnvironmentProfile environment) {
        this(name, environment, Instant.now());
    }

    public String getName() {
        return name;
    }

    public EnvironmentProfile getEnvironment() {
        return environment;
    }

    public Instant getSavedAt() {
        return savedAt;
    }

    public JSONObject toJson() {
        JSONObject o = new JSONObject();
        try {
            o.put("name", name);
            o.put("gravityG", environment.getGravityG());
            o.put("waterPercent", environment.getWaterPercent());
            o.put("temperatureC", environment.getTemperatureC());
            o.put("atmosphere", environment.getAtmosphere());
            o.put("generations", environment.getGenerations());
            o.put("savedAt", savedAt.toString());
        } catch (Exception e) {
            throw new IllegalStateException("Could not serialize colony profile", e);
        }
        return o;
    }

    public static ColonyProfile fromJson(JSONObject o) {
        String name = o.optString("name", "");
        EnvironmentProfile env = new EnvironmentProfile(
                o.optDouble("gravityG", 1.0),
                o.optDouble("waterPercent", 50.0),
                o.optDouble("temperatureC", 15.0),
                o.optString("atmosphere", "N2-O2"),
                o.optInt("generations", 1000));
        Instant savedAt;
        try {
            savedAt = Instant.parse(o.optString("savedAt", Instant.now().toString()));
        } catch (Exception e) {
            savedAt = Instant.now();
        }
        return new ColonyProfile(name, env, savedAt);
    }

    public static String normalizeName(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.trim().toLowerCase().replaceAll("\\s+", "-");
    }
}
