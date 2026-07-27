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
 * File-backed store for colony terraforming profiles (Learning Mode v2).
 */
public class LearningStore {
    private static final LearningStore DEFAULT = new LearningStore(Path.of("Backend", "data", "colonies.json"));

    private final Path dataFile;
    private final Map<String, ColonyProfile> profiles = new LinkedHashMap<>();

    public LearningStore(Path dataFile) {
        this.dataFile = dataFile;
        loadFromDisk();
    }

    public static LearningStore getInstance() {
        return DEFAULT;
    }

    public synchronized List<ColonyProfile> list() {
        return new ArrayList<>(profiles.values());
    }

    public synchronized Optional<ColonyProfile> get(String name) {
        return Optional.ofNullable(profiles.get(ColonyProfile.normalizeName(name)));
    }

    public synchronized ColonyProfile save(ColonyProfile profile) {
        String key = ColonyProfile.normalizeName(profile.getName());
        if (key.isBlank()) {
            throw new IllegalArgumentException("Colony name required");
        }
        ColonyProfile stored = new ColonyProfile(key, profile.getEnvironment(), profile.getSavedAt());
        profiles.put(key, stored);
        persist();
        return stored;
    }

    public synchronized boolean delete(String name) {
        String key = ColonyProfile.normalizeName(name);
        if (!profiles.containsKey(key)) {
            return false;
        }
        profiles.remove(key);
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
                    profiles.put(key, ColonyProfile.fromJson(entry));
                }
            }
        } catch (Exception e) {
            System.err.println("LearningStore: could not load " + dataFile + " — " + e.getMessage());
        }
    }

    private void persist() {
        try {
            Files.createDirectories(dataFile.getParent());
            JSONObject root = new JSONObject();
            for (ColonyProfile profile : profiles.values()) {
                try {
                    root.put(profile.getName(), profile.toJson());
                } catch (Exception e) {
                    throw new IllegalStateException("Could not serialize " + profile.getName(), e);
                }
            }
            Files.writeString(dataFile, root.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Could not persist colony profiles", e);
        }
    }

    /** Test hook — clears in-memory state without touching default file. */
    synchronized void resetForTests() {
        profiles.clear();
    }
}
