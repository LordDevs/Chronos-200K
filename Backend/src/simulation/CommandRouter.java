package simulation;

import java.util.Locale;

/**
 * Intercepts AIML routing tokens and delegates to simulation services.
 *
 * <p>Protocol:
 * <ul>
 *   <li>{@code simulate:jungle[:gravity]} → BiomeSimulationService</li>
 *   <li>{@code astartes[:gravity][:atmosphere]} → AstartesKitService</li>
 *   <li>{@code speciate:200000[:biome]} → SpeciationService</li>
 * </ul>
 */
public class CommandRouter {

    private final BiomeSimulationService biomeService = new BiomeSimulationService();
    private final AstartesKitService astartesService = new AstartesKitService();
    private final SpeciationService speciationService = new SpeciationService();

    /** @return HTML response if handled; {@code null} otherwise */
    public String route(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return null;
        }

        String token = rawToken.trim();
        String lower = token.toLowerCase(Locale.ROOT);

        if (lower.startsWith("simulate:")) {
            return routeSimulate(token.substring("simulate:".length()));
        }
        if (lower.startsWith("astartes:")) {
            return routeAstartes(token.substring("astartes:".length()));
        }
        if (lower.startsWith("speciate:")) {
            return routeSpeciate(token.substring("speciate:".length()));
        }

        return null;
    }

    private String routeSimulate(String payload) {
        String[] parts = payload.split(":", 2);
        String biome = parts[0].trim();
        String gravityOverride = parts.length > 1 ? parts[1].trim() : "";
        return biomeService.analyzeBiome(biome, gravityOverride);
    }

    private String routeAstartes(String payload) {
        String[] parts = payload.split(":", 2);
        String gravity = parts[0].trim();
        String atmosphere = parts.length > 1 ? parts[1].trim() : "N2-O2";
        return astartesService.generateBlueprint(gravity, atmosphere);
    }

    private String routeSpeciate(String payload) {
        String[] parts = payload.split(":", 2);
        long years;
        try {
            years = Long.parseLong(parts[0].trim().replace("k", "000").replace("K", "000"));
        } catch (NumberFormatException e) {
            years = 200_000L;
        }
        String biome = parts.length > 1 ? parts[1].trim() : "baseline";
        return speciationService.predictSpeciation(years, biome);
    }
}
