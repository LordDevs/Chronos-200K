package evolution;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.json.JSONObject;

/**
 * File-backed Speciation Vault (NASA-anchored forecasts).
 */
public class SpeciationVaultStore {
    private static final SpeciationVaultStore DEFAULT =
            new SpeciationVaultStore(Path.of("Backend", "data", "speciation-vault.json"));

    private final Path dataFile;
    private final Map<String, VaultEntry> entries = new LinkedHashMap<>();

    public SpeciationVaultStore(Path dataFile) {
        this.dataFile = dataFile;
        loadFromDisk();
    }

    public static SpeciationVaultStore getInstance() {
        return DEFAULT;
    }

    public synchronized List<VaultEntry> list() {
        return new ArrayList<>(entries.values());
    }

    public synchronized Optional<VaultEntry> get(String planetName) {
        return Optional.ofNullable(entries.get(VaultEntry.normalizeKey(planetName)));
    }

    public synchronized VaultEntry save(VaultEntry entry) {
        String key = VaultEntry.normalizeKey(entry.getPlanetName());
        if (key.isBlank()) {
            throw new IllegalArgumentException("Planet name required");
        }
        entries.put(key, entry);
        persist();
        return entry;
    }

    public synchronized boolean delete(String planetName) {
        String key = VaultEntry.normalizeKey(planetName);
        if (!entries.containsKey(key)) {
            return false;
        }
        entries.remove(key);
        persist();
        return true;
    }

    private void loadFromDisk() {
        if (!Files.exists(dataFile)) {
            return;
        }
        try {
            String raw = Files.readString(dataFile, StandardCharsets.UTF_8);
            if (raw.isBlank()) {
                return;
            }
            JSONObject root = new JSONObject(raw);
            java.util.Iterator<String> keys = root.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject entry = root.optJSONObject(key);
                if (entry != null) {
                    entries.put(key, VaultEntry.fromJson(entry));
                }
            }
        } catch (Exception e) {
            System.err.println("SpeciationVaultStore: could not load " + dataFile + " — " + e.getMessage());
        }
    }

    private void persist() {
        try {
            Files.createDirectories(dataFile.getParent());
            JSONObject root = new JSONObject();
            for (VaultEntry entry : entries.values()) {
                try {
                    root.put(entry.getPlanetName(), entry.toJson());
                } catch (Exception e) {
                    throw new IllegalStateException("Could not serialize " + entry.getPlanetName(), e);
                }
            }
            Files.writeString(dataFile, root.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Could not persist speciation vault", e);
        }
    }

    /** Test helper: wipe in-memory + disk. */
    public synchronized void clearForTests() {
        entries.clear();
        try {
            if (Files.exists(dataFile)) {
                Files.delete(dataFile);
            }
        } catch (IOException ignored) {
            // best-effort
        }
    }
}
