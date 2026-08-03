# Chronos-200K architecture

```text
Frontend (HUD)  --POST /api-->  ChatAPI  -->  MessageHandler
                                                      |        |
                                                   AIML     Command router
                                                 (chronos)   |         |
                        simulate:/apex:/speciate:      exoplanet:/deepscan:/compare:/vault:
                             |         |         |               |                    |
                    BiomeSimulation  ApexProt  Speciation  Exoplanet / Observatory  evolve:/learn:
                         Service      Service    Service     + NasaTapClient         Evolution/Learning
```

- **AIML** (`Backend/ab/bots/chronos/aiml/`): persona, command phrasing, ship flavor text (`observatory_cmds.aiml`).
- **Simulation** (`Backend/src/simulation/`): jungle/ocean logistics, Chronos Apex Protocol, 200k speciation.
- **Java services**: dynamic science (NASA TAP, evolution math, Observatory Channel).
- **Observatory Channel**: Deep Scan / Compare / Speciation Vault / Mission Deploy — see [`OBSERVATORY_CHANNEL.md`](OBSERVATORY_CHANNEL.md). HUD panel + `GET /api/vault` + `GET /api/observatory`.
- **Learning Mode v2** (`evolution/LearningStore`): persist colony terraforming profiles; `GET /api/colonies`.
- **Voice I/O** (`Frontend/voice.js`): Web Speech API — mic → text → `/api`; TTS for bot replies (client-only).
- **WebSocket** `/ws` remains available; the HUD uses HTTP `POST /api`.

Lore / naming: [`docs/CHRONOS_LORE.md`](CHRONOS_LORE.md).
