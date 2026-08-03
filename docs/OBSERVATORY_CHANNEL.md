# Observatory Channel

NASA-anchored observatory layer for Chronos-200K. **Source of truth:** Exoplanet Archive TAP `pscomppars` only.

**Out of scope:** Chronos Drive, Nano-Forge (as a separate system), FTL, invented physics constants beyond existing `g ≈ M/R²` and T_eq habitability proxy.

## Commands

| Command | Token | Behavior |
|---------|-------|----------|
| `DEEP SCAN PLANET <name>` | `deepscan:<name>` | Extended TAP field report |
| `COMPARE PLANETS <a> AND <b> [AND <c>]` | `compare:a\|b` or `a\|b\|c` | Side-by-side HTML (max 3) |
| `VAULT ARCHIVE <planet>` | `vault:archive:<planet>` | TAP → EvolutionEngine → persist entry |
| `VAULT LIST` | `vault:list` | List archived keys |
| `VAULT SHOW <planet>` | `vault:show:<planet>` | Last forecast + TAP snapshot |
| `VAULT DELETE <planet>` | `vault:delete:<planet>` | Remove entry |
| `DEPLOY COLONY <planet>` | `deploy:<planet>` | Mission Brief from TAP → suggested colony profile |
| `DEPLOY COLONY <planet> SAVE` | `deploy:<planet>:save` | Brief + save profile to Colony Archive |

Short scan remains `ANALYZE PLANET` → `exoplanet:` ([`ExoplanetService`](../Backend/src/ExoplanetService.java)).

Implementation: [`ObservatoryService`](../Backend/src/ObservatoryService.java), [`MissionDeployService`](../Backend/src/MissionDeployService.java), AIML [`observatory_cmds.aiml`](../Backend/ab/bots/chronos/aiml/observatory_cmds.aiml).

## A — Deep Scan

Adds observatory block: orbital period, density, insolation, discovery metadata, host Teff / spectral type. Missing → `n/d`.

## C — Compare

Fetches each planet via shared TAP client. Table: name, g (est.), T_eq, radius, mass, distance, HZ proxy. Sorted by `|T_eq − 255 K|` (Earth proxy ranking aid).

## B — Speciation Vault

Storage: `Backend/data/speciation-vault.json` (gitignored). Store: [`SpeciationVaultStore`](../Backend/src/evolution/SpeciationVaultStore.java).

### Profile mapping

| EnvironmentProfile | Source |
|--------------------|--------|
| `gravityG` | `estimateGravity(pl_bmasse, pl_rade)` — **fail if M or R missing** |
| `temperatureC` | `pl_eqt − 273.15` when present; else **15** with note “T_eq n/d — default used” |
| `waterPercent` | **50** assumed (not in TAP) |
| `atmosphere` | **N2-O2** assumed (not in TAP) |
| `generations` | **1000** (Learning Mode default) |

Every vault report starts with `SPECIATION VAULT // NASA-anchored` and states TAP vs assumed inputs. Forecast uses existing deterministic [`EvolutionEngine`](../Backend/src/evolution/EvolutionEngine.java) — no new physics constants.

## Mission Brief / Colony Deploy

Same TAP → profile mapping as Vault. Emits `MISSION BRIEF // NASA-anchored deploy` with suggested colony name (`ColonyProfile.normalizeName`), CAP hint, and links to Vault / Learning Mode. `SAVE` writes to [`LearningStore`](../Backend/src/evolution/LearningStore.java).

## HUD / HTTP

| Endpoint | Payload |
|----------|---------|
| `GET /api/vault` | JSON array of vault entries |
| `GET /api/observatory` | `{ "recent": [ { type, title, summary, at } ] }` in-memory recent ops |

HUD panel **OBSERVATORY // Channel** shows recent ops + vault cards (Show / Deploy / Apagar).
