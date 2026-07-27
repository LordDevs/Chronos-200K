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
- **WebSocket** `/chat` remains available; the HUD uses HTTP POST for simplicity.

Voice I/O (Web Speech API) is deferred to a later phase; the same `/chat/api` contract applies.
