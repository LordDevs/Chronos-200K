# Voice I/O (Web Speech API)

Phase 3 adds **browser-only** voice — no backend changes. Speech is converted to text and sent to the same `POST /api` endpoint.

## UI

| Control | Action |
|---------|--------|
| **MIC** | Toggle speech recognition (pt-PT) |
| **TTS** | Toggle CHRONOS voice output for bot replies |

While listening, the HUD shows a helmet visor pulse (`body.helmet-listening`).

## Files

- `Frontend/voice.js` — `normalizeVoiceTranscript()` + `createVoiceChannel()`
- `Frontend/script.js` — wires mic → `sendToChronos()` → optional TTS
- `Backend/ab/bots/chronos/aiml/voice_cmds.aiml` — PT/EN phrase aliases

## Browser support

- **Recommended:** Chrome, Edge (Chromium)
- **Requires:** HTTPS or `localhost`, microphone permission
- Firefox has limited `SpeechRecognition` support

## Voice command phrases

### Telemetria

| Fala (PT / EN) | Comando interno |
|----------------|-----------------|
| "Status da nave" / "Estado da nave" / "Telemetria" | `SHIP STATUS` |
| "Verificar oxigénio" / "Níveis de oxigénio" / "Check oxygen levels" | `OXYGEN` |

### Simulação sci-fi

| Fala | Comando interno |
|------|-----------------|
| "Analisar planeta oceano" | `ANALYZE OCEAN PLANET` |
| "Analisar planeta selva" | `ANALYZE JUNGLE PLANET` |
| "Analisar Marte" | `ANALYZE MARS` |
| "Analisar super terra" | `ANALYZE SUPER EARTH` |
| "Ativar protocolo Apex" / "Ativar CAP" | `ACTIVATE APEX PROTOCOL` |
| "Prever evolução 200 mil" / "200K" | `PREDICT EVOLUTION 200K` |
| "Evolução 200 mil anos no planeta oceano" | `PREDICT EVOLUTION 200K ON OCEAN PLANET` |

### Exoplanetas NASA

| Fala | Comando interno |
|------|-----------------|
| "Analisar planeta kepler 442 b" | `exoplanet:kepler-442 b` |
| "Quais as condições do proxima centauri b" | `exoplanet:proxima centauri b` |

STT often drops hyphens — `normalizeVoiceTranscript()` fixes `kepler 442 b` → `kepler-442 b`.

### Persona

| Fala | Comando interno |
|------|-----------------|
| "Olá" / "Bom dia" | greeting |
| "Ajuda" / "Protocolos" | `SYSTEM PROTOCOLS` |
| "Quem és tu" | `WHO ARE YOU` |

## Architecture

```text
Microphone → SpeechRecognition → normalizeVoiceTranscript()
                                              ↓
                                    POST /api → voice_cmds.aiml → AIML + Java
                                              ↓
                                    HTML reply → strip tags → speechSynthesis
```

Future: push-to-talk hold, English `en-US` locale toggle, wake word.
