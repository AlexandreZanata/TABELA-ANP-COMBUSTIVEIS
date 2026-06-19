# ANP Fuel Prices — Remaining Work Plan

> **Location:** `.local/PROJECT_PLAN.md`  
> **Status:** v2 **code-complete** — release, validation & UX gaps **pending**  
> **Stack:** [docs/tech-stack.md](../docs/tech-stack.md) · **Product:** [docs/user-business-logic.md](../docs/user-business-logic.md)

**Already shipped in repo (do not re-implement):**

| Scope | Phases | Evidence |
|-------|--------|----------|
| v1 MVP | 0–10 | Tag `v1.0.0`, UC-001…UC-008 |
| v2 features | 11–15 impl | Week picker, national search, `AnpScaffold`, fuel icons, docs, JVM unit tests green |

This document lists **only what remains** to reach **100% plan completion** (all gates green + Gate 15 release).

**Legend:** `[ ]` todo · `[x]` done · ✅ validation gate (hard stop)

---

## Overview — gates still open

| Gate | Blocker | Phase |
|------|---------|-------|
| **Gate 11** | Manual search script (10 cities + homonyms) | R3 |
| **Gate 12** | Appendix A2 manual not signed off | R3 |
| **Gate 13** | Manual safe-area check (API 34 + notch device) | R3 |
| **Gate 15** | GitHub Release publish + Appendix A2 sign-off pending | R2/R3 |
| **Risk 12** | Week list “load more” UX (200+ weeks) | R5 |
| **15.1.2** | README screenshot PNGs missing | R4 |

---

## Phase R3 — Manual validation (Appendix A + A2)

**Goal:** Sign off all manual gates. Track progress in [.local/manual-test-v2-results.md](manual-test-v2-results.md).

**Environment:** Emulator **API 34, gesture navigation** + **one physical device with notch**.

### R3.1 Appendix A2 — v2 script (10 steps × 2 devices)

- [ ] **R3.1.1** Step 1 — Fresh install → week picker before sync
- [ ] **R3.1.2** Step 2 — **Use latest week** → sync completes
- [ ] **R3.1.3** Step 3 — Search `"san paolo"` → São Paulo (SP)
- [ ] **R3.1.4** Step 4 — Search `"Bom Jesus"` → multiple states disambiguated
- [ ] **R3.1.5** Step 5 — City with no ANP data → BR-010 empty state
- [ ] **R3.1.6** Step 6 — Week `31/05–06/06/2026` → prices reflect that week
- [ ] **R3.1.7** Step 7 — Operational note week → banner visible (if catalog has one)
- [ ] **R3.1.8** Step 8 — Rotate home + prices → no clip under system bars
- [ ] **R3.1.9** Step 9 — Fuel icon + label; TalkBack reads fuel name
- [ ] **R3.1.10** Step 10 — Settings change week → chip updates globally

### R3.2 Appendix A — v1 regression spot-check

- [ ] **R3.2.1** Run Appendix A steps 1–10 on emulator (offline, stations, pt-BR, cache clear)
- [ ] **R3.2.2** Repeat critical paths on physical device

### R3.3 Gate 11 — National search manual

- [ ] **R3.3.1** Search **10 cities** including homonyms (e.g. Bom Jesus, São Paulo, Curitiba, Rio de Janeiro, Salvador, Goiânia, Florianópolis, Belém, Maceió, Porto Alegre)
- [ ] **R3.3.2** Confirm cities without weekly data show informative empty state (no crash)

### R3.4 Gate 13 — Safe areas manual

- [ ] **R3.4.1** Status bar: no title/button clipped on all main screens
- [ ] **R3.4.2** Gesture nav bar: ANP footer + bottom CTAs fully visible
- [ ] **R3.4.3** Week picker bottom sheet: list clears nav bar inset; drag handle visible
- [ ] **R3.4.4** Landscape rotation on home + prices

### R3.5 Sign-off

- [ ] **R3.5.1** Mark all checkboxes in `.local/manual-test-v2-results.md`
- [ ] **R3.5.2** Record test date, emulator profile, and device model in that file

**✅ Gate R3 — Manual validation passed:** A2 + Gate 11 + Gate 13 criteria met on emulator **and** physical device.

---

## Phase R4 — README screenshots (15.1.2)

**Goal:** README screenshot links resolve to real PNG assets.

- [ ] **R4.1** Capture `docs/screenshots/v2/week-picker.png` — week list + **Use latest week**
- [ ] **R4.2** Capture `docs/screenshots/v2/home-fuel-icons.png` — prices with icons + week chip
- [ ] **R4.3** Capture `docs/screenshots/v2/search-national.png` — search with state disambiguation
- [ ] **R4.4** Capture `docs/screenshots/v2/safe-area.png` — edge-to-edge layout (portrait or landscape)
- [ ] **R4.5** Verify README `#screenshots` section renders all four images

