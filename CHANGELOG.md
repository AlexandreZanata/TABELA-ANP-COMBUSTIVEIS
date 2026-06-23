# Changelog

All notable changes to this project are documented in this file.

Format based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [3.1.0] - 2026-06-23

### Added

- **Horizontal vehicle carousel on Home** — swipe between registered vehicles; tank cost card follows selection
- **Use current location** in location picker — resolves municipality via device GPS (`ResolveDeviceLocationUseCase`)
- **Share app** in Settings — system share sheet with direct APK download link
- **Back navigation** on secondary screens (History, Prices, Search, Stations, Settings, Location picker)
- **Stale price table warning** on Home when the latest survey week end date is before today

### Changed

- Tank cost card layout and station row tap-to-navigate UX (Maps with station name, address, and city — BR-026)
- System locale detection with pt-BR fallback when device language is unsupported
- New UI strings localized in all 8 supported locales (en, pt-BR, es, fr, de, ja, ru, zh-CN)
- App version **3.1.0** (versionCode 4)

### Fixed

- First-launch onboarding sync failure (listing cache, catalog URL reuse, sync lock, auto-retry)
- Vehicle form specific-station picker — full-width selectable rows instead of squashed filter chips
- Home no longer reloads when returning from other screens (scroll position preserved)

## [3.0.0] - 2026-06-21

### Added

- **Vehicle profiles (UC-010)** — register up to 3 vehicles with tank capacity, fuel type, and price reference (cheapest station or specific CNPJ)
- **Tank fill cost on Home (UC-011)** — estimated full-tank cost per vehicle using active survey week prices
- **Optional device location (UC-012)** — one-shot GPS + Nominatim reverse geocode during onboarding; coordinates not stored (BR-021)
- **Station navigation (UC-013)** — open Maps/Waze from station list with normalized address query (BR-026)
- **Local price drop alerts (UC-014)** — on-device notifications after weekly import when tracked price falls vs previous week (BR-025)
- `NotificationPermissionHandler`, `PriceDropEvaluationWorker`, and notification channel for UC-014
- Database migration v3 → v4: `vehicle` table

### Changed

- Onboarding may show optional location prompt after first sync (UC-002 extended)
- Settings links to system notification settings when alerts are enabled but permission is denied
- App version **3.0.0** (versionCode 3)
- Privacy policy updated for optional location, Nominatim, vehicles, and local notifications

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

[3.1.0]: https://github.com/AlexandreZanata/TABELA-ANP-COMBUSTIVEIS/compare/v3.0.0...v3.1.0
[3.0.0]: https://github.com/AlexandreZanata/TABELA-ANP-COMBUSTIVEIS/compare/v2.0.0...v3.0.0
[2.0.0]: https://github.com/AlexandreZanata/TABELA-ANP-COMBUSTIVEIS/compare/v1.0.0...v2.0.0
[1.0.0]: https://github.com/AlexandreZanata/TABELA-ANP-COMBUSTIVEIS/releases/tag/v1.0.0
