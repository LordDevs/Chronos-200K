package simulation;

import evolution.EnvironmentProfile;
import evolution.EvolutionEngine;
import java.util.Locale;

/** Exoplanet logistics: jungle vs ocean archetypes with gravity/implant impact logs. */
public class BiomeSimulationService {

    private final EvolutionEngine evolutionEngine = new EvolutionEngine();

    public String analyzeBiome(String biomeKey, String gravityOverride) {
        PhysicsCatalog.WorldPreset preset = PhysicsCatalog.get(biomeKey);
        if (preset == null) {
            return "Capitão, bioma desconhecido: <strong>" + escape(biomeKey)
                    + "</strong>. Use jungle, ocean, mars, ou super-earth.";
        }

        double gravity = PhysicsCatalog.parseGravityToken(gravityOverride, preset.gravityG());
        EnvironmentProfile env = new EnvironmentProfile(
                gravity,
                preset.waterPercent(),
                preset.temperatureC(),
                preset.atmosphere(),
                1000);

        StringBuilder sb = new StringBuilder();
        sb.append("LOGISTICS // ").append(escape(preset.label())).append("<br>");
        sb.append(String.format(Locale.US,
                "Gravidade: <strong>%.2fg</strong> · Água superficial: %.0f%% · Temp: %.0f°C · Atmosfera: %s<br>",
                gravity, preset.waterPercent(), preset.temperatureC(), preset.atmosphere()));
        sb.append("Humidade ambiente: ")
                .append(String.format(Locale.US, "%.0f%%", preset.humidityPercent()))
                .append("<br><br>");

        if ("jungle".equalsIgnoreCase(biomeKey)) {
            appendJungleLog(sb, gravity);
        } else if ("ocean".equalsIgnoreCase(biomeKey)) {
            appendOceanLog(sb, gravity);
        } else if ("mars".equalsIgnoreCase(biomeKey)) {
            appendMarsLog(sb);
        } else if ("super-earth".equalsIgnoreCase(biomeKey)) {
            appendSuperEarthLog(sb, gravity);
        }

        sb.append("<br>").append(evolutionEngine.simulate(env).toHtml());
        return sb.toString();
    }

    private void appendJungleLog(StringBuilder sb, double gravity) {
        sb.append("<strong>Impacto gravitacional (jungla hiper-densa):</strong><br>");
        if (gravity > 1.3) {
            sb.append("• Coluna vertebral comprimida; lombares reforçados com matriz de carbono.<br>");
            sb.append("• Membros inferiores hipertrofiados para escalada em dossel de 80m+.<br>");
        } else {
            sb.append("• Estatura preservada; adaptação pulmonar a spores e O2 saturado.<br>");
        }
        sb.append("• Implantes: filtros alveolares nano-porosos, retina anti-UV canopy.<br>");
        sb.append("• Mutações: pele fotossintética residual, unhas ósseas para ancoragem em lianas.<br>");
    }

    private void appendOceanLog(StringBuilder sb, double gravity) {
        sb.append("<strong>Impacto gravitacional (ocean world):</strong><br>");
        sb.append("• Membranas interdigitais para correntes de ")
                .append(String.format(Locale.US, "%.2fg", gravity)).append(".<br>");
        sb.append("• Implantes: brânquias auxiliares sintéticas, ossos de densidade variável.<br>");
        sb.append("• Coração auxiliar (classe Apex) para mergulhos abissais &gt; 400m.<br>");
        sb.append("• Mutações: olhos adaptados a baixa luminosidade; metabolismo cetáceo parcial.<br>");
    }

    private void appendMarsLog(StringBuilder sb) {
        sb.append("<strong>Mars reference (0.38g):</strong><br>");
        sb.append("• Perda óssea acelerada sem exercício resistido.<br>");
        sb.append("• Implantes: exoesqueleto de suporte, pulmões com scrubber de regolito.<br>");
        sb.append("• Sobrevivência colonial sem kit: <strong>baixa</strong> — habitat pressurizado obrigatório.<br>");
    }

    private void appendSuperEarthLog(StringBuilder sb, double gravity) {
        sb.append("<strong>Super-Earth logistics (")
                .append(String.format(Locale.US, "%.2fg", gravity)).append("):</strong><br>");
        sb.append("• Esqueleto de matriz de carbono obrigatório acima de 1.8g.<br>");
        sb.append("• Coração auxiliar e válvulas reforçadas.<br>");
        sb.append("• Mutações: estatura -18%, densidade muscular +40%.<br>");
    }

    private static String escape(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("<", "&lt;").replace(">", "&gt;");
    }
}
