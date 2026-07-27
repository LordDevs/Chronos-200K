package simulation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class SimulationServicesTest {

  @Test
  void jungleSimulationContainsGravityAndImplants() {
    BiomeSimulationService svc = new BiomeSimulationService();
    String report = svc.analyzeBiome("jungle", "");
    assertTrue(report.contains("LOGISTICS"));
    assertTrue(report.toLowerCase().contains("jungla") || report.contains("Jungle"));
    assertTrue(report.contains("1.20g") || report.contains("1.2g"));
  }

  @Test
  void oceanSimulationMentionsApexOrGills() {
    BiomeSimulationService svc = new BiomeSimulationService();
    String report = svc.analyzeBiome("ocean", "");
    assertTrue(report.toLowerCase().contains("ocean") || report.contains("oceân"));
    assertTrue(report.toLowerCase().contains("apex") || report.contains("brânquias"));
  }

  @Test
  void apexProtocolForSuperEarth() {
    ApexProtocolService svc = new ApexProtocolService();
    String blueprint = svc.generateBlueprint("2.5", "CO2-rich");
    assertTrue(blueprint.contains("APEX PROTOCOL"));
    assertTrue(blueprint.toLowerCase().contains("carbono") || blueprint.contains("carbon"));
    assertTrue(blueprint.contains("%"));
  }

  @Test
  void speciation200kUsesGenerations() {
    SpeciationService svc = new SpeciationService();
    assertEquals(8000, SpeciationService.yearsToGenerations(200_000));
    String forecast = svc.predictSpeciation(200_000, "ocean");
    assertTrue(forecast.contains("SPECIATION"));
    assertTrue(forecast.contains("200,000") || forecast.contains("200000"));
  }

  @Test
  void commandRouterSimulateOcean() {
    CommandRouter router = new CommandRouter();
    String out = router.route("simulate:ocean");
    assertNotNull(out);
    assertTrue(out.contains("LOGISTICS"));
  }

  @Test
  void commandRouterApex() {
    CommandRouter router = new CommandRouter();
    String out = router.route("apex:2.5:CO2-rich");
    assertNotNull(out);
    assertTrue(out.contains("APEX PROTOCOL"));
  }
}
