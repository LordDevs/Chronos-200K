package simulation;

import evolution.EnvironmentProfile;
import evolution.EvolutionEngine;
import evolution.EvolutionReport;
import java.util.Locale;

/** Deep-time human speciation forecasts (up to 200,000 years). */
public class SpeciationService {

    private final EvolutionEngine evolutionEngine = new EvolutionEngine();

    public String predictSpeciation(long years, String biomeKey) {
        long cappedYears = Math.min(200_000L, Math.max(1L, years));
        int generations = yearsToGenerations(cappedYears);

        PhysicsCatalog.WorldPreset preset = resolveBiome(biomeKey);
        EnvironmentProfile env = new EnvironmentProfile(
                preset.gravityG(),
                preset.waterPercent(),
                preset.temperatureC(),
                preset.atmosphere(),
                generations);

        EvolutionReport report = evolutionEngine.simulate(env);

        StringBuilder sb = new StringBuilder();
        sb.append("SPECIATION FORECAST // deep-time<br>");
        sb.append(String.format(Locale.US,
                "Horizonte: <strong>%,d anos</strong> (~%,d gerações) · Bioma: <strong>%s</strong><br><br>",
                cappedYears, generations, preset.label()));

        sb.append("<strong>Fases morfológicas:</strong><br>");
        sb.append(describePhases(cappedYears, biomeKey));
        sb.append("<br>").append(report.toHtml());

        if (cappedYears >= 50_000) {
            sb.append("<br><br>⚠️ <strong>Alerta CHRONOS:</strong> isolamento reprodutivo provável — subespécie colonial distinta da Terra.");
        }

        return sb.toString();
    }

    private PhysicsCatalog.WorldPreset resolveBiome(String biomeKey) {
        if (biomeKey == null || biomeKey.isBlank() || "baseline".equalsIgnoreCase(biomeKey)) {
            return new PhysicsCatalog.WorldPreset(
                    "Baseline colony", PhysicsCatalog.EARTH_G, 60.0, 15.0, "N2-O2", 50.0);
        }
        PhysicsCatalog.WorldPreset preset = PhysicsCatalog.get(biomeKey);
        return preset != null ? preset : PhysicsCatalog.get("jungle");
    }

    /** ~25 years per generation (colonial demographic model). */
    static int yearsToGenerations(long years) {
        return (int) Math.max(1, years / 25L);
    }

    private String describePhases(long years, String biome) {
        StringBuilder sb = new StringBuilder();
        if (years >= 500) {
            sb.append("• Fase I (0–500a): adaptações fisiológicas reversíveis; Protocolo Apex suficiente.<br>");
        }
        if (years >= 5_000) {
            sb.append("• Fase II (500–5ka): deriva genética mensurável; seleção por gravidade/atmosfera.<br>");
        }
        if (years >= 50_000) {
            sb.append("• Fase III (5–50ka): morfotipos estáveis; incompatabilidade parcial com Homo sapiens basal.<br>");
        }
        if (years >= 200_000) {
            sb.append("• Fase IV (50–200ka): <strong>especiação</strong> — nova designação taxonómica colonial recomendada.<br>");
        }
        if ("ocean".equalsIgnoreCase(biome)) {
            sb.append("• Traço oceânico: cetáceo-humanoide, pele laminar, metabolismo salino.<br>");
        } else if ("jungle".equalsIgnoreCase(biome)) {
            sb.append("• Traço de jungla: arborícola, preensilismo expandido, visão tetracromática.<br>");
        } else if ("super-earth".equalsIgnoreCase(biome)) {
            sb.append("• Traço super-Terra: massa corporal +35%, centro de gravidade rebaixado.<br>");
        } else if ("mars".equalsIgnoreCase(biome)) {
            sb.append("• Traço marciano: ossatura leve, estatura +12%, metabolismo de conservação.<br>");
        }
        return sb.toString();
    }
}
