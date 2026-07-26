package evolution;

import java.util.List;
import java.util.Locale;

public class EvolutionReport {
    private final EnvironmentProfile profile;
    private final List<String> traits;
    private final double survivalProbability;

    public EvolutionReport(EnvironmentProfile profile, List<String> traits, double survivalProbability) {
        this.profile = profile;
        this.traits = traits;
        this.survivalProbability = survivalProbability;
    }

    public String toHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append("EVOLUTION FORECAST // deep-time v1<br>");
        sb.append(String.format(Locale.US,
                "Input — gravidade: %.2fg · água: %.0f%% · temp: %.0f°C · atmosfera: %s · gerações: %d<br><br>",
                profile.getGravityG(),
                profile.getWaterPercent(),
                profile.getTemperatureC(),
                profile.getAtmosphere(),
                profile.getGenerations()));
        sb.append("Adaptações previstas:<br>");
        for (String trait : traits) {
            sb.append("• ").append(trait).append("<br>");
        }
        sb.append("<br>Probabilidade de sobrevivência colonial: <strong>")
                .append(String.format(Locale.US, "%.0f%%", survivalProbability * 100))
                .append("</strong>");
        return sb.toString();
    }
}
