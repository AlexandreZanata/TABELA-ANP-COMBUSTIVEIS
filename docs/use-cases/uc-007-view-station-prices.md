# UC-007 — View Station Prices

| Field | Value |
|-------|-------|
| **ID** | UC-007 |
| **Name** | View Station Prices |
| **Actors** | End User |
| **Layer** | UI → Application → Infrastructure |

## Goal

List individual `RetailStation` prices for a selected municipality, fuel, and week — sorted by price ascending.

## Preconditions

- Municipality and `FuelProduct` selected.
- `StationPrice` data available locally for `SurveyWeek` **OR** user accepts on-demand download (BR-008).

## Main flow

1. User opens station list from UC-005.
2. System checks local `StationPrice` for scope.
3. **IF** data exists → load and display list.
4. **IF** data missing → show prompt to download station detail for this week.
5. User confirms download → emit `StationDetailRequested` → run UC-001 subset (station file only).
6. System loads stations sorted by `price` ascending.
7. UI shows per row: trade name (or legal name), brand, address, price, collection date.
8. ANP attribution visible (BR-009).

## Alternative flows

### A1 — User declined station sync globally

- **WHEN** `syncStationDetail` is false and no local data  
- **THEN** explain storage/bandwidth tradeoff, offer one-time download

### A2 — Download fails

- **WHEN** station file import fails  
- **THEN** show error, municipality averages still available (UC-005)

### A3 — No stations for fuel in city

- **WHEN** query returns empty (e.g. no GNV in small town)  
- **THEN** empty state (BR-010)

## Business rules

- BR-004, BR-008, BR-009, BR-010, BR-013

## Domain events

- `StationDetailRequested`
- `FuelProductSelected`

## Postconditions

- User can compare station-level prices within municipality.

## i18n keys

- `stations_title`
- `stations_sort_by_price`
- `stations_download_prompt`
- `stations_empty`
- `error_station_detail_missing`
