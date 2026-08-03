# NASA Exoplanet Archive TAP

Chronos uses the public TAP endpoint (no API key):

`https://exoplanetarchive.ipac.caltech.edu/TAP/sync`

Table: `pscomppars` ([column definitions](https://exoplanetarchive.ipac.caltech.edu/docs/API_PS_columns.html)).

## Shared client

[`NasaTapClient`](../Backend/src/NasaTapClient.java) — injectable fetcher for unit tests. Used by:

- [`ExoplanetService`](../Backend/src/ExoplanetService.java) — short `ANALYZE PLANET` scan
- [`ObservatoryService`](../Backend/src/ObservatoryService.java) — Deep Scan, Compare, Speciation Vault

## ADQL select (Observatory / Deep Scan)

```sql
select top 1
  pl_name, hostname, pl_eqt, pl_rade, pl_bmasse, pl_orbsmax, sy_dist,
  pl_orbper, pl_dens, pl_insol, discoverymethod, disc_year, disc_facility,
  st_teff, st_spectype
from pscomppars
where lower(pl_name) like '%kepler-442%'
```

Requested with `format=json`. Missing values render as `n/d`.

## Fields

| Field | Meaning | Used in |
|-------|---------|---------|
| `pl_eqt` | Equilibrium temperature (K) | Scan, Deep Scan, Compare, Vault |
| `pl_rade` | Radius (Earth radii) | all |
| `pl_bmasse` | Mass (Earth masses) | all |
| estimated g | `mass / radius²` (labeled *estimated*) | all |
| `pl_orbsmax` | Orbital semi-major axis (AU) | Scan, Deep Scan |
| `sy_dist` | System distance (parsec) | Scan, Deep Scan, Compare |
| `pl_orbper` | Orbital period (days) | Deep Scan |
| `pl_dens` | Density (g/cm³) | Deep Scan |
| `pl_insol` | Insolation (Earth flux) | Deep Scan |
| `discoverymethod` | Discovery method | Deep Scan |
| `disc_year` | Discovery year | Deep Scan |
| `disc_facility` | Facility / mission | Deep Scan |
| `st_teff` | Host effective temperature (K) | Deep Scan |
| `st_spectype` | Spectral type | Deep Scan |

Habitable-zone **proxy**: equilibrium temperature between 180 K and 310 K (not a full HZ model).

Compare ranks by closest `pl_eqt` to Earth ≈ 255 K — ranking aid only, still labeled proxy.

See also [`OBSERVATORY_CHANNEL.md`](OBSERVATORY_CHANNEL.md).
