# Evolution rules (v1 — deterministic)

Learning Mode uses pure Java rules (no LLM). Input is parsed by `EnvironmentProfile` and scored by `TraitCalculator`.

## Input

| Variable | Tokens | Default |
|----------|--------|---------|
| Gravity | `2g`, `gravity 1.5` | 1.0 g |
| Surface water | `water 80` | 50% |
| Temperature | `temp -60` | 15°C |
| Atmosphere | `co2`, `thin`, `ch4` | N2-O2 |
| Generations | `generations 1000` | 1000 |

Compact AIML form: `evolve:2g:80:1000`

## Sample rules

- Gravity > 1.5g → higher bone density, lower stature, muscle mass
- Gravity < 0.7g → lighter skeleton, elongated limbs
- Water > 70% → aquatic adaptations
- Water < 25% → water retention / thick skin
- Temp < -50°C → slow metabolism, thermal insulation
- CO2-rich atmosphere → modified respiration + neural hypoxia alerts
- Generations ≥ 5000 → speciation signal

Survival probability starts at 0.75 and is penalized for extreme environments.

Code: `Backend/src/evolution/`.
