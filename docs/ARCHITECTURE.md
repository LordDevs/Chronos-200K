# Chronos-200K architecture

```text
Frontend (HUD)  --POST /chat/api-->  ChatAPI  -->  MessageHandler
                                                      |        |
                                                   AIML     Command router
                                                 (chronos)   |         |
                                                        exoplanet:   evolve:
                                                             |         |
                                                   ExoplanetService  EvolutionEngine
                                                             |
                                                      NASA TAP (pscomppars)
```

- **AIML** (`Backend/ab/bots/chronos/aiml/`): persona, command phrasing, ship flavor text.
- **Java services**: dynamic science (NASA lookup, evolution math).
- **WebSocket** `/chat` remains available; the HUD uses HTTP POST for simplicity.

Voice I/O (Web Speech API) is deferred to a later phase; the same `/chat/api` contract applies.
