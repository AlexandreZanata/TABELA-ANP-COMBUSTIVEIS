# ANP Fuel Prices — Master Execution Plan

> **Location:** `.local/PROJECT_PLAN.md`  
> **Status:** Living document — **v1 complete; v2 in planning**  
> **Stack:** [docs/tech-stack.md](../docs/tech-stack.md)  
> **Architecture:** [docs/architecture.md](../docs/architecture.md)  
> **Product:** [docs/user-business-logic.md](../docs/user-business-logic.md)

Each item is a **micro-step**. Mark `[x]` when done. Do not skip **Validation gates** or **POCs** — they de-risk the hardest parts early.

**Legend**

| Symbol | Meaning |
|--------|---------|
| 🔬 | POC — throwaway or isolated spike; must pass gate before continuing |
| ✅ | Validation gate — hard stop; criteria must be 100% met |
| 📎 | References UC / BR / doc |
| ⏱ | Rough estimate (solo dev, focused) |

---

## Phase 0 — Repository & tooling baseline

**Goal:** Empty repo becomes a buildable Android multi-module skeleton.  
**Duration:** ~1–2 days ⏱

**✅ Gate 0.1:** Repo clones clean; no secrets tracked; cursor rules load in IDE.

---

## Phase 3 — POC: Room + FTS + batch import 🔬

**Goal:** Prove local DB handles import volume and city search.  
**Duration:** ~3–4 days ⏱  
📎 UC-004, UC-005, architecture schema

**✅ Gate 3 — Database POC passed:**

| Criterion | Target |
|-----------|--------|
| Summary import | ~2344 rows queryable |
| Station import | ~19676 rows queryable |
| FTS search | < 100ms for 3-char query |
| BR-003 | Re-import safe |
| DB file size (one week station) | < 15 MB |

---

## Phase 4 — POC: ANP network discovery & download 🔬

**Goal:** App finds and downloads latest XLSX from gov.br without hardcoded URLs.  
**Duration:** ~2 days ⏱  
📎 UC-001, `docs/data-sources.md`

**✅ Gate 4 — Network POC passed:**

| Criterion | Target |
|-----------|--------|
| Fixture test | Finds ≥2 URLs for latest week |
| Live test (manual) | Downloads resumo + revendas successfully |
| Filename parsing | BR-001 valid `SurveyWeek` from URL |

Document live test date in `.local/poc-results/network-poc.md`.

---

## Phase 5 — Application layer (use cases)

**Goal:** All UC-001…UC-008 orchestrated in `:application`.  
**Duration:** ~5–7 days ⏱  
📎 `docs/use-cases/*`

Implement **one use case at a time** — test before wiring UI.

**✅ Gate 5.2:** UC-001 tests green with mocked ports.

**✅ Gate 5 — Application complete:**

```bash
./gradlew :application:test :data:testDebugUnitTest
```

All UC-001…UC-008 have at least happy-path + one failure test.

---

## Phase 8 — Feature screens (MVP v1)

**Goal:** UC-002 through UC-008 shippable MVP.  
**Duration:** ~7–10 days ⏱

Implement in user journey order:

### 8.6 UC-008 Settings

**✅ Gate 8 — MVP feature complete:**

Manual test script (see Appendix A) passes on emulator + one physical device.

---

## Phase 9 — Hardening & quality

**Goal:** Production-ready open-source quality.  
**Duration:** ~4–5 days ⏱

### 9.1 Testing pyramid

### 9.2 Performance

### 9.3 Accessibility

### 9.4 Security

**✅ Gate 9:** `./gradlew test connectedCheck` (or documented CI equivalent) green.

---

## Phase 10 — Release & open source

**Goal:** Publishable OSS Android app.  
**Duration:** ~2–3 days ⏱

- [x] **10.1** Choose license (MIT or Apache 2.0) — `LICENSE` file

**✅ Gate 10 — Release:** Installable signed build; README accurate; license present. **Phase 10 complete.**

---

# v2 — National coverage, week picker, safe areas & fuel icons

