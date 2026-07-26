package evolution;

/**
 * Environmental inputs for deep-time evolutionary forecasting.
 */
public class EnvironmentProfile {
    private final double gravityG;
    private final double waterPercent;
    private final double temperatureC;
    private final String atmosphere;
    private final int generations;

    public EnvironmentProfile(
            double gravityG,
            double waterPercent,
            double temperatureC,
            String atmosphere,
            int generations) {
        this.gravityG = gravityG;
        this.waterPercent = waterPercent;
        this.temperatureC = temperatureC;
        this.atmosphere = atmosphere == null ? "N2-O2" : atmosphere;
        this.generations = Math.max(1, generations);
    }

    public double getGravityG() {
        return gravityG;
    }

    public double getWaterPercent() {
        return waterPercent;
    }

    public double getTemperatureC() {
        return temperatureC;
    }

    public String getAtmosphere() {
        return atmosphere;
    }

    public int getGenerations() {
        return generations;
    }

    /**
     * Parses compact tokens from AIML/MessageHandler:
     * evolve:2g:80:1000
     * evolve:gravity 2g water 80 generations 1000
     * evolve:2:80:1000
     */
    public static EnvironmentProfile parse(String raw) {
        String s = raw == null ? "" : raw.trim().toLowerCase();
        double gravity = 1.0;
        double water = 50.0;
        double temp = 15.0;
        String atmosphere = "N2-O2";
        int generations = 1000;

        if (s.contains("co2")) {
            atmosphere = "CO2-rich";
        } else if (s.contains("methane") || s.contains("ch4")) {
            atmosphere = "CH4-rich";
        } else if (s.contains("thin")) {
            atmosphere = "thin";
        }

        // Token form: 2g:80:1000 or 2:80:1000
        String[] colon = s.split(":");
        if (colon.length >= 3 && !s.contains("gravity") && !s.contains("water")) {
            gravity = parseGravityToken(colon[0]);
            water = parseNumber(colon[1], water);
            generations = (int) parseNumber(colon[2], generations);
            if (colon.length >= 4) {
                temp = parseNumber(colon[3], temp);
            }
            return new EnvironmentProfile(gravity, water, temp, atmosphere, generations);
        }

        gravity = extractAfter(s, new String[] {"gravity", "g"}, gravity);
        // patterns like "2g"
        java.util.regex.Matcher gm = java.util.regex.Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*g\\b").matcher(s);
        if (gm.find()) {
            gravity = Double.parseDouble(gm.group(1));
        }
        water = extractAfter(s, new String[] {"water", "h2o"}, water);
        generations = (int) extractAfter(s, new String[] {"generations", "gens", "gen"}, generations);
        temp = extractAfter(s, new String[] {"temp", "temperature", "c"}, temp);

        return new EnvironmentProfile(gravity, water, temp, atmosphere, generations);
    }

    private static double parseGravityToken(String token) {
        String t = token.trim().replace("g", "");
        return parseNumber(t, 1.0);
    }

    private static double parseNumber(String token, double fallback) {
        try {
            String cleaned = token.trim().replace("%", "").replace("g", "");
            return Double.parseDouble(cleaned);
        } catch (Exception e) {
            return fallback;
        }
    }

    private static double extractAfter(String s, String[] keys, double fallback) {
        for (String key : keys) {
            java.util.regex.Matcher m = java.util.regex.Pattern
                    .compile(key + "\\s*[:=]?\\s*(\\d+(?:\\.\\d+)?)")
                    .matcher(s);
            if (m.find()) {
                return Double.parseDouble(m.group(1));
            }
        }
        return fallback;
    }
}
