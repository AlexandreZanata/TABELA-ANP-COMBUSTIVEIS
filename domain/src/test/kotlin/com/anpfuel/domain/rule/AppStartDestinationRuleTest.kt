package com.anpfuel.domain.rule

import com.anpfuel.domain.model.PriceSurvey
import com.anpfuel.domain.navigation.AppStartDestination
import com.anpfuel.domain.valueobject.SurveyWeek
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant

class AppStartDestinationRuleTest {

    private val activeWeek = SurveyWeek.fromIsoDates("2026-06-07", "2026-06-13")
    private val otherWeek = SurveyWeek.fromIsoDates("2026-05-31", "2026-06-06")

    @Test
    fun routesToOnboardingWhenNotCompleted() {
        val destination = AppStartDestinationRule.resolve(
            onboardingCompleted = false,
            activeSurveyWeek = activeWeek,
            importedSurveys = listOf(importedSurvey(activeWeek)),
        )

        assertEquals(AppStartDestination.ONBOARDING, destination)
    }

    @Test
    fun routesToWeekPickerWhenActiveSurveyWeekMissing() {
        val destination = AppStartDestinationRule.resolve(
            onboardingCompleted = true,
            activeSurveyWeek = null,
            importedSurveys = listOf(importedSurvey(otherWeek)),
        )

        assertEquals(AppStartDestination.WEEK_PICKER, destination)
    }

    @Test
    fun routesToWeekPickerWhenActiveSurveyWeekNotImportedLocally() {
        val destination = AppStartDestinationRule.resolve(
            onboardingCompleted = true,
            activeSurveyWeek = activeWeek,
            importedSurveys = listOf(importedSurvey(otherWeek)),
        )

        assertEquals(AppStartDestination.WEEK_PICKER, destination)
    }

    @Test
    fun routesToHomeWhenActiveSurveyWeekImportedLocally() {
        val destination = AppStartDestinationRule.resolve(
            onboardingCompleted = true,
            activeSurveyWeek = activeWeek,
            importedSurveys = listOf(importedSurvey(activeWeek)),
        )

        assertEquals(AppStartDestination.HOME, destination)
    }

    private fun importedSurvey(surveyWeek: SurveyWeek): PriceSurvey {
        val survey = PriceSurvey.create(surveyWeek = surveyWeek)
        survey.markSummaryImported(Instant.parse("2026-06-13T12:00:00Z"))
        return survey
    }
}
