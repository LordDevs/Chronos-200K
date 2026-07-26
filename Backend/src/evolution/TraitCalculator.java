package evolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic v1 trait rules driven by environmental variables.
 */
public class TraitCalculator {

    public List<String> calculate(EnvironmentProfile env) {
        List<String> traits = new ArrayList<>();
        double g = env.getGravityG();
        double water = env.getWaterPercent();
        double temp = env.getTemperatureC();
        String atm = env.getAtmosphere().toLowerCase();
        int gens = env.getGenerations();
        double intensity = Math.min(3.0, Math.log10(gens + 1));

        if (g > 1.5) {
            traits.add(String.format(
                    "Densidade óssea elevada (+%.0f%%) e estatura média reduzida — adaptação a %.1fg",
                    15 * intensity, g));
            traits.add("Massa muscular hipertrofiada; articulações reforçadas (matriz esquelética Astartes)");
        } else if (g < 0.7) {
            traits.add(String.format(
                    "Ossatura mais leve e membros alongados — microgravidade relativa (%.2fg)", g));
            traits.add("Sistema vestibular recalibrado; risco de perda muscular sem estímulo artificial");
        } else {
            traits.add("Morfologia basal preservada sob gravidade próxima da terrestre");
        }

        if (water > 70) {
            traits.add("Adaptações aquáticas: membranas interdigitais e capacidade pulmonar parcial submersa");
            traits.add("Pele com secreção lipídica para isolamento em ocean worlds");
        } else if (water < 25) {
            traits.add("Retenção hídrica extrema; rins hiper-eficientes e pele espessa/cerosa");
        }

        if (temp < -50) {
            traits.add("Metabolismo lento e isolamento térmico subcutâneo (crio-adaptação)");
        } else if (temp > 45) {
            traits.add("Dissipação térmica ampliada: vasocontraste periférico e menor densidade pilosidade");
        }

        if (atm.contains("co2")) {
            traits.add("Pulmões / hemoglobina modificados para atmosferas ricas em CO2");
            traits.add("Nanorrede neural de alerta hipóxico (kit biomecânico)");
        } else if (atm.contains("thin")) {
            traits.add("Capacidade aeróbica expandida; peito amplo sob pressão atmosférica baixa");
        } else if (atm.contains("ch4")) {
            traits.add("Detoxificação hepática reforçada contra hidrocarbonetos atmosféricos");
        }

        if (gens >= 5000) {
            traits.add("Sinais de especiação morfologica após " + gens + " gerações isoladas");
        } else {
            traits.add("Deriva controlada em " + gens + " gerações — ainda compatível com geno-reserva humana");
        }

        return traits;
    }

    public double survivalProbability(EnvironmentProfile env) {
        double score = 0.75;
        if (env.getGravityG() > 2.5 || env.getGravityG() < 0.2) {
            score -= 0.25;
        } else if (env.getGravityG() > 1.5 || env.getGravityG() < 0.5) {
            score -= 0.1;
        }
        if (env.getWaterPercent() < 10 || env.getWaterPercent() > 95) {
            score -= 0.15;
        }
        if (env.getTemperatureC() < -80 || env.getTemperatureC() > 60) {
            score -= 0.2;
        }
        if (env.getAtmosphere().toLowerCase().contains("co2")
                || env.getAtmosphere().toLowerCase().contains("thin")) {
            score -= 0.1;
        }
        score += Math.min(0.1, Math.log10(env.getGenerations() + 1) / 40.0);
        return Math.max(0.05, Math.min(0.95, score));
    }
}
