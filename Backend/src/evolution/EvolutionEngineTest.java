package evolution;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class EvolutionEngineTest {

    @Test
    void testHighGravityTraits() {
        EvolutionEngine engine = new EvolutionEngine();
        String report = engine.simulateFromRaw("2g:80:1000");
        assertNotNull(report);
        assertTrue(report.toLowerCase().contains("óssea")
                        || report.toLowerCase().contains("ossea")
                        || report.toLowerCase().contains("densidade"),
                report);
        assertTrue(report.contains("80") || report.toLowerCase().contains("água")
                        || report.toLowerCase().contains("aqua"),
                report);
    }

    @Test
    void testParseProfile() {
        EnvironmentProfile p = EnvironmentProfile.parse("gravity 1.2 water 40 generations 500");
        assertEquals(1.2, p.getGravityG(), 0.001);
        assertEquals(40.0, p.getWaterPercent(), 0.001);
        assertEquals(500, p.getGenerations());
    }
}
