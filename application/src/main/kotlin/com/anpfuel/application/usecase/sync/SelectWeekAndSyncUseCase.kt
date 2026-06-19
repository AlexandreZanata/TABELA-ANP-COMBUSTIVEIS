package com.anpfuel.application.usecase.sync

import com.anpfuel.application.error.AppError
import com.anpfuel.domain.event.SyncJobOutcome
import com.anpfuel.domain.event.SyncRequestSource
import com.anpfuel.domain.valueobject.SurveyWeek
import com.anpfuel.domain.valueobject.SurveyWeekSelectionMode

sealed class SelectWeekAndSyncResult {
    data class Success(
        val syncResult: SyncPriceTablesResult,
    ) : SelectWeekAndSyncResult()

    data class Failed(
        val error: AppError,
    ) : SelectWeekAndSyncResult()
}

/**
 * UC-009 — Persist survey week choice and sync only the selected week (returning users).
 */
class SelectWeekAndSyncUseCase(
    private val selectSurveyWeekUseCase: SelectSurveyWeekUseCase,
    private val syncPriceTablesUseCase: SyncPriceTablesUseCase,
) {

    suspend operator fun invoke(
        surveyWeek: SurveyWeek,
        selectionMode: SurveyWeekSelectionMode,
        source: SyncRequestSource = SyncRequestSource.MANUAL,
    ): SelectWeekAndSyncResult {
        selectSurveyWeekUseCase(
            surveyWeek = surveyWeek,
            selectionMode = selectionMode,
        )

        val syncResult = syncPriceTablesUseCase(
            source = source,
            targetSurveyWeek = surveyWeek,
        )

        if (syncResult.outcome == SyncJobOutcome.FAILED) {
            return SelectWeekAndSyncResult.Failed(
                error = syncResult.error ?: AppError.SyncNetworkError,
            )
        }

        return SelectWeekAndSyncResult.Success(syncResult = syncResult)
    }
}
