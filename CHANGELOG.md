# Changelog

All notable changes to this project are documented in this file.

Format based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [2.0.0] - 2026-06-19

### Added

- **Survey week picker (UC-009)** — choose latest or historical ANP week before sync; gov.br catalog with operational notes
- **Active survey week chip** on app bars with bottom-sheet week picker for returning users
- **National municipality search** — IBGE catalog (~5 570 cities) with FTS ranking and state disambiguation (BR-016, BR-017)
- **Edge-to-edge safe areas** — `AnpScaffold` / `AnpTopAppBar` on all navigation screens (Phase 13)
- **Fuel product vector icons** — MDI drawables with per-fuel tints and TalkBack labels (Phase 14)
- `docs/attribution.md` for open-source icon licenses
- Database migration v2 → v3: `municipality_catalog` + catalog-backed FTS (`V1ToV2DatabaseMigrationTest`)

### Changed

- Onboarding flow includes week selection step before first sync (UC-002 extended)
- Search works on full national catalog offline after IBGE seed (UC-004 extended)
- App version **2.0.0** (versionCode 2)

### Fixed

- Content no longer renders under status bar or gesture navigation bar on edge-to-edge devices

## [1.0.0] - 2026-06-01

### Added

- MVP release: UC-001 through UC-008
- Onboarding, city search, fuel averages, price history, optional station detail
- Offline-first sync with WorkManager
- i18n: English (default) and Brazilian Portuguese

[2.0.0]: https://github.com/AlexandreZanata/TABELA-ANP-COMBUSTIVEIS/compare/v1.0.0...v2.0.0
[1.0.0]: https://github.com/AlexandreZanata/TABELA-ANP-COMBUSTIVEIS/releases/tag/v1.0.0