Capture guide: [docs/screenshots/v2/README.md](../docs/screenshots/v2/README.md)

**✅ Gate R4 — Screenshots complete:** All four PNGs committed; README links valid.

---

## Phase R5 — Week list “load more” UX (Appendix C risk #12)

**Goal:** Mitigate 200+ week catalog UX — show recent weeks first, paginate older blocks.

**Reference:** Risk register — *Virtualized list; show last 52 weeks expanded, "load more"*.

- [ ] **R5.1** Define visible window constant (e.g. `INITIAL_WEEK_PAGE_SIZE = 52`) in UI layer
- [ ] **R5.2** `WeekPickerContent` — render only first N weeks by default; older years collapsed or hidden
- [ ] **R5.3** Add **Load more weeks** CTA (i18n: `week_picker_load_more`, `week_picker_load_more_count`)
- [ ] **R5.4** Append next page on tap; preserve scroll position
- [ ] **R5.5** Same behavior in `WeekPickerBottomSheet` (sheet height constraint)
- [ ] **R5.6** Compose UI test: catalog with 60+ entries shows CTA; tap expands list
- [ ] **R5.7** Update [docs/user-business-logic.md](../docs/user-business-logic.md) week picker section if UX changes

**✅ Gate R5 — Week list UX passed:** 200+ week catalog usable without scrolling entire history on first open.

---

## Phase R6 — Optional hardening (close remaining risks)

**Not blocking Gate 15** — complete for 100% risk-register mitigation.

- [ ] **R6.1** Edge-to-edge regression guard — ArchUnit or lint rule: no raw `Scaffold`/`TopAppBar` in `app/ui/**` (except `AnpScaffold` / `AnpTopAppBar` wrappers)
- [ ] **R6.2** IBGE FTS benchmark on 2 GB RAM device — document result in `.local/poc-results/fts-benchmark.md`
- [ ] **R6.3** Play Store listing draft update for v2 features (`docs/play-store-listing.md`)

---

## Appendix A — Manual test script (MVP v1 regression)

Run on **emulator** and **one physical device** (Phase R3.2):

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

Run on **emulator (API 34, gesture nav)** and **one physical device with notch** (Phase R3.1):

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

## Quick reference — completion order

```
R5 (load more UX) → R4 (screenshots)
    → R3 (manual A + A2 + Gate 11/13) → R2 (release scripts + tag + GitHub)
    → R6 (optional hardening)
```

**100% plan complete when:** R2 ✅ R3 ✅ R4 ✅ R5 ✅ (R6 optional).

---

## Completed work archive (reference only)

<details>
<summary>v1 Phases 0–10 · v2 Phases 11–15 implementation (click to expand)</summary>

- **v1:** Repo setup, domain, parser POC, DB POC, network POC, application layer UC-001…UC-008, WorkManager, UI foundation, feature screens, hardening, MIT license, tag `v1.0.0`
- **v2 Phase 11:** MunicipalityCatalog, IBGE seed, FTS ranking, search UI (11.4.1–11.4.3)
- **v2 Phase 12:** Week catalog scraper (Gate 12.2), UC-009, WeekPickerScreen, SurveyWeekChip, onboarding week step
- **v2 Phase 13:** AnpScaffold, AnpTopAppBar, screen migration, AnpScaffoldTest, SafeAreaScreenshotMatrixTest
- **v2 Phase 14:** MDI fuel drawables, FuelProductIcon, FuelProductTint, attribution.md
- **v2 Phase 15 (partial):** user-business-logic.md, CHANGELOG, v2.0.0 release notes draft, versionName 2.0.0, V1ToV2DatabaseMigrationTest, JVM tests green
- **R1 (automated gates):** JVM suite, connectedCheck (Gate 9), assembleRelease (Gate 14), verifyReleaseApkSize, securityCheck — all green on device 2311DRK48G (2026-06-19)
- **R2.1 (release scripts):** validate-release-tag, validate-github-release-notes, create-release-tag, Gradle tasks, README — pinned to v2.0.0 / Gate 15 (2026-06-19)
- **R2.2 (build & tag local):** validateReleaseBuild (signed APK ~2.05 MB + AAB), validateReleaseTag, annotated tag `v2.0.0` at `55f0cbb` — local only (2026-06-19)
- **R2.3 (publish dry-run):** validateGate15Release + publishGithubRelease dry-run; `publish-github-release.sh --publish` for maintainer push + gh release (2026-06-19)

</details>
