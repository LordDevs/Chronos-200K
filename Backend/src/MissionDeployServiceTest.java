import static org.junit.jupiter.api.Assertions.*;

import evolution.LearningStore;
import java.nio.file.Path;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class MissionDeployServiceTest {

    @TempDir
    Path tempDir;

    private MissionDeployService deploy;
    private LearningStore store;
    private ObservatoryActivityLog activity;

    @BeforeEach
    void setup() {
        store = new LearningStore(tempDir.resolve("colonies.json"));
        activity = new ObservatoryActivityLog(10);
        NasaTapClient tap = new NasaTapClient(q -> {
            try {
                JSONObject row = new JSONObject();
                row.put("pl_name", "Kepler-442 b");
                row.put("hostname", "Kepler-442");
                row.put("pl_eqt", 233.0);
                row.put("pl_rade", 1.34);
                row.put("pl_bmasse", 2.36);
                row.put("sy_dist", 370.0);
                return row;
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
        deploy = new MissionDeployService(tap, store, activity);
    }

    @Test
    void testMissionBriefWithoutSave() {
        String report = deploy.deploy("kepler-442 b", false);
        assertTrue(report.contains("MISSION BRIEF"), report);
        assertTrue(report.contains("NASA-anchored"), report);
        assertTrue(report.toLowerCase().contains("kepler"), report);
        assertTrue(report.contains("Perfil colonial sugerido") || report.contains("kepler-442-b"), report);
        assertTrue(store.list().isEmpty());
        assertEquals(1, activity.list().size());
    }

    @Test
    void testDeploySaveCreatesColony() {
        String report = deploy.handle("kepler-442 b:save");
        assertTrue(report.contains("COLONY ARCHIVE"), report);
        assertTrue(store.get("kepler-442-b").isPresent());
        assertEquals(2.36 / (1.34 * 1.34), store.get("kepler-442-b").get().getEnvironment().getGravityG(), 0.01);
    }

    @Test
    void testFailsWithoutMass() {
        NasaTapClient tap = new NasaTapClient(q -> {
            try {
                JSONObject row = new JSONObject();
                row.put("pl_name", "Thin Data");
                row.put("pl_rade", 1.0);
                return row;
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
        MissionDeployService local = new MissionDeployService(tap, store, activity);
        String result = local.deploy("thin", false);
        assertTrue(result.toLowerCase().contains("falhou") || result.toLowerCase().contains("massa"), result);
    }
}
