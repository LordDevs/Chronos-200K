import static org.junit.jupiter.api.Assertions.*;

import evolution.EvolutionEngine;
import evolution.SpeciationVaultStore;
import java.nio.file.Path;
import java.util.List;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ObservatoryServiceTest {

    @TempDir
    Path tempDir;

    private ObservatoryService obs;

    @BeforeEach
    void setup() {
        SpeciationVaultStore vault = new SpeciationVaultStore(tempDir.resolve("vault.json"));
        NasaTapClient tap = new NasaTapClient(this::stubPlanet);
        obs = new ObservatoryService(tap, new EvolutionEngine(), vault);
    }

    private JSONObject stubPlanet(String query) {
        try {
            String q = query.toLowerCase();
            if (q.contains("missing") || q.contains("unknown")) {
                return null;
            }
            JSONObject row = new JSONObject();
            if (q.contains("proxima")) {
                row.put("pl_name", "Proxima Centauri b");
                row.put("hostname", "Proxima Centauri");
                row.put("pl_eqt", 234.0);
                row.put("pl_rade", 1.07);
                row.put("pl_bmasse", 1.27);
                row.put("sy_dist", 1.3);
                row.put("pl_orbper", 11.2);
                row.put("discoverymethod", "Radial Velocity");
                row.put("disc_year", 2016);
                return row;
            }
            row.put("pl_name", "Kepler-442 b");
            row.put("hostname", "Kepler-442");
            row.put("pl_eqt", 233.0);
            row.put("pl_rade", 1.34);
            row.put("pl_bmasse", 2.36);
            row.put("pl_orbsmax", 0.409);
            row.put("sy_dist", 370.0);
            row.put("pl_orbper", 112.3);
            row.put("pl_dens", 5.0);
            row.put("pl_insol", 0.7);
            row.put("discoverymethod", "Transit");
            row.put("disc_year", 2015);
            row.put("disc_facility", "Kepler");
            row.put("st_teff", 4402.0);
            row.put("st_spectype", "K");
            return row;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    void testDeepScanIncludesObservatoryFields() {
        String report = obs.deepScan("kepler-442 b");
        assertTrue(report.contains("OBSERVATORY DEEP SCAN"), report);
        assertTrue(report.toLowerCase().contains("kepler-442"), report);
        assertTrue(report.contains("Período orbital") || report.contains("112"), report);
        assertTrue(report.contains("Método") || report.contains("Transit"), report);
        assertTrue(report.contains("g estimado") || report.contains("g"), report);
    }

    @Test
    void testCompareRanksCloserTeq() {
        String report = obs.compare("kepler-442 b|proxima centauri b");
        assertTrue(report.contains("OBSERVATORY COMPARE"), report);
        assertTrue(report.contains("Kepler-442 b"), report);
        assertTrue(report.contains("Proxima Centauri b"), report);
        // Proxima T_eq 234 closer to 255 than Kepler 233? |234-255|=21, |233-255|=22 → Proxima first
        int proxIdx = report.indexOf("Proxima Centauri b");
        int kepIdx = report.indexOf("Kepler-442 b");
        assertTrue(proxIdx >= 0 && kepIdx >= 0);
        assertTrue(proxIdx < kepIdx, "Proxima should rank closer to 255 K: " + report);
        assertTrue(report.contains("★"), report);
    }

    @Test
    void testParseCompareNames() {
        List<String> pipe = ObservatoryService.parseCompareNames("a|b|c");
        assertEquals(3, pipe.size());
        List<String> and = ObservatoryService.parseCompareNames("kepler-442 b AND proxima centauri b");
        assertEquals(2, and.size());
        assertEquals("kepler-442 b", and.get(0));
    }

    @Test
    void testVaultArchiveListShowDelete() {
        String archived = obs.vaultArchive("kepler-442 b");
        assertTrue(archived.contains("SPECIATION VAULT"), archived);
        assertTrue(archived.contains("NASA-anchored"), archived);
        assertTrue(archived.toLowerCase().contains("assum"), archived);

        String list = obs.vaultList();
        assertTrue(list.toLowerCase().contains("kepler-442"), list);

        String show = obs.vaultShow("kepler-442 b");
        assertTrue(show.contains("SHOW"), show);
        assertTrue(show.contains("EVOLUTION FORECAST") || show.contains("Adapta"), show);

        String deleted = obs.vaultDelete("kepler-442 b");
        assertTrue(deleted.toLowerCase().contains("removid") || deleted.toLowerCase().contains("remov"), deleted);
        assertTrue(obs.vaultList().contains("0") || obs.vaultList().toLowerCase().contains("vazio")
                || obs.vaultList().contains("entradas: 0"), obs.vaultList());
    }

    @Test
    void testVaultFailsWithoutMassOrRadius() throws Exception {
        NasaTapClient tap = new NasaTapClient(q -> {
            try {
                JSONObject row = new JSONObject();
                row.put("pl_name", "No Mass Planet");
                row.put("pl_eqt", 250.0);
                row.put("pl_rade", 1.0);
                return row;
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
        ObservatoryService local = new ObservatoryService(
                tap, new EvolutionEngine(), new SpeciationVaultStore(tempDir.resolve("v2.json")));
        String result = local.vaultArchive("no mass");
        assertTrue(result.toLowerCase().contains("falhou") || result.toLowerCase().contains("massa"), result);
    }

    @Test
    void testHandleVaultTokens() {
        assertTrue(obs.handleVault("archive:kepler-442 b").contains("SPECIATION VAULT"));
        assertTrue(obs.handleVault("list").contains("SPECIATION VAULT"));
    }
}
