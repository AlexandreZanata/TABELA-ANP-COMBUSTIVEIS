# UC-002 — Onboarding and First Launch

| Field | Value |
|-------|-------|
| **ID** | UC-002 |
| **Name** | Onboarding and First Launch |
| **Actors** | End User |
| **Layer** | UI → Application |

## Goal

Introduce the app value proposition and guide the user through the first data sync.

## Preconditions

- App installed, no imported `SurveyWeek` (`DataReadiness.EMPTY`).

## Main flow

1. User opens app for the first time.
2. System shows onboarding screens (max 3):
   - Official ANP data source (BR-009).
   - Weekly updates, offline after sync.
   - Optional: location permission **not** requested in v1.
3. User taps **Get started** / **Sync now**.
4. System triggers UC-001 with `SyncRequested(FIRST_LAUNCH)`.
5. On `SyncJobCompleted` success, system sets onboarding complete flag.
6. System navigates to UC-003 (location selection) or home if `preferredMunicipality` exists.

## Alternative flows

### A1 — User skips sync (offline first open)

- **WHEN** user dismisses sync on onboarding  
- **THEN** remain in `EMPTY` state  
- **AND** home shows empty state with prominent sync CTA

### A2 — Sync fails on first launch

- **WHEN** UC-001 fails  
- **THEN** show error with retry  
- **AND** do not mark onboarding complete until at least one successful summary import

## Business rules

- BR-005, BR-009, BR-011

## Domain events

- `SyncRequested(FIRST_LAUNCH)`

## Postconditions

- `onboarding_completed` preference is true only after first successful summary import.
- User understands data is weekly and from ANP.

## i18n keys

- `onboarding_title_welcome`
- `onboarding_body_anp_source`
- `onboarding_body_offline`
- `onboarding_action_get_started`
