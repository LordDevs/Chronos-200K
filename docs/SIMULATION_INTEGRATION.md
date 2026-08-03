# Simulation integration guide

Chronos routes AIML templates to Java services via structured tokens.

## Flow

```text
User utterance
    → Program AB (chronos/*.aiml) matches pattern
    → Template emits token (e.g. simulate:ocean)
    → MessageHandler.tryRoute()
    → CommandRouter → BiomeSimulationService | ApexProtocolService | SpeciationService
    → HTML log returned to HUD
```

## AIML files

| File | Role |
|------|------|
| `chronos_persona.aiml` | Identity, Commander Michel, LordDevs, protocols |
| `sci_fi_simulation.aiml` | Jungle/ocean/Apex Protocol/speciation triggers |
| `commands.aiml` | NASA exoplanet + EVOLVE routing |
| `observatory_cmds.aiml` | Deep Scan / Compare / Speciation Vault |
| `astronaut_cmds.aiml` | Ship telemetry |
| `learning_mode.aiml` | Colony Archive (Learning Mode v2) |
| `voice_cmds.aiml` | PT/EN spoken aliases |

## Routing tokens

| Token | Example | Java handler |
|-------|---------|--------------|
| `simulate:<biome>[:g]` | `simulate:jungle:1.5` | `BiomeSimulationService` |
| `apex:<g>[:atmos]` | `apex:2.5:CO2-rich` | `ApexProtocolService` |
| `speciate:<years>[:biome]` | `speciate:200000:ocean` | `SpeciationService` |
| `exoplanet:<name>` | `exoplanet:kepler-442 b` | `ExoplanetService` |
| `evolve:<tokens>` | `evolve:2g:80:1000` | `EvolutionEngine` |
| `learn:<action>[:payload]` | `learn:save:mars …` | `LearningModeService` |

## Physics catalog (hardcoded)

| Body | Gravity |
|------|---------|
| Mars | 0.38g |
| Earth | 1.0g |
| Jungle world | 1.2g |
| Ocean world | 1.1g |
| Super-Earth | 2.5g |

Defined in `Backend/src/simulation/PhysicsCatalog.java`.

## Sample commands

```
ANALYZE JUNGLE PLANET
ANALYZE OCEAN PLANET
ACTIVATE APEX PROTOCOL
PREDICT EVOLUTION 200K ON OCEAN PLANET
ANALYZE MARS
WELCOME COMMANDER MICHEL
```

## Adding a new simulation

1. Add AIML `<pattern>` + `<template>token:args</template>` in `sci_fi_simulation.aiml`.
2. Extend `CommandRouter.route()` with new prefix.
3. Implement service under `Backend/src/simulation/`.
4. Recompile: `run.bat` or `run.sh`.
