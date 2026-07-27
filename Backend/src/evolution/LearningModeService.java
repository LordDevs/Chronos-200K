package evolution;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Learning Mode v2 — save / load / evolve colony terraforming profiles.
 */
public class LearningModeService {
    private final LearningStore store;
    private final EvolutionEngine evolutionEngine;

    public LearningModeService() {
        this(LearningStore.getInstance(), new EvolutionEngine());
    }

    public LearningModeService(LearningStore store, EvolutionEngine evolutionEngine) {
        this.store = store;
        this.evolutionEngine = evolutionEngine;
    }

    public String handle(String action, String payload) {
        if (action == null || action.isBlank()) {
            return helpHtml();
        }
        return switch (action.trim().toLowerCase(Locale.ROOT)) {
            case "save" -> saveProfile(payload);
            case "list" -> listProfiles();
            case "load" -> loadProfile(payload);
            case "delete" -> deleteProfile(payload);
            case "evolve" -> evolveProfile(payload);
            case "help" -> helpHtml();
            default -> "Capitão, ação Learning Mode desconhecida: <strong>" + action + "</strong>. "
                    + helpHtml();
        };
    }

    public String saveProfile(String raw) {
        ParsedSave parsed = parseSave(raw);
        if (parsed.error != null) {
            return parsed.error;
        }
        ColonyProfile saved = store.save(new ColonyProfile(parsed.name, parsed.profile));
        return "COLONY ARCHIVE // perfil guardado<br>"
                + profileSummaryHtml(saved)
                + "<br>Use <code>LEARNING MODE EVOLVE " + saved.getName() + "</code> para simular.";
    }

    public String listProfiles() {
        List<ColonyProfile> profiles = store.list();
        if (profiles.isEmpty()) {
            return "COLONY ARCHIVE // vazio<br>"
                    + "Capitão, ainda não há perfis de terraformação.<br>"
                    + "Exemplo: <code>LEARNING MODE SAVE mars-colony gravity 0.38 water 10 temp -60 co2 generations 2000</code>";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("COLONY ARCHIVE // ").append(profiles.size()).append(" perfil(is)<br><br>");
        for (ColonyProfile profile : profiles) {
            sb.append("• <strong>").append(profile.getName()).append("</strong> — ");
            sb.append(formatEnvLine(profile.getEnvironment()));
            sb.append("<br>");
        }
        return sb.toString();
    }

    public String loadProfile(String name) {
        Optional<ColonyProfile> profile = store.get(name);
        if (profile.isEmpty()) {
            return "Perfil colonial não encontrado: <strong>" + ColonyProfile.normalizeName(name)
                    + "</strong>. Use LEARNING MODE LIST.";
        }
        return "COLONY ARCHIVE // perfil carregado<br>" + profileSummaryHtml(profile.get());
    }

    public String deleteProfile(String name) {
        String key = ColonyProfile.normalizeName(name);
        if (key.isBlank()) {
            return "Especifique o nome: LEARNING MODE DELETE &lt;nome&gt;";
        }
        if (!store.delete(key)) {
            return "Perfil não encontrado: <strong>" + key + "</strong>";
        }
        return "COLONY ARCHIVE // perfil <strong>" + key + "</strong> eliminado.";
    }

    public String evolveProfile(String name) {
        Optional<ColonyProfile> profile = store.get(name);
        if (profile.isEmpty()) {
            return "Sem perfil guardado para <strong>" + ColonyProfile.normalizeName(name)
                    + "</strong>. Guarde primeiro com LEARNING MODE SAVE.";
        }
        EvolutionReport report = evolutionEngine.simulate(profile.get().getEnvironment());
        return "LEARNING MODE v2 // evolução de <strong>" + profile.get().getName() + "</strong><br>"
                + report.toHtml();
    }

    private String profileSummaryHtml(ColonyProfile profile) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                .withZone(ZoneId.systemDefault());
        return "<strong>" + profile.getName() + "</strong><br>"
                + formatEnvLine(profile.getEnvironment())
                + "<br>Registado: " + fmt.format(profile.getSavedAt());
    }

    private static String formatEnvLine(EnvironmentProfile env) {
        return String.format(Locale.US,
                "g=%.2f · água=%.0f%% · temp=%.0f°C · %s · %d gerações",
                env.getGravityG(),
                env.getWaterPercent(),
                env.getTemperatureC(),
                env.getAtmosphere(),
                env.getGenerations());
    }

    private static String helpHtml() {
        return "Comandos Learning Mode v2:<br>"
                + "• <code>LEARNING MODE SAVE &lt;nome&gt; gravity 2g water 80 temp 15 generations 1000</code><br>"
                + "• <code>LEARNING MODE LIST</code><br>"
                + "• <code>LEARNING MODE LOAD &lt;nome&gt;</code><br>"
                + "• <code>LEARNING MODE EVOLVE &lt;nome&gt;</code><br>"
                + "• <code>LEARNING MODE DELETE &lt;nome&gt;</code>";
    }

    private ParsedSave parseSave(String raw) {
        if (raw == null || raw.isBlank()) {
            return ParsedSave.error("Formato: LEARNING MODE SAVE &lt;nome&gt; gravity 2g water 80 generations 1000");
        }
        String trimmed = raw.trim();
        int space = trimmed.indexOf(' ');
        if (space < 0) {
            return ParsedSave.error("Inclua parâmetros após o nome: gravity, water, temp, atmosphere, generations.");
        }
        String name = ColonyProfile.normalizeName(trimmed.substring(0, space));
        String params = trimmed.substring(space + 1).trim();
        if (name.isBlank()) {
            return ParsedSave.error("Nome da colónia inválido.");
        }
        EnvironmentProfile profile = EnvironmentProfile.parse(params);
        return new ParsedSave(name, profile, null);
    }

    private static final class ParsedSave {
        final String name;
        final EnvironmentProfile profile;
        final String error;

        private ParsedSave(String name, EnvironmentProfile profile, String error) {
            this.name = name;
            this.profile = profile;
            this.error = error;
        }

        static ParsedSave error(String message) {
            return new ParsedSave(null, null, message);
        }
    }
}
