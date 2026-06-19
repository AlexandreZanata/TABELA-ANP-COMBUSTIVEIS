package com.anpfuel.domain.rule

import com.anpfuel.domain.valueobject.SurveyWeek

/**
 * BR-018 — Week selection before sync.
 */
object WeekSelectionBeforeSyncRule {

    fun requiresWeekSelection(activeSurveyWeek: SurveyWeek?): Boolean =
        activeSurveyWeek == null
}
