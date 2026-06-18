# ANP Fuel Prices — Master Execution Plan

> **Location:** `.local/PROJECT_PLAN.md`  
> **Status:** Living document  
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

### 0.1 Git & conventions

- [ ] **0.1.1** Confirm `.gitignore` covers Gradle, IDE, secrets, `data/downloads/`
- [ ] **0.1.2** Confirm `.cursor/rules/` present (agent core + stack + i18n + commits)
- [ ] **0.1.3** Read `docs/commit-conventions.md` — align on Conventional Commits in English

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

### 8.5 UC-007 Stations (on-demand)

- [ ] **8.5.1** `StationsScreen` — sorted list, download prompt
- [ ] **8.5.2** One-time station file download flow

**✅ Gate 8.5:** Station list loads after on-demand download; shows brand, address, price.

### 8.6 UC-008 Settings

- [ ] **8.6.1** `SettingsScreen` — toggles, storage stats, clear cache, sync now
- [ ] **8.6.2** Language switch
- [ ] **8.6.3** ANP link (BR-009)

**✅ Gate 8 — MVP feature complete:**

Manual test script (see Appendix A) passes on emulator + one physical device.

---

## Phase 9 — Hardening & quality

**Goal:** Production-ready open-source quality.  
**Duration:** ~4–5 days ⏱

### 9.1 Testing pyramid

- [ ] **9.1.1** Domain coverage ≥ 90%
- [ ] **9.1.2** Application: all use cases have tests
- [ ] **9.1.3** Data: parser + repository integration tests
- [ ] **9.1.4** UI: 2–3 critical Compose tests (search, home prices)

### 9.2 Performance

- [ ] **9.2.1** Import 20K rows on mid-range emulator < 60s
- [ ] **9.2.2** APK size check — target < 15 MB (without samples)
- [ ] **9.2.3** Startup time < 2s cold start to home (with cache)

### 9.3 Accessibility

- [ ] **9.3.1** TalkBack pass on main flows
- [ ] **9.3.2** Font scaling 200% — no clipped text
- [ ] **9.3.3** Contrast check (WCAG AA)

### 9.4 Security

- [ ] **9.4.1** `android:usesCleartextTraffic="false"`
- [ ] **9.4.2** No secrets in repo scan
- [ ] **9.4.3** ProGuard/R8 rules for release if needed

**✅ Gate 9:** `./gradlew test connectedCheck` (or documented CI equivalent) green.

---

## Phase 10 — Release & open source

**Goal:** Publishable OSS Android app.  
**Duration:** ~2–3 days ⏱

- [x] **10.1** Choose license (MIT or Apache 2.0) — `LICENSE` file
- [ ] **10.2** `CONTRIBUTING.md` — points to docs, commit conventions, TDD
- [x] **10.3** README badges (build, license) — license badge added; build badge pending
- [ ] **10.4** Play Store listing draft (optional sideload first)
- [ ] **10.5** Signed release APK/AAB
- [ ] **10.6** GitHub Release v1.0.0 notes — ANP attribution, offline disclaimer
- [ ] **10.7** Tag `v1.0.0`

**✅ Gate 10 — Release:** Installable signed build; README accurate; license present.

---

## Appendix A — Manual test script (MVP)

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

## Appendix B — POC results folder

Create as POCs complete:

```
.local/poc-results/
├── parser-poc.md      # row counts, memory, timing
├── database-poc.md    # FTS latency, DB size
└── network-poc.md     # live scrape date, URL count
```

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

---

## Appendix D — Suggested timeline (solo dev)

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

Parallelization (if 2 devs): Phase 2/3/4 POCs can overlap after Phase 1; UI Phase 7 can start during Phase 5.

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

## Quick reference — what to build next

**If starting from zero today:**

```
Phase 2.1 (Parser POC) → Gate 2
         → Phase 2 (Parser POC) → Gate 2
         → Phase 3 (DB POC) → Gate 3
         → …
```

**Current repo status (2026-06-18):** Phase 8.4 UC-006 History complete ✓ — next action **Phase 8.5 Stations (on-demand)**.
