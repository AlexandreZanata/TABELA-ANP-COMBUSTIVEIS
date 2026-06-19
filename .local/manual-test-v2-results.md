# Manual test results — v2.0.0 (Phase 15.2.2)

> **Appendix A** (v1 regression) and **Appendix A2** (v2 features) from [.local/PROJECT_PLAN.md](../.local/PROJECT_PLAN.md).

## Automated coverage (emulator / CI)

| Area | Automated test |
|------|----------------|
| Fuel icons light/dark | `FuelProductIconScreenshotTest` |
| Safe-area layouts | `SafeAreaScreenshotMatrixTest`, `AnpScaffoldTest` |
| DB v1 → v3 migration | `V1ToV2DatabaseMigrationTest` |
| FTS national search | `MunicipalityFtsSearchTest` |
| Week catalog parsing | `AnpListingFullFixtureTest` |

## Appendix A2 — v2 manual checklist

Run on **emulator (API 34, gesture nav)** and **one physical device with notch**.

| # | Step | Emulator | Physical device |
|---|------|----------|-----------------|
| 1 | Fresh install → week picker before sync | ☐ | ☐ |
| 2 | Use latest week → sync completes | ☐ | ☐ |
| 3 | Search `"san paolo"` → São Paulo (SP) | ☐ | ☐ |
| 4 | Search `"Bom Jesus"` → multiple states | ☐ | ☐ |
| 5 | City with no ANP data → BR-010 empty state | ☐ | ☐ |
| 6 | Select week `31/05–06/06/2026` → prices update | ☐ | ☐ |
| 7 | Operational note week → banner visible | ☐ | ☐ |
| 8 | Rotate home + prices → safe areas | ☐ | ☐ |
| 9 | Fuel icon + label; TalkBack reads name | ☐ | ☐ |
| 10 | Settings → change week → chip updates | ☐ | ☐ |

## Appendix A — v1 regression (spot check)

| # | Step | Emulator | Physical device |
|---|------|----------|-----------------|
| 1–10 | See PROJECT_PLAN Appendix A | ☐ | ☐ |

**Sign-off:** complete checkboxes on release candidate before tagging `v2.0.0`.