> **Started:** 2026-06-18  
> **Goal:** Match and exceed the gov.br ANP listing experience on device — every municipality the government publishes, intelligent search, user-chosen survey week, correct safe areas on all screens, and recognizable fuel icons.  
> **Reference page:** [Levantamento de Preços — últimas semanas pesquisadas](https://www.gov.br/anp/pt-br/assuntos/precos-e-defesa-da-concorrencia/precos/levantamento-de-precos-de-combustiveis-ultimas-semanas-pesquisadas)  
> **Data source doc:** [docs/data-sources.md](../docs/data-sources.md)

## v2 product pillars

| # | Pillar | User-visible outcome |
|---|--------|----------------------|
| 1 | **All published municipalities** | Every city that appears in ANP spreadsheets is searchable; IBGE catalog fills gaps so users can find any Brazilian municipality and see whether ANP data exists for the selected week |
| 2 | **Intelligent search** | Accent-insensitive, typo-tolerant FTS across ~5 570 municipalities; state always shown; relevance-ranked results in &lt; 100 ms |
| 3 | **Survey week picker** | On every app entry (first launch and returning), user chooses **latest week** or a **specific historical week** before download — mirroring the gov.br week blocks |
| 4 | **Safe areas** | No content clipped under status bar, notch, or gesture navigation bar on **every** screen |
| 5 | **Fuel SVG icons** | Each `FuelProduct` shows a vector icon (OSS-licensed); no text-only fuel rows in price lists |

---

## Phase 11 — Municipality master catalog & intelligent search 🔬

**Goal:** User can find **any** municipality published by ANP (and any IBGE municipality for discovery), with search quality matching or beating gov.br usability.  
**Duration:** ~5–7 days ⏱  
📎 UC-004 (extend), UC-003, BR-010, BR-012, `docs/glossary.md`

**Context (v1 gap):** Summary import indexes only municipalities present in the imported `SurveyWeek` (~380 cities/week). v2 adds a persistent **MunicipalityCatalog** (IBGE 2024 baseline ~5 570 cities) merged with ANP-published names so search never misses a city the government has ever reported.

### 11.4 UI

- [x] **11.4.1** Search results row: municipality + state + optional "no data this week" subtitle (i18n)
- [x] **11.4.2** State/municipality picker: full list with section headers by letter
- [x] **11.4.3** Empty state illustrations for BR-010 variants

**✅ Gate 11 — National search complete:**

```bash
./gradlew :domain:test :application:test :data:testDebugUnitTest
```

Manual: search 10 cities including homonyms (e.g. "Bom Jesus" in multiple states) — all resolve correctly; cities without weekly data show informative empty state, not crash.

---

## Phase 12 — Survey week selection (gov.br parity)

**Goal:** User always chooses **which week** to download or consult — defaulting to latest — matching the ANP listing page structure.  
**Duration:** ~4–6 days ⏱  
📎 UC-001 (extend), UC-002 (extend), new **UC-009**, BR-001, BR-006, `docs/data-sources.md`

**Gov.br reference model (each block on listing page):**

```
{start_date} a {end_date}                    ← SurveyWeek label (pt-BR locale)
  • Preços médios semanais: Brasil, regiões, estados e municípios   ← WEEKLY_SUMMARY
  • Preços por posto revendedor (combustíveis automotivos e GLP P13) ← STATION_DETAIL
    (Atualizado em {publish_date})           ← optional, parse from HTML when present
  [NOTA / Aviso …]                           ← optional operational warning (e.g. Belo Horizonte gap)
```

The listing exposes **~200+ weeks** (Jul/2022 → present, Jun/2026). v2 does **not** bulk-download all weeks on first launch; user picks one week (or "latest") and may add more later.

### 12.2 Data layer — listing scraper enrichment 🔬

**✅ Gate 12.2 — Week catalog POC passed:**

| Criterion | Target |
|-----------|--------|
| Latest week | Both `WEEKLY_SUMMARY` + `STATION_DETAIL` URLs |
| Week label | Matches gov.br pt-BR date range formatting |
| Historical depth | ≥ 50 weeks discoverable from single HTML fetch |
| BH May/2026 note | Operational warning captured when present in fixture |
| Live catalog match | Discovered count matches complete visible week blocks (see `.local/poc-results/week-catalog-poc.md`) |

### 12.3 Application layer

### 12.4 UI — Week picker screen

- [ ] **12.4.6** App bar **SurveyWeekChip** on home, prices, stations, history — tap opens week picker sheet
- [ ] **12.4.7** i18n keys: `week_picker_title`, `week_picker_latest`, `week_picker_updated_at`, `week_picker_operational_note`, `week_picker_download_week`, `active_week_label`

**✅ Gate 12 — Week selection complete:**

Manual script (Appendix A2) passes; user can install fresh → pick week `31/05–06/06/2026` → see prices for that week → switch to latest → re-sync.

---

## Phase 13 — Safe area compliance (edge-to-edge)

**Goal:** `enableEdgeToEdge()` is already called in `MainActivity`; every screen must respect system bar insets so content is never clipped.  
**Duration:** ~2–3 days ⏱  
📎 Material 3 Compose guidelines, WCAG 2.5.5 touch targets

**Known gap:** No `WindowInsets` / `safeDrawing` padding in current Compose tree — content can render under status bar and gesture navigation bar.

### 13.1 Foundation

- [ ] **13.1.1** Create `AnpScaffold` composable: wraps Material3 `Scaffold` with `contentWindowInsets = WindowInsets.safeDrawing`; exposes `innerPadding` to content lambda
- [ ] **13.1.2** Create `AnpTopAppBar` wrapper applying `WindowInsets.statusBars` only where full-bleed hero is not used
- [ ] **13.1.3** Audit `MainActivity`: keep `enableEdgeToEdge()`; set `WindowCompat.setDecorFitsSystemWindows(window, false)` if not implicit
- [ ] **13.1.4** Document pattern in `docs/architecture.md` § UI — all new screens must use `AnpScaffold`

### 13.2 Screen-by-screen migration

- [ ] **13.2.1** Onboarding flow — top title and bottom CTA clear of system bars
- [ ] **13.2.2** Week picker (Phase 12) — list padding bottom ≥ nav bar inset
- [ ] **13.2.3** Home / search — search field and ANP footer attribution above nav bar
- [ ] **13.2.4** Location picker (state + municipality lists)
- [ ] **13.2.5** Municipality prices + fuel detail
- [ ] **13.2.6** Station list + station detail
- [ ] **13.2.7** Price history chart/list
- [ ] **13.2.8** Settings + dialogs / bottom sheets

### 13.3 Validation

- [ ] **13.3.1** Compose UI test: assert `AnpScaffold` applies non-zero top/bottom padding when insets mocked
- [ ] **13.3.2** Screenshot test matrix: phone with notch + gesture nav + tablet (if supported)

**✅ Gate 13 — Safe areas passed:**

| Criterion | Target |
|-----------|--------|
| Screens audited | 100% of navigation destinations |
| Manual check | No text/buttons under status or nav bar on emulator API 34 + one physical device |
| ANP footer | Fully visible above bottom inset |
| Bottom sheets | Handle nav bar inset; drag handle not obscured |

---

## Phase 14 — Fuel product SVG icons

**Goal:** Replace text-only `FuelProductLabel` with recognizable per-fuel vector icons using **open-source** assets (Petrobras trademark logos are **not** acceptable for OSS redistribution).  
**Duration:** ~2–3 days ⏱  
📎 `FuelProduct` enum (7 values), `FuelProductLabel.kt`

### 14.1 Asset sourcing & license

- [ ] **14.1.1** Evaluate icon sets — pick one primary library for consistency:

| Library | License | Notes |
|---------|---------|-------|
| [Material Design Icons (Pictogrammers)](https://pictogrammers.com/library/mdi/) | Apache 2.0 | `gas-station`, `fuel`, `propane-tank`; generic but consistent |
| [Phosphor Icons](https://phosphoricons.com/) | MIT | `GasPump`, `Drop`, `Flame`; good weight variants |
| [Tabler Icons](https://tabler.io/icons) | MIT | `gas-station`, `flame`; stroke style |

- [ ] **14.1.2** **Do not use** Petrobras, Ipiranga, or brand-specific pump artwork — trademark restriction
- [ ] **14.1.3** Create `docs/attribution.md` (or extend `NOTICE`) listing icon sources and licenses
- [ ] **14.1.4** Download SVG sources; convert to Android Vector Drawable (`app/src/main/res/drawable/ic_fuel_*.xml`)

### 14.2 Icon mapping (`FuelProduct` → drawable)

| `FuelProduct` | Icon concept | Suggested MDI name |
|---------------|--------------|-------------------|
| `ETHANOL` | Green drop / leaf fuel | `mdi-barrel` or custom ethanol drop |
| `GASOLINE_REGULAR` | Pump nozzle | `mdi-gas-station` |
| `GASOLINE_PREMIUM` | Pump + star badge | `mdi-gas-station` + overlay |
| `DIESEL_S500` | Pump / barrel (amber) | `mdi-oil` |
| `DIESEL_S10` | Pump / barrel (variant tint) | `mdi-oil` |
| `CNG` | Gas cylinder | `mdi-gas-cylinder` |
| `LPG_P13` | Propane tank | `mdi-propane-tank` |

- [ ] **14.2.1** Add tint tokens in `Color.kt` per fuel (reuse existing contrast-safe palette)
- [ ] **14.2.2** Create `FuelProductIcon` composable: icon + accessible label (`contentDescription` from i18n)
- [ ] **14.2.3** Replace text-only rows in price lists, home cards, station detail headers
- [ ] **14.2.4** Preview composables + screenshot tests for light/dark theme

**✅ Gate 14 — Fuel icons complete:**

All 7 fuels render vector icon in list rows; TalkBack reads localized fuel name; attribution file committed; `./gradlew :app:assembleRelease` succeeds with vector drawables.

---

## Phase 15 — v2 integration, hardening & release

**Goal:** Ship v2.0.0 with all gates green.  
**Duration:** ~3–4 days ⏱

### 15.1 Documentation

- [ ] **15.1.1** Update `docs/user-business-logic.md` — week picker journey, national search
- [ ] **15.1.2** Update `README.md` screenshots showing week picker + fuel icons
- [ ] **15.1.3** CHANGELOG v2.0.0

### 15.2 Quality

- [ ] **15.2.1** Full test suite green
- [ ] **15.2.2** Manual scripts Appendix A + A2 on emulator + physical device
- [ ] **15.2.3** DB migration test from v1.0.0 user data

**✅ Gate 15 — v2 release:** Signed APK/AAB; tag `v2.0.0`; GitHub Release notes mention week picker, national search, safe areas, fuel icons.

---

## Appendix A — Manual test script (MVP v1)

Run on **emulator** and **one physical device**:

| # | Step | Expected |
|---|------|----------|
| 1 | Fresh install | Onboarding shown |
| 2 | Complete sync | Progress → home or location |
| 3 | Search city ≥2 chars | Results with state |
| 4 | Select city | Prices per fuel shown |
| 5 | Enable airplane mode | Cached prices + offline banner |
| 6 | Disable airplane, refresh | Updated sync timestamp |
| 7 | Open station detail (download) | Station list by price |
| 8 | Settings → pt-BR | UI in Portuguese |
| 9 | Settings → clear station cache | Stations gone; averages remain |
| 10 | ANP attribution visible | Footer on price screens |

---

## Appendix A2 — Manual test script (v2)

Run on **emulator (API 34, gesture nav)** and **one physical device with notch**:

| # | Step | Expected |
|---|------|----------|
| 1 | Fresh install | Onboarding → **week picker** shown before sync |
| 2 | Tap **Use latest week** | Sync starts; progress visible; completes |
| 3 | Search `"san paolo"` (typo) | São Paulo (SP) in top results |
| 4 | Search `"Bom Jesus"` | Multiple states listed, disambiguated |
| 5 | Pick catalog city with no ANP data this week | Empty state explains no survey data (BR-010) |
| 6 | Open week picker → select `31/05–06/06/2026` | Re-sync; prices reflect that week |
| 7 | Operational note week (if available) | Banner shows gov.br warning text |
| 8 | Rotate device on home + prices | No content under status or nav bars |
| 9 | All price screens | Fuel icon + label per product; TalkBack reads name |
| 10 | Settings → change week | Active week chip updates globally |

---

## Appendix C — Risk register

| Risk | Mitigation | Phase |
|------|------------|-------|
| ANP page HTML changes | Fixture tests + fallback URL pattern docs | 4 |
| XLSX format changes | POI cross-validation tests on samples | 2 |
| Low-end device OOM | Streaming parser; never POI in prod | 2 |
| DB too large (years of stations) | BR-013 rolling window; summary always kept | 3, 5 |
| gov.br downtime | Offline-first BR-004; stale banner | 8 |
| Duplicate municipality names | Always show state in search results | 8 |
| IBGE ↔ ANP name mismatch | Normalization layer + alias table from import | 11 |
| Week catalog HTML structure change | Fixture tests + graceful fallback to URL-only discovery | 12 |
| 200+ weeks list UX | Virtualized list; show last 52 weeks expanded, "load more" | 12 |
| Large IBGE FTS index size | External-content FTS; benchmark on 2 GB RAM device | 11 |
| Petrobras logo license | Use OSS icon sets only; document in attribution | 14 |
| Edge-to-edge regression | `AnpScaffold` mandatory; lint check or arch unit test | 13 |

---

## Appendix D — Suggested timeline (solo dev)

### v1 (complete)

| Phase | Duration | Cumulative |
|-------|----------|------------|
| 0 — Setup | 1–2 days | ~2 days |
| 1 — Domain | 3–5 days | ~7 days |
| 2 — Parser POC | 3–4 days | ~11 days |
| 3 — DB POC | 3–4 days | ~15 days |
| 4 — Network POC | 2 days | ~17 days |
| 5 — Application | 5–7 days | ~24 days |
| 6 — WorkManager | 2 days | ~26 days |
| 7 — UI foundation | 2–3 days | ~29 days |
| 8 — Feature screens | 7–10 days | ~39 days |
| 9 — Hardening | 4–5 days | ~44 days |
| 10 — Release | 2–3 days | **~47 days (~9 weeks)** |

### v2 (in progress)

| Phase | Duration | Cumulative |
|-------|----------|------------|
| 11 — National search | 5–7 days | ~54 days |
| 12 — Week picker | 4–6 days | ~60 days |
| 13 — Safe areas | 2–3 days | ~63 days |
| 14 — Fuel icons | 2–3 days | ~66 days |
| 15 — v2 release | 3–4 days | **~70 days (~14 weeks total)** |

Parallelization: Phase 13 (safe areas) can start alongside Phase 11 UI; Phase 14 (icons) independent of Phase 12.

---

## Appendix E — Definition of Done (per micro-step)

A step is **done** only when:

1. Code merged on feature branch
2. Tests written first or alongside (TDD for domain)
3. i18n keys added for any new UI string
4. No layer violation (lint / manual import check)
5. Commit message follows `docs/commit-conventions.md`
6. Related UC/BR referenced in commit body if applicable

---

## Appendix F — Gov.br listing page spec (week picker reference)

Source: [ANP — Levantamento de Preços (últimas semanas pesquisadas)](https://www.gov.br/anp/pt-br/assuntos/precos-e-defesa-da-concorrencia/precos/levantamento-de-precos-de-combustiveis-ultimas-semanas-pesquisadas)  
Last verified: 2026-06-12 (page updated 12/06/2026 14h24)

### Page structure (what the app must mirror)

Each **SurveyWeek block** on the page contains:

1. **Heading** — date range in pt-BR: `07/06/2026 a 13/06/2026` (maps to `SurveyWeek`)
2. **Link A** — `Preços médios semanais: Brasil, regiões, estados e municípios` → `WEEKLY_SUMMARY` XLSX
3. **Link B** — `Preços por posto revendedor (combustíveis automotivos e GLP P13)` → `STATION_DETAIL` XLSX
4. **Updated timestamp** — `(Atualizado em 12/6/2026)` per link when present
5. **Operational note** — optional paragraph (e.g. Belo Horizonte missing 26/04–16/05/2026; Goiatuba correction 21–27/05/2023)

### URL patterns (already implemented in v1)

| Type | Pattern |
|------|---------|
| Summary | `…/arquivos-lpc/{YEAR}/resumo_semanal_lpc_{start}_{end}.xlsx` |
| Stations | `…/arquivos-lpc/{YEAR}/revendas_lpc_{start}_{end}.xlsx` |

Dates in URLs use ISO `YYYY-MM-DD`; headings use pt-BR `dd/MM/yyyy`.

### v2 UX mapping

| Gov.br element | App component |
|----------------|---------------|
| Week heading | `WeekPickerScreen` list row primary text |
| Link A + B | Single row selection downloads both (summary always; station per setting) |
| "Atualizado em …" | Row subtitle `week_picker_updated_at` |
| NOTA / Aviso | Collapsible `OperationalNoteBanner` |
| First block (newest) | **Use latest week** CTA — equivalent to clicking most recent block |

### Discovery depth

The live page lists weeks back to **31/07/2022–06/08/2022** and growing. The app stores discovered metadata locally; it does **not** pre-download historical files until the user selects a week.

---

## Quick reference — what to build next

**v1 (complete):** Phases 0–10 ✓ — MVP v1.0.0 tagged locally.

**v2 (start here):**

```
Phase 11.4 (use cases + UI) → Gate 11
    → Phase 12.1–12.2 (UC-009 + week catalog POC) → Gate 12.2
    → Phase 12.3–12.4 (sync + WeekPickerScreen) → Gate 12
    → Phase 13 (AnpScaffold + screen audit) → Gate 13   ← can parallelize from 11.4
    → Phase 14 (fuel SVG icons) → Gate 14             ← can parallelize from 12
    → Phase 15 (hardening + v2.0.0) → Gate 15
```

**Current repo status (2026-06-19):** Phase 12.4.5 returning-user launch routing complete ✓ — next micro-step **Phase 12.4.6** SurveyWeekChip on app bars.
