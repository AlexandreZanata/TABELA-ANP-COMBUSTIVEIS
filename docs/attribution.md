# Third-party attribution

This document lists open-source assets bundled with the ANP Fuel Prices app and their licenses.

## Fuel product icons (Phase 14)

### Library selection

| Library | License | Decision |
|---------|---------|----------|
| [Material Design Icons (Pictogrammers)](https://pictogrammers.com/library/mdi/) | Apache 2.0 | **Selected** — aligns with Material 3 UI, single consistent set, fuel-related glyphs available |
| [Phosphor Icons](https://phosphoricons.com/) | MIT | Not selected — would mix stroke weights with Material components |
| [Tabler Icons](https://tabler.io/icons) | MIT | Not selected — stroke style diverges from filled MDI icons |

### Trademark policy (BR-014 / Phase 14.1.2)

Icons are **generic** fuel-industry symbols only. The app does **not** include:

- Petrobras, Ipiranga, Shell, or any retail brand logos
- Brand-specific pump artwork or trademarked color schemes tied to a distributor

### Source

- **Project:** [Pictogrammers / Material Design Icons](https://pictogrammers.com/library/mdi/)
- **Upstream repository:** [Templarian/MaterialDesign-SVG](https://github.com/Templarian/MaterialDesign-SVG)
- **License:** [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)
- **SVG copies in repo:** `docs/assets/icons/mdi/`

### Icon mapping (`FuelProduct` → MDI → drawable)

| `FuelProduct` | MDI icon | Android drawable | Notes |
|---------------|----------|------------------|-------|
| `ETHANOL` | `barrel` | `ic_fuel_ethanol.xml` | Biofuel barrel metaphor |
| `GASOLINE_REGULAR` | `gas-station` | `ic_fuel_gasoline_regular.xml` | Generic pump / station |
| `GASOLINE_PREMIUM` | `gas-station` | `ic_fuel_gasoline_premium.xml` | Same base; star badge in Phase 14.2 |
| `DIESEL_S500` | `oil` | `ic_fuel_diesel_s500.xml` | Oil drop + dispenser |
| `DIESEL_S10` | `oil` | `ic_fuel_diesel_s10.xml` | Same base; variant tint in Phase 14.2 |
| `CNG` | `gas-cylinder` | `ic_fuel_cng.xml` | Compressed natural gas cylinder |
| `LPG_P13` | `propane-tank` | `ic_fuel_lpg_p13.xml` | LPG tank |

Drawables use `#FF000000` fill so Compose `Icon` tint tokens apply per fuel in Phase 14.2.

### Apache 2.0 notice

```
Copyright Material Design Icons / Pictogrammers contributors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use these files except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## ANP data source

Fuel price data originates from the Brazilian National Agency of Petroleum, Natural Gas and Biofuels (ANP). ANP datasets are public government information; see [docs/data-sources.md](data-sources.md) for URLs and update cadence.

## Related documents

- [architecture.md](architecture.md) — UI layer
- [glossary.md](glossary.md) — `FuelProduct` domain terms
