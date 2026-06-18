# ANP Fuel Prices

[![CI](https://github.com/AlexandreZanata/TABELA-ANP-COMBUSTIVEIS/actions/workflows/ci.yml/badge.svg)](https://github.com/AlexandreZanata/TABELA-ANP-COMBUSTIVEIS/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

Open-source Android app to browse Brazilian ANP (National Petroleum Agency) weekly fuel price surveys — fully offline-capable, no backend required.

**100% open source and free to use** under the [MIT License](LICENSE). The only requirement when reusing or redistributing this project is to **reference the original source** — see [docs/license.md](docs/license.md).

## Features

- Automatic download of latest ANP price tables from [gov.br/anp](https://www.gov.br/anp/pt-br/assuntos/precos-e-defesa-da-concorrencia/precos/levantamento-de-precos-de-combustiveis-ultimas-semanas-pesquisadas)
- Browse average prices by fuel type, state, and city
- Search cities with autocomplete
- Optional per-station detail (CNPJ, address, brand, price)
- Historical price trends (local storage)
- 100% on-device processing — no server

## Stack (definitive)

| Concern | Technology |
|---------|------------|
| Language | Kotlin 2.0+ |
| UI | Jetpack Compose + Material Design 3 |
| Architecture | Clean Architecture — `:domain` / `:application` / `:data` / `:app` |
| Database | Room + SQLite + FTS5 |
| Background sync | WorkManager + OkHttp + Jsoup |
| XLSX parsing | Streaming XLSX parser (custom; Apache POI test-only) |
| DI | Hilt |
| i18n | Android string resources (`en` default, `pt-BR`) |
| Tests | JUnit 5 + MockK + Turbine |

Full specification: **[docs/tech-stack.md](docs/tech-stack.md)** · ADR: **[docs/adr/001-kotlin-compose-stack.md](docs/adr/001-kotlin-compose-stack.md)**

## Project structure

```
:app/               # Compose UI, ViewModels, theme, navigation
:application/       # Use cases (UC-001…UC-008)
:domain/            # Entities, Value Objects, rules, events, ports
:data/              # Room, OkHttp, parser, WorkManager, repositories
docs/               # Architecture, use cases, glossary
data/samples/       # Reference ANP spreadsheets for parser tests
.cursor/rules/      # Agent engineering contract
```

Package root: `com.anpfuel`

See **[docs/architecture.md](docs/architecture.md)** for package layout, data flow, and database schema.

## Sample data

Reference files for week **2026-06-07 → 2026-06-13** are in `data/samples/`:

| File | Size | Rows | Purpose |
|------|------|------|---------|
| `resumo_semanal_lpc_*.xlsx` | ~287 KB | ~3.9K (municipalities) | Averages by region/state/city |
| `revendas_lpc_*.xlsx` | ~1.8 MB | ~19.7K | Per-station prices |

See [docs/data-sources.md](docs/data-sources.md) for column schemas and download URL patterns.

## Documentation

| Document | Purpose |
|----------|---------|
| [docs/user-business-logic.md](docs/user-business-logic.md) | **Product contract** — user journeys, states, rules |
| [docs/use-cases/](docs/use-cases/) | Detailed use cases (UC-001 … UC-008) |
| [docs/glossary.md](docs/glossary.md) | Domain language + business rules (BR-001 … BR-015) |
| [docs/architecture.md](docs/architecture.md) | Layers, packages, data flow, database |
| [docs/tech-stack.md](docs/tech-stack.md) | **Definitive stack** — libraries and module deps |
| [docs/adr/001-kotlin-compose-stack.md](docs/adr/001-kotlin-compose-stack.md) | Architecture decision record |
| [docs/data-sources.md](docs/data-sources.md) | ANP file formats and URL patterns |
| [docs/commit-conventions.md](docs/commit-conventions.md) | Git commit and PR standards |
| [CONTRIBUTING.md](CONTRIBUTING.md) | How to contribute — TDD, layers, i18n, PRs |
| [docs/license.md](docs/license.md) | MIT license — free use, attribution required |
| [docs/play-store-listing.md](docs/play-store-listing.md) | Play Store listing draft (en + pt-BR) |
| [docs/privacy-policy.md](docs/privacy-policy.md) | Privacy policy — no personal data collected |
| [docs/release-build.md](docs/release-build.md) | Signed release APK/AAB build instructions |
| [docs/releases/v1.0.0.md](docs/releases/v1.0.0.md) | GitHub Release v1.0.0 draft notes |
| [.local/PROJECT_PLAN.md](.local/PROJECT_PLAN.md) | Micro-step execution plan (POCs & gates) |

## Development

Before implementing a feature:

1. Read the use case in `docs/use-cases/` (do not implement undocumented flows)
2. Check business rules in `docs/glossary.md`
3. Verify stack and layer placement in `docs/architecture.md`
4. Write domain tests first (TDD, GIVEN/WHEN/THEN)
5. Add i18n keys for all user-visible strings
6. Follow [commit conventions](docs/commit-conventions.md) (English, Conventional Commits)

See **[CONTRIBUTING.md](CONTRIBUTING.md)** for the full contributor guide.

## Releases

**Latest:** [v1.0.0](docs/releases/v1.0.0.md) — MVP with UC-001…UC-008. Sideload the signed APK from GitHub Releases (see [release build guide](docs/release-build.md)).

Validate a release locally:

```bash
./gradlew validateReleaseTag      # Gate 10 readiness (no build)
./gradlew createReleaseTag        # signed build + annotated tag v1.0.0
```

## License

This project is released under the **[MIT License](LICENSE)** — free for any use (personal, commercial, modification, distribution). The only obligation is **attribution**: keep the copyright notice and reference the [project origin](https://github.com/AlexandreZanata/TABELA-ANP-COMBUSTIVEIS).

Full details, examples, and FAQ: **[docs/license.md](docs/license.md)**.

## Data attribution

Fuel price data is published by [ANP](https://www.gov.br/anp) under open government data policies.
