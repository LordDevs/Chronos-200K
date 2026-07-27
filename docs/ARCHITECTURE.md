# Chronos-200K architecture

```text
Frontend (HUD)  --POST /api-->  ChatAPI  -->  MessageHandler
                                                      |        |
                                                   AIML     Command router
                                                 (chronos)   |         |
                        simulate:/astartes:/speciate:      exoplanet:   evolve:
                             |         |         |               |         |
                    BiomeSimulation  Astartes  Speciation  Exoplanet  Evolution
                         Service      KitSvc     Service     Service    Engine
```

- **AIML** (`Backend/ab/bots/chronos/aiml/`): persona, command phrasing, ship flavor text.
- **Simulation** (`Backend/src/simulation/`): jungle/ocean logistics, Astartes Kit, 200k speciation.
- **Java services**: dynamic science (NASA lookup, evolution math).
- **Voice I/O** (`Frontend/voice.js`): Web Speech API — mic → text → `/api`; TTS for bot replies (client-only).
- **WebSocket** `/ws` remains available; the HUD uses HTTP `POST /api`.
