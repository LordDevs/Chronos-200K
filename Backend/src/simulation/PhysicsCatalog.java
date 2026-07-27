package simulation;

import java.util.Locale;
import java.util.Map;

/**
 * Hardcoded reference physics for colonial deployment scenarios.
 */
public final class PhysicsCatalog {

    public static final double EARTH_G = 1.0;
    public static final double MARS_G = 0.38;
    public static final double MOON_G = 0.17;
    public static final double SUPER_EARTH_G = 2.5;
    public static final double JUNGLE_WORLD_G = 1.2;
    public static final double OCEAN_WORLD_G = 1.1;

    private static final Map<String, WorldPreset> PRESETS = Map.of(
            "mars", new WorldPreset("Mars (reference)", MARS_G, 0.0, -60.0, "CO2-thin", 15.0),
            "super-earth", new WorldPreset("Super-Earth (reference)", SUPER_EARTH_G, 30.0, 22.0, "N2-O2-dense", 40.0),
            "jungle", new WorldPreset("Hyper-dense Jungle World", JUNGLE_WORLD_G, 55.0, 32.0, "O2-humid", 85.0),
            "ocean", new WorldPreset("Ocean World", OCEAN_WORLD_G, 92.0, 8.0, "N2-O2-mist", 95.0)
    );

    private PhysicsCatalog() {
    }

    public static WorldPreset get(String key) {
        if (key == null) {
            return null;
        }
        return PRESETS.get(key.trim().toLowerCase(Locale.ROOT));
    }

    public static double parseGravityToken(String token, double fallback) {
        if (token == null || token.isBlank()) {
            return fallback;
        }
        String cleaned = token.trim().toLowerCase(Locale.ROOT).replace("g", "");
        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            WorldPreset preset = get(cleaned);
            return preset != null ? preset.gravityG() : fallback;
        }
    }

    public record WorldPreset(
            String label,
            double gravityG,
            double waterPercent,
            double temperatureC,
            String atmosphere,
            double humidityPercent) {
    }
}
