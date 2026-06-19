package com.anpfuel.application.usecase.onboarding

import com.anpfuel.application.error.AppError
import com.anpfuel.application.usecase.sync.SelectSurveyWeekUseCase
import com.anpfuel.application.usecase.sync.SyncPriceTablesResult
import com.anpfuel.application.usecase.sync.SyncPriceTablesUseCase
import com.anpfuel.domain.event.SyncJobOutcome
import com.anpfuel.domain.event.SyncRequestSource
import com.anpfuel.domain.valueobject.SurveyWeek
import com.anpfuel.domain.valueobject.SurveyWeekSelectionMode

sealed class OnboardingSelectWeekAndSyncResult {
    data class SyncFailed(
        val error: AppError,
    ) : OnboardingSelectWeekAndSyncResult()

    data class Completed(
        val syncResult: SyncPriceTablesResult,
        val onboardingResult: CompleteOnboardingResult,
    ) : OnboardingSelectWeekAndSyncResult()
}

/**
 * UC-002 (v2) — After week selection in onboarding, persist choice and sync that week only.
 */
class OnboardingSelectWeekAndSyncUseCase(
    private val selectSurveyWeekUseCase: SelectSurveyWeekUseCase,
    private val syncPriceTablesUseCase: SyncPriceTablesUseCase,
    private val completeOnboardingUseCase: CompleteOnboardingUseCase,
) {

    suspend operator fun invoke(
        surveyWeek: SurveyWeek,
        selectionMode: SurveyWeekSelectionMode,
        source: SyncRequestSource = SyncRequestSource.FIRST_LAUNCH,
    ): OnboardingSelectWeekAndSyncResult {
        selectSurveyWeekUseCase(
            surveyWeek = surveyWeek,
            selectionMode = selectionMode,
        )

        val syncResult = syncPriceTablesUseCase(
            source = source,
            targetSurveyWeek = surveyWeek,
        )

        if (syncResult.outcome == SyncJobOutcome.FAILED) {
            return OnboardingSelectWeekAndSyncResult.SyncFailed(
                error = syncResult.error ?: AppError.SyncNetworkError,
            )
        }

        val onboardingResult = completeOnboardingUseCase.completeAfterSync(syncResult)
        return OnboardingSelectWeekAndSyncResult.Completed(
            syncResult = syncResult,
            onboardingResult = onboardingResult,
        )
    }
}
