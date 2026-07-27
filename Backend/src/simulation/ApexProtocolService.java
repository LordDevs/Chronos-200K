package simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Chronos Apex Protocol (CAP) — biomechanical enhancement blueprints. */
public class ApexProtocolService {

    public String generateBlueprint(String gravityToken, String atmosphereToken) {
        double gravity = PhysicsCatalog.parseGravityToken(gravityToken, PhysicsCatalog.EARTH_G);
        String atmosphere = atmosphereToken == null || atmosphereToken.isBlank()
                ? "N2-O2"
                : atmosphereToken.trim();

        List<String> implants = new ArrayList<>();
        List<String> organs = new ArrayList<>();

        if (gravity >= 2.0) {
            implants.add("Matriz esquelética de fibra de carbono (densidade óssea +65%)");
            organs.add("Coração auxiliar de dupla câmara — fluxo paralelo para 2.0g+");
            organs.add("Válvulas aórticas reforçadas em titânio biocompatível");
        } else if (gravity >= 1.5) {
            implants.add("Endoesqueleto híbrido: osso autólogo + nanotubos");
            organs.add("Músculo sintético de backup (grupos posteriores)");
        } else if (gravity < 0.5) {
            implants.add("Exoesqueleto de suporte articulado (modo Marte/Lua)");
            organs.add("Estimuladores miogénicos anti-atrofia contínua");
        } else {
            implants.add("Placa subcutânea opcional — não crítica em "
                    + String.format(Locale.US, "%.2fg", gravity));
        }

        String atmLower = atmosphere.toLowerCase(Locale.ROOT);
        if (atmLower.contains("co2")) {
            organs.add("Pulmões bioengenheirados com enzima CA-II amplificada");
            organs.add("Hemoglobina de alta afinidade (variante Andean-synthetic)");
            implants.add("Nanorrede neural hipóxica — alerta em &lt; 85% SpO2");
        }
        if (atmLower.contains("thin") || atmLower.contains("mars")) {
            organs.add("Alvéolos expandidos + compressor torácico mecânico interno");
        }
        if (atmLower.contains("ocean") || atmLower.contains("humid")) {
            organs.add("Brânquias auxiliares descartáveis (cápsulas de 72h)");
            implants.add("Membrana timpânica líquida — equilíbrio pressão abissal");
        }

        double survival = estimateSurvivalWithProtocol(gravity, atmosphere);

        StringBuilder sb = new StringBuilder();
        sb.append("CHRONOS APEX PROTOCOL // biomechanical blueprint<br>");
        sb.append(String.format(Locale.US,
                "Perfil alvo: <strong>%.2fg</strong> · Atmosfera: <strong>%s</strong><br><br>",
                gravity, escape(atmosphere)));

        sb.append("<strong>Implantes estruturais:</strong><br>");
        for (String item : implants) {
            sb.append("• ").append(item).append("<br>");
        }
        sb.append("<br><strong>Órgãos sintéticos:</strong><br>");
        for (String item : organs) {
            sb.append("• ").append(item).append("<br>");
        }

        sb.append("<br>Probabilidade de sobrevivência colonial <em>com Protocolo Apex</em>: <strong>")
                .append(String.format(Locale.US, "%.0f%%", survival * 100))
                .append("</strong><br>");
        sb.append("<em>Capitão, blueprints CAP prontos para fabricação em nano-forja de bordo.</em>");

        return sb.toString();
    }

    private double estimateSurvivalWithProtocol(double gravity, String atmosphere) {
        double base = 0.55;
        if (gravity <= 2.5 && gravity >= 0.3) {
            base += 0.25;
        }
        if (gravity > 2.5) {
            base += 0.10;
        }
        String atm = atmosphere.toLowerCase(Locale.ROOT);
        if (!atm.contains("co2") && !atm.contains("thin")) {
            base += 0.10;
        } else {
            base += 0.05;
        }
        return Math.min(0.92, base);
    }

    private static String escape(String s) {
        return s.replace("<", "&lt;").replace(">", "&gt;");
    }
}
