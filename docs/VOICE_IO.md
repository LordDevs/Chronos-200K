# Voice I/O (Web Speech API)

Phase 3 adds **browser-only** voice — no backend changes. Speech is converted to text and sent to the same `POST /api` endpoint.

## UI

| Control | Action |
|---------|--------|
| **MIC** | Toggle speech recognition |
| **TTS** | Toggle CHRONOS voice output for bot replies |
| **PT / EN** | Toggle recognition + TTS locale (`pt-PT` ↔ `en-US`); preference saved in `localStorage` |

While listening, the HUD shows a helmet visor pulse (`body.helmet-listening`).

## Files

- `Frontend/voice.js` — `normalizeVoiceTranscript()` + `createVoiceChannel()` with `setLang` / `toggleLang`
- TTS runs `sanitizeForSpeech()` first (strips HTML, `//`, bullets `•·`, `*_\`#`, brackets) so the engine does not read markup aloud
- `Frontend/script.js` — wires mic / TTS / lang → `sendToChronos()` → optional TTS
- `Backend/ab/bots/chronos/aiml/voice_cmds.aiml` — PT/EN phrase aliases

## Browser support

- **Recommended:** Chrome, Edge (Chromium)
- **Requires:** HTTPS or `localhost`, microphone permission
- Firefox has limited `SpeechRecognition` support

## Locales

| Locale | STT / TTS | Label |
|--------|-----------|-------|
| `pt-PT` (default) | Portuguese (Portugal) | **PT** |
| `en-US` | English (US) | **EN** |

Switching language stops the current mic session and rebuilds `SpeechRecognition` with the new `lang`. TTS uses a matching browser voice when available.

## Voice command phrases

### Telemetria

| Fala (PT / EN) | Comando interno |
|----------------|-----------------|
| "Status da nave" / "Ship status" | `SHIP STATUS` |
| "Verificar oxigénio" / "Check oxygen levels" | `OXYGEN` |

### Simulação sci-fi

| Fala | Comando interno |
|------|-----------------|
| "Analisar planeta oceano" / "Analyze ocean planet" | `ANALYZE OCEAN PLANET` |
| "Analisar planeta selva" / "Analyze jungle planet" | `ANALYZE JUNGLE PLANET` |
| "Ativar protocolo Apex" / "Activate Apex Protocol" | `ACTIVATE APEX PROTOCOL` |
| "Prever evolução 200 mil" / "Predict evolution 200K" | `PREDICT EVOLUTION 200K` |

### Exoplanetas NASA

| Fala | Comando interno |
|------|-----------------|
| "Analisar planeta kepler 442 b" / "Analyze planet kepler 442 b" | `exoplanet:kepler-442 b` |

STT often drops hyphens — `normalizeVoiceTranscript()` fixes `kepler 442 b` → `kepler-442 b`.

### Persona

| Fala | Comando interno |
|------|-----------------|
| "Olá" / "Hello" | greeting |
| "Ajuda" / "Help" | `SYSTEM PROTOCOLS` |
| "Quem és tu" / "Who are you" | `WHO ARE YOU` |

## Architecture

```text
Microphone → SpeechRecognition (pt-PT|en-US) → normalizeVoiceTranscript()
                                              ↓
                                    POST /api → voice_cmds.aiml → AIML + Java
                                              ↓
                                    HTML reply → sanitizeForSpeech() → speechSynthesis (same locale)
```

Future: push-to-talk hold, wake word.
