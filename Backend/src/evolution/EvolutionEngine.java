package evolution;

import java.util.List;

/**
 * Orchestrates deterministic evolutionary forecasting (Learning Mode v1).
 */
public class EvolutionEngine {

    private final TraitCalculator calculator = new TraitCalculator();

    public EvolutionReport simulate(EnvironmentProfile profile) {
        List<String> traits = calculator.calculate(profile);
        double survival = calculator.survivalProbability(profile);
        return new EvolutionReport(profile, traits, survival);
    }

    public String simulateFromRaw(String rawTokens) {
        EnvironmentProfile profile = EnvironmentProfile.parse(rawTokens);
        return simulate(profile).toHtml();
    }
}
