package evolution;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class LearningStoreTest {

    @TempDir
    Path tempDir;

    @Test
    void testSaveLoadListDelete() throws Exception {
        Path file = tempDir.resolve("colonies.json");
        LearningStore store = new LearningStore(file);

        EnvironmentProfile env = new EnvironmentProfile(0.38, 10, -60, "CO2-rich", 2000);
        store.save(new ColonyProfile("mars-colony", env));

        assertEquals(1, store.list().size());
        assertTrue(store.get("mars-colony").isPresent());
        assertEquals(0.38, store.get("mars-colony").get().getEnvironment().getGravityG(), 0.001);

        assertTrue(store.delete("mars-colony"));
        assertTrue(store.list().isEmpty());
        assertTrue(Files.exists(file));
    }

    @Test
    void testNormalizeNameOnSave() {
        LearningStore store = new LearningStore(tempDir.resolve("c.json"));
        store.save(new ColonyProfile("Mars Colony", new EnvironmentProfile(1, 50, 15, "N2-O2", 1000)));
        assertTrue(store.get("mars-colony").isPresent());
    }
}
