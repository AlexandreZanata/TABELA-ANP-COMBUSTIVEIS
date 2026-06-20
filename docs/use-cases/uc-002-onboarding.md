# UC-002 — Onboarding and First Launch

| Field | Value |
|-------|-------|
| **ID** | UC-002 |
| **Name** | Onboarding and First Launch |
| **Actors** | End User |
| **Layer** | UI → Application |

## Goal

Introduce the app value proposition and guide the user through survey week selection and the first data sync.

## Preconditions

- App installed, no imported `SurveyWeek` (`DataReadiness.EMPTY`).

## Main flow

1. User opens app for the first time.
2. System shows onboarding screens (max 3):
   - Official ANP data source (BR-009).
   - Weekly updates, offline after sync.
   - Survey week selection overview (v2).
3. User taps **Get started** on the last intro slide.
4. System runs UC-009 week catalog discovery. If `autoDownloadLatestWeek` is enabled (BR-020, default), auto-selects latest week and syncs; otherwise shows the week picker (BR-018).
5. When week picker is shown: user taps **Use latest week** or selects a specific historical week.
6. System persists `activeSurveyWeek`, emits `SurveyWeekSelected`, and triggers UC-001 scoped to the selected week (`SyncRequested(FIRST_LAUNCH)`).
7. On `SyncJobCompleted` success, system sets onboarding complete flag.
8. System navigates to UC-003 (location selection) or home if `preferredMunicipality` exists.

## Alternative flows

### A1 — User skips sync (offline first open)

- **WHEN** user dismisses sync on onboarding or week picker  
- **THEN** remain in `EMPTY` state  
- **AND** home shows empty state with prominent sync CTA

### A2 — Sync fails on first launch

- **WHEN** UC-001 fails  
- **THEN** show error with retry on week picker  
- **AND** do not mark onboarding complete until at least one successful summary import

### A3 — Week catalog discovery fails

- **WHEN** ANP listing cannot be fetched during onboarding  
- **THEN** show structured error with retry on week picker  
- **AND** user may go back to intro slides or skip sync (A1)

## Business rules

- BR-005, BR-009, BR-011, BR-018, BR-019, BR-020

## Domain events

- `SurveyWeekSelected` (v2, via UC-009)
- `SyncRequested(FIRST_LAUNCH)`
- `PreferencesUpdated` (`activeSurveyWeek`, `onboardingCompleted`)

## Postconditions

- `onboarding_completed` preference is true only after first successful summary import.
- `activeSurveyWeek` reflects user choice when sync was started (BR-019).
- User understands data is weekly and from ANP.

## i18n keys

- `onboarding_title_welcome`
- `onboarding_body_anp_source`
- `onboarding_body_offline`
- `onboarding_action_get_started`
- `week_picker_title`, `week_picker_latest`, `week_picker_updated_at`, `week_picker_operational_note` (UC-009)

## Related documentation

- [uc-009-select-survey-week.md](uc-009-select-survey-week.md) — week picker after intro slides (Phase 12.3)
