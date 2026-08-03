# Chronos-200K Project Board Setup

Estrutura do Project Board para o Chronos-200K.

## Colunas do Board

### 1. Core Setup
Configuração base: deps, Tomcat/Program AB, estrutura do repo, metadata — **concluído** (ver [`docs/PHASE1.md`](../docs/PHASE1.md)).

> Nota: paths on-disk permanecem `Backend/` / `Frontend/` (compatibilidade Java/`run.bat`). Lowercase fica para um refactor dedicado.

### 2. In Development
Persona AIML Chronos, ExoplanetService (NASA TAP), EvolutionEngine, frontend HUD.

### 3. Testing
Unit tests (MessageHandler, ExoplanetService, TraitCalculator), validação de routing AIML → Java.

### 4. Deployment
Demo local, docs (`ARCHITECTURE`, `NASA_API`, `EVOLUTION_RULES`), card no portfolio.

## Como usar

- Criar issues e adicionar ao board
- Mover entre colunas conforme o progresso
- Labels sugeridas: `aiml`, `nasa`, `evolution`, `frontend`, `docs`, `bug`

Fluxo: Core Setup → In Development → Testing → Deployment
