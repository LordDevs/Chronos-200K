import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ExoplanetServiceTest {

    @Test
    void testEstimateGravity() {
        Double g = ExoplanetService.estimateGravity(2.36, 1.34);
        assertNotNull(g);
        assertTrue(g > 1.0 && g < 2.0, "Kepler-442 b proxy gravity should be ~1.3g, got " + g);
    }

    @Test
    void testHabitabilityBands() {
        assertEquals("candidato à zona habitável", ExoplanetService.classifyHabitability(241.0));
        assertTrue(ExoplanetService.classifyHabitability(100.0).contains("frio"));
        assertTrue(ExoplanetService.classifyHabitability(400.0).contains("quente"));
    }

    @Test
    void testNasaLookupKepler442() {
        ExoplanetService svc = new ExoplanetService("kepler-442 b");
        String report = svc.formatReport();
        assertNotNull(report);
        assertTrue(report.toLowerCase().contains("kepler-442"), report);
    }
}
