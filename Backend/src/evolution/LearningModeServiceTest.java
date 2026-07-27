package evolution;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class LearningModeServiceTest {

    @TempDir
    Path tempDir;

    private LearningStore store;
    private LearningModeService service;

    @BeforeEach
    void setup() {
        store = new LearningStore(tempDir.resolve("colonies.json"));
        service = new LearningModeService(store, new EvolutionEngine());
    }

    @Test
    void testSaveAndEvolve() {
        String saved = service.saveProfile("test-colony gravity 2g water 80 temp 15 generations 1000");
        assertTrue(saved.contains("COLONY ARCHIVE"), saved);
        assertTrue(saved.contains("test-colony"), saved);

        String evolved = service.evolveProfile("test-colony");
        assertTrue(evolved.contains("LEARNING MODE v2"), evolved);
        assertTrue(evolved.contains("EVOLUTION FORECAST"), evolved);
    }

    @Test
    void testListEmpty() {
        String list = service.listProfiles();
        assertTrue(list.contains("vazio"), list);
    }

    @Test
    void testDeleteMissing() {
        assertTrue(service.deleteProfile("ghost").contains("não encontrado"));
    }
}
