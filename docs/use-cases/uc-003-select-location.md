# UC-003 — Select Location (State + City)

| Field | Value |
|-------|-------|
| **ID** | UC-003 |
| **Name** | Select Location |
| **Actors** | End User |
| **Layer** | UI → Application → Domain |

## Goal

Let the user choose a Brazilian state and municipality to view fuel prices.

## Preconditions

- At least one `SurveyWeek` imported (BR-005).
- `DataReadiness` is `READY`, `PARTIAL`, or `STALE`.

## Main flow

1. User opens location picker from home or search tab.
2. System displays list of 27 `BrazilianState` values (localized display names via i18n).
3. User selects a state.
4. System loads municipalities with data for latest `SurveyWeek` (BR-006) in that state.
5. User selects a municipality from the list.
6. System validates selection exists in local DB.
7. System persists `preferredState` and `preferredMunicipality` (BR-012).
8. System emits `CitySelected`.
9. System navigates to UC-005 (municipality prices).

## Alternative flows

### A1 — State has no data for selected week

- **WHEN** municipality list is empty for state  
- **THEN** show empty state (BR-010) with message to sync or pick another state

### A2 — User changes location from home

- **WHEN** user already has a preferred city  
- **THEN** picker pre-selects current state/municipality

## Business rules

- BR-005, BR-006, BR-010, BR-012

## Domain events

- `CitySelected(municipality, state, surveyWeekId)`

## Postconditions

- Home screen reflects selected municipality.
- Preference survives app restart.

## i18n keys

- `location_picker_title`
- `location_state_label`
- `location_municipality_label`
- `location_empty_state`
