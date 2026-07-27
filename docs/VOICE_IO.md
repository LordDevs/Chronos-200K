# Voice I/O (Web Speech API)

Phase 3 adds **browser-only** voice — no backend changes. Speech is converted to text and sent to the same `POST /api` endpoint.

## UI

| Control | Action |
|---------|--------|
| **MIC** | Toggle speech recognition (pt-PT) |
| **TTS** | Toggle CHRONOS voice output for bot replies |

While listening, the HUD shows a helmet visor pulse (`body.helmet-listening`).

## Files

- `Frontend/voice.js` — `createVoiceChannel()` wrapper
- `Frontend/script.js` — wires mic → `sendToChronos()` → optional TTS

## Browser support

- **Recommended:** Chrome, Edge (Chromium)
- **Requires:** HTTPS or `localhost`, microphone permission
- Firefox has limited `SpeechRecognition` support

## Example voice commands

These map to existing AIML patterns (spoken in Portuguese or English):

- "Analisar planeta oceano" → `ANALYZE OCEAN PLANET`
- "Ativar kit Astartes" → `ACTIVATE ASTARTES KIT`
- "Status da nave" → `SHIP STATUS`
- "Check oxygen levels" → `OXYGEN`

## Architecture

```text
Microphone → SpeechRecognition → text → POST /api → AIML + Java
                                              ↓
                                    HTML reply → strip tags → speechSynthesis
```

Future: push-to-talk hold, English `en-US` locale toggle, wake word.
