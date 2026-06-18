# Domain Glossary

> Ubiquitous language for the ANP Fuel Prices project.
> Code identifiers **must** match these terms exactly.

## Business Terms

| Term | Definition |
|------|------------|
| **PriceSurvey** | A weekly ANP fuel price collection for a fixed date range (Sunday–Saturday). |
| **SurveyWeek** | The date range of a PriceSurvey (start date + end date). |
| **FuelProduct** | A fuel type tracked by ANP (enum). |
| **GeographicScope** | Aggregation level: `NATIONAL`, `REGION`, `STATE`, `MUNICIPALITY`, `STATION`. |
| **AveragePrice** | Statistical summary (mean, min, max, std dev) for a FuelProduct at a GeographicScope. |
| **RetailStation** | A licensed fuel retail point identified by CNPJ. |
| **StationPrice** | The price charged by a RetailStation for a FuelProduct on a collection date. |
| **PriceTable** | The downloadable XLSX file published by ANP for a SurveyWeek. |
| **PriceTableType** | Either `WEEKLY_SUMMARY` or `STATION_DETAIL`. |
| **SyncJob** | A background task that discovers, downloads, and imports new PriceTables. |

## FuelProduct Enum

| Value | ANP source label (Portuguese) | Unit |
|-------|-------------------------------|------|
| `ETHANOL` | ETANOL HIDRATADO / ETANOL | R$/liter |
| `GASOLINE_REGULAR` | GASOLINA COMUM | R$/liter |
| `GASOLINE_PREMIUM` | GASOLINA ADITIVADA | R$/liter |
| `DIESEL_S500` | OLEO DIESEL / DIESEL S500 | R$/liter |
| `DIESEL_S10` | OLEO DIESEL S10 / DIESEL S10 | R$/liter |
| `CNG` | GNV | R$/m³ |
| `LPG_P13` | GLP | R$/13 kg |

## BrazilianRegion Enum

`NORTH`, `NORTHEAST`, `CENTRAL_WEST`, `SOUTHEAST`, `SOUTH`

## BrazilianState

27 federative units. Stored as enum with `name`, `abbreviation` (e.g. `SAO_PAULO`, `SP`).

## Domain Events

| Event | Fired when |
|-------|------------|
| `PriceTableDiscovered` | A new weekly file URL is found on the ANP page |
| `PriceTableDownloaded` | A PriceTable file is saved locally |
| `PriceTableImported` | Rows are parsed and persisted to local DB |
| `PriceTableImportFailed` | Parsing or validation failed |
| `SyncJobCompleted` | A SyncJob finishes (success or partial) |
| `CitySelected` | User selects a municipality for price lookup |
| `FuelProductSelected` | User selects a fuel to view detail or stations |
| `SyncRequested` | User or system triggers a sync (`MANUAL`, `SCHEDULED`, `FIRST_LAUNCH`) |
| `StationDetailRequested` | User requests on-demand station-level download |
| `PreferencesUpdated` | User changes a local preference |
| `CacheCleared` | User clears local data (`ALL` or `STATION_DETAIL_ONLY`) |

## Business Rules

### BR-001 — Valid Survey Week
**GIVEN** a PriceTable file name  
**WHEN** parsed for date range  
**THEN** start date must be before or equal to end date  
**AND** range must be ≤ 7 days

### BR-002 — Fuel Product Normalization
**GIVEN** a raw product label from ANP (Portuguese)  
**WHEN** imported  
**THEN** it must map to exactly one `FuelProduct` enum value  
**AND** unmapped labels must be logged and skipped (not crash)

### BR-003 — Immutable Price History
**GIVEN** an imported StationPrice or AveragePrice  
**WHEN** the same ANP file is re-imported with corrections  
**THEN** a new record is created referencing the SurveyWeek  
**AND** the previous record is never deleted or overwritten

### BR-004 — Offline Read
**GIVEN** no network connectivity  
**WHEN** user requests prices for a previously synced city  
**THEN** cached data must be returned  
**AND** UI must indicate stale/sync status

### BR-005 — Search Requires Imported Data
**GIVEN** the local database has zero imported `SurveyWeek` records  
**WHEN** user attempts search or location browse  
**THEN** the app must redirect to sync/onboarding  
**AND** must not execute an empty FTS query

### BR-006 — Default Survey Week
**GIVEN** multiple `SurveyWeek` records exist  
**WHEN** the app needs a default week for display  
**THEN** it must select the most recent successfully imported week by `endDate`  
**AND** never a failed or partial-import week unless explicitly chosen by the user

### BR-007 — Minimum Search Length
**GIVEN** the municipality search field  
**WHEN** the query has fewer than 2 characters  
**THEN** no FTS query is executed  
**AND** a hint is shown to the user

### BR-008 — Station Detail Opt-in
**GIVEN** the user has not enabled `syncStationDetail` and no local `StationPrice` exists  
**WHEN** user opens station list  
**THEN** the app must offer on-demand download  
**AND** must not auto-download station files in background sync

### BR-009 — ANP Data Attribution
**GIVEN** any screen displaying price data  
**WHEN** rendered to the user  
**THEN** ANP source attribution must be visible  
**AND** link to official ANP page must be available in settings

### BR-010 — Empty Municipality Is Not an Error
**GIVEN** a valid municipality with no rows for the selected `SurveyWeek`  
**WHEN** prices are requested  
**THEN** show empty state UI  
**AND** do not throw or show generic error

### BR-011 — Sync Failure Preserves Cache
**GIVEN** a sync job fails at any stage  
**WHEN** the failure is handled  
**THEN** existing imported data must remain intact  
**AND** no rollback of previously imported weeks

### BR-012 — Preferred Location Persistence
**GIVEN** user selects a municipality  
**WHEN** selection is confirmed  
**THEN** persist `preferredState` and `preferredMunicipality` locally  
**AND** restore on next app launch as default home location

### BR-013 — Station Detail Retention Window
**GIVEN** `stationDetailRetentionWeeks` is set to N  
**WHEN** retention cleanup runs after import  
**THEN** delete `StationPrice` rows older than the N most recent weeks  
**AND** never delete `AveragePrice` summary rows in the same operation

### BR-014 — Wi-Fi Only Background Sync
**GIVEN** `autoSyncOnWifi` is true  
**WHEN** WorkManager scheduled sync runs  
**THEN** require unmetered network constraint  
**AND** manual sync may proceed on metered with optional user confirmation

### BR-015 — Single Active Sync Job
**GIVEN** a `SyncJob` is in a non-terminal state  
**WHEN** another sync is requested  
**THEN** reject or queue the second request  
**AND** never run two imports concurrently

## Acronyms

| Acronym | Meaning |
|---------|---------|
| ANP | Agência Nacional do Petróleo, Gás Natural e Biocombustíveis |
| LPC | Levantamento de Preços de Combustíveis (Fuel Price Survey) |
| CNPJ | Cadastro Nacional da Pessoa Jurídica (Brazilian company tax ID) |
| CNG | Compressed Natural Gas (GNV in Brazil) |
| LPG | Liquefied Petroleum Gas (GLP in Brazil) |
