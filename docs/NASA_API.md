# NASA Exoplanet Archive TAP

Chronos uses the public TAP endpoint (no API key):

`https://exoplanetarchive.ipac.caltech.edu/TAP/sync`

## Query used

```sql
select top 1 pl_name, hostname, pl_eqt, pl_rade, pl_bmasse, pl_orbsmax, sy_dist
from pscomppars
where lower(pl_name) like '%kepler-442%'
```

Requested with `format=json`.

## Fields exposed in chat

| Field | Meaning |
|-------|---------|
| `pl_eqt` | Equilibrium temperature (K) |
| `pl_rade` | Radius (Earth radii) |
| `pl_bmasse` | Mass (Earth masses) |
| estimated g | `mass / radius²` |
| `pl_orbsmax` | Orbital semi-major axis (AU) |
| `sy_dist` | System distance (parsec) |

Habitable-zone proxy: equilibrium temperature between 180 K and 310 K.

Implementation: [`Backend/src/ExoplanetService.java`](../Backend/src/ExoplanetService.java).
