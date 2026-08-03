import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

public class NasaTapClientTest {

    @Test
    void testEstimateGravity() {
        Double g = NasaTapClient.estimateGravity(2.36, 1.34);
        assertNotNull(g);
        assertTrue(g > 1.0 && g < 2.0);
    }

    @Test
    void testHabitabilityBands() {
        assertEquals("candidato à zona habitável", NasaTapClient.classifyHabitability(241.0));
        assertTrue(NasaTapClient.classifyHabitability(100.0).contains("frio"));
        assertTrue(NasaTapClient.classifyHabitability(400.0).contains("quente"));
    }

    @Test
    void testOptHelpers() throws Exception {
        JSONObject row = new JSONObject();
        row.put("pl_name", "Kepler-442 b");
        row.put("pl_eqt", 233.0);
        assertEquals("Kepler-442 b", NasaTapClient.optString(row, "pl_name"));
        assertEquals(233.0, NasaTapClient.optDouble(row, "pl_eqt"), 0.01);
        assertNull(NasaTapClient.optDouble(row, "missing"));
        assertEquals("n/d", NasaTapClient.fmt(null, " K"));
    }

    @Test
    void testInjectableFetcher() throws Exception {
        NasaTapClient client = new NasaTapClient(q -> {
            try {
                JSONObject row = new JSONObject();
                row.put("pl_name", "Stub Planet");
                row.put("pl_eqt", 255.0);
                return row;
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
        JSONObject row = client.fetchPlanet("anything");
        assertEquals("Stub Planet", NasaTapClient.optString(row, "pl_name"));
    }
}
