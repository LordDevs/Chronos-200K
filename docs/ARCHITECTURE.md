# Chronos-200K architecture

```text
Frontend (HUD)  --POST /api-->  ChatAPI  -->  MessageHandler
                                                      |        |
                                                   AIML     Command router
                                                 (chronos)   |         |
                        simulate:/apex:/speciate:      exoplanet:   evolve:   learn:
                             |         |         |               |         |        |
                    BiomeSimulation  ApexProt  Speciation  Exoplanet  Evolution  LearningStore
                         Service      Service    Service     Service    Engine
```

- **AIML** (`Backend/ab/bots/chronos/aiml/`): persona, command phrasing, ship flavor text.
- **Simulation** (`Backend/src/simulation/`): jungle/ocean logistics, Chronos Apex Protocol, 200k speciation.
- **Java services**: dynamic science (NASA lookup, evolution math).
- **Learning Mode v2** (`evolution/LearningStore`): persist colony terraforming profiles; `GET /api/colonies`.
- **Voice I/O** (`Frontend/voice.js`): Web Speech API — mic → text → `/api`; TTS for bot replies (client-only).
- **WebSocket** `/ws` remains available; the HUD uses HTTP `POST /api`.

Lore / naming: [`docs/CHRONOS_LORE.md`](CHRONOS_LORE.md).
