package evolution;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.util.List;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class SpeciationVaultStoreTest {

    @TempDir
    Path tempDir;

    @Test
    void testSaveLoadListDelete() throws Exception {
        Path file = tempDir.resolve("speciation-vault.json");
        SpeciationVaultStore store = new SpeciationVaultStore(file);

        JSONObject snap = new JSONObject();
        snap.put("pl_name", "Kepler-442 b");
        snap.put("pl_eqt", 233);
        VaultEntry entry = new VaultEntry(
                "Kepler-442 b",
                snap,
                1.31,
                -40.15,
                false,
                50,
                "N2-O2",
                1000,
                List.of("denser bone matrix"),
                0.72,
                "EVOLUTION FORECAST // test",
                System.currentTimeMillis());

        store.save(entry);
        assertEquals(1, store.list().size());
        assertTrue(store.get("kepler-442 b").isPresent());
        assertEquals(1.31, store.get("kepler-442 b").get().getGravityG(), 0.01);

        SpeciationVaultStore reloaded = new SpeciationVaultStore(file);
        assertTrue(reloaded.get("Kepler-442 b").isPresent());
        assertTrue(reloaded.delete("kepler-442 b"));
        assertTrue(reloaded.list().isEmpty());
    }
}
