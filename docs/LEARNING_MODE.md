# Learning Mode v2 — Colony Archive

Learning Mode v1 runs one-off evolution forecasts from inline parameters (`EVOLVE gravity 2g water 80 generations 1000`).

**v2** adds named **terraforming profiles** persisted to disk so commanders can save, reload, and re-run deep-time simulations.

## Commands

| Command | Action |
|---------|--------|
| `LEARNING MODE SAVE <name> gravity 2g water 80 temp 15 co2 generations 1000` | Save profile |
| `LEARNING MODE LIST` | List all saved colonies |
| `LEARNING MODE LOAD <name>` | Show saved parameters |
| `LEARNING MODE EVOLVE <name>` | Run EvolutionEngine on saved profile |
| `LEARNING MODE DELETE <name>` | Remove profile |
| `LEARNING MODE HELP` | Show command help |

Portuguese AIML aliases: `MODO APRENDIZAGEM GUARDAR/LISTAR/EVOLUIR/APAGAR`.

Voice aliases: `GUARDAR COLONIA`, `LISTAR COLONIAS`, `EVOLUIR COLONIA`.

## Storage

- File: `Backend/data/colonies.json` (gitignored)
- JSON API: `GET /api/colonies` — used by the HUD colony panel

## HUD

The **COLONY ARCHIVE** panel lets you:

1. Fill gravity / water / temp / atmosphere / generations
2. **Guardar perfil** → `LEARNING MODE SAVE …`
3. **Evoluir / Carregar / Apagar** on each saved card

## Architecture

```text
LEARNING MODE SAVE *  →  learn:save:*  →  LearningModeService  →  LearningStore (JSON)
LEARNING MODE EVOLVE * →  learn:evolve:* →  LearningModeService  →  EvolutionEngine
GET /api/colonies      →  ColoniesAPI    →  LearningStore.list()
```

Code: `Backend/src/evolution/ColonyProfile.java`, `LearningStore.java`, `LearningModeService.java`.

See also: [`EVOLUTION_RULES.md`](EVOLUTION_RULES.md).
