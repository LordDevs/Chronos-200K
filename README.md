# Chronos-200K ⏳🚀
### Deep-Time AI Simulator for Human Evolution and Exoplanetary Adaptation

Chronos-200K is an advanced, AI-driven core system designed to simulate, analyze, and predict human biological evolution and biomechanical adaptation across extreme exoplanetary environments over deep-time scales (up to 200,000 years).

Acting as a deep-space colony central computer, the system calculates astrophysics data and cross-references it with generative medicine and evolutionary biology to determine how the human species must adapt to survive.

---

## Key Scientific Pillars

*   **Astrobiology & Exoplanet Logistics:** Simulates human deployment on extreme celestial bodies (e.g., high-gravity super-Earths, ocean worlds, hyper-dense jungle planets).
*   **Biomechanical Enhancements ("Chronos Apex Protocol" / CAP):** Generates architectural blueprints for synthetic organs, bio-engineered skeletal matrices, and nanorobotic neural networks required for environmental survival.
*   **Deep-Time Evolutionary Forecasting:** Predicts morphological and genetic changes (speciation) of isolated human populations over thousands of generations.

---

## Features & Development Checklist

*   [x] **Phase 1 Core Setup:** Migrated from LEARN; public Chronos-200K repo; LEARN archived with pointer stub ([`docs/PHASE1.md`](docs/PHASE1.md)).
*   [x] **Core Rule-Based Analytics Engine:** Processes complex environmental variables and outputs survival probability logs.
*   [x] **AIML Integration & Predefined Knowledge Bases:** Hybrid architecture combining rule-based astronomical parameters with generative AI flexibility.
*   [x] **Sci-Fi Tactical UI Prototype:** Dark-mode, terminal-inspired interface reflecting a starship command deck or bio-informatics lab.
*   [x] **ExoplanetService (NASA TAP):** Live lookup of mass, radius, equilibrium temperature, estimated gravity, habitability proxy.
*   [x] **Sci-Fi Simulation + Chronos Apex Protocol (CAP):** Ocean/jungle logistics, biomechanical blueprints, 200K speciation forecasts.
*   [x] **Adaptive Learning Mode v1 (EvolutionEngine):** Deterministic deep-time trait forecasting from gravity / water / atmosphere / generations.
*   [x] **Adaptive Learning Mode v2 (Colony Archive):** Save / load terraforming profiles; evolve from named colonies; HUD panel + `GET /api/colonies`.
*   [x] **Sub-vocal Speech Command Integration:** Web Speech API (MIC + TTS) on the tactical HUD; PT/EN locale toggle; same `/api` contract.
*   [x] **Observatory Channel (NASA-anchored):** Deep Scan, Compare Planets, Speciation Vault — TAP-only fields + disclosed assumed vault inputs ([`docs/OBSERVATORY_CHANNEL.md`](docs/OBSERVATORY_CHANNEL.md)).

---

## Quick start

```bash
# JDK 17+
bash run.sh          # or run.bat on Windows
# open http://localhost:8080/
```

Try in the HUD: `ANALYZE PLANET kepler-442 b` · `DEEP SCAN PLANET kepler-442 b` · `COMPARE PLANETS kepler-442 b AND proxima centauri b` · `VAULT ARCHIVE kepler-442 b` · `SHIP STATUS`.

Docs: [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) · [`docs/PHASE1.md`](docs/PHASE1.md) · [`docs/CHRONOS_LORE.md`](docs/CHRONOS_LORE.md) · [`docs/NASA_API.md`](docs/NASA_API.md) · [`docs/OBSERVATORY_CHANNEL.md`](docs/OBSERVATORY_CHANNEL.md) · [`docs/EVOLUTION_RULES.md`](docs/EVOLUTION_RULES.md) · [`docs/LEARNING_MODE.md`](docs/LEARNING_MODE.md) · [`docs/VOICE_IO.md`](docs/VOICE_IO.md)

---

## Project Architecture

```text
├── Backend/
│   ├── src/                 # ChatAPI, MessageHandler, ExoplanetService
│   ├── src/evolution/       # EvolutionEngine + trait rules
│   ├── ab/bots/chronos/     # AIML persona & command routing
│   └── lib/                 # Program AB, Tomcat, Jakarta
├── Frontend/                # Tactical HUD UI
├── docs/
├── run.bat
└── run.sh
```

---

## Core Architecture Team

Developed with passion and imagination by **LordDevs**:
*   **Michel (LordDevs):** AI Solution Architect & Prompt Engineer
*   **Emmanuel:** Core System Developer
*   **Celso:** Frontend & Integration Engineer

*Driven by the ultimate goal of ensuring humanity's survival across the stars.*
