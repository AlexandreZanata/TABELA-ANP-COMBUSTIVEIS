package com.anpfuel.application.usecase.navigation

import com.anpfuel.domain.model.PriceSurvey
import com.anpfuel.domain.model.UserPreferences
import com.anpfuel.domain.navigation.AppStartDestination
import com.anpfuel.domain.repository.PriceTableRepository
import com.anpfuel.domain.repository.UserPreferencesRepository
import com.anpfuel.domain.valueobject.SurveyWeek
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

class ResolveAppStartDestinationUseCaseTest {

    private val userPreferencesRepository = mockk<UserPreferencesRepository>()
    private val priceTableRepository = mockk<PriceTableRepository>()

    private lateinit var useCase: ResolveAppStartDestinationUseCase

    private val activeWeek = SurveyWeek.fromIsoDates("2026-06-07", "2026-06-13")

    @BeforeEach
    fun setUp() {
        useCase = ResolveAppStartDestinationUseCase(
            userPreferencesRepository = userPreferencesRepository,
            priceTableRepository = priceTableRepository,
        )
    }

    @Test
    fun returnsHomeForReturningUserWithImportedActiveWeek() = runTest {
        coEvery { userPreferencesRepository.getPreferences() } returns UserPreferences(
            onboardingCompleted = true,
            activeSurveyWeek = activeWeek,
        )
        coEvery { priceTableRepository.getImportedPriceSurveys() } returns listOf(
            importedSurvey(activeWeek),
        )

        assertEquals(AppStartDestination.HOME, useCase())
    }

    @Test
    fun returnsWeekPickerWhenActiveWeekMissingLocally() = runTest {
        coEvery { userPreferencesRepository.getPreferences() } returns UserPreferences(
            onboardingCompleted = true,
            activeSurveyWeek = activeWeek,
        )
        coEvery { priceTableRepository.getImportedPriceSurveys() } returns emptyList()

        assertEquals(AppStartDestination.WEEK_PICKER, useCase())
    }

    private fun importedSurvey(surveyWeek: SurveyWeek): PriceSurvey {
        val survey = PriceSurvey.create(surveyWeek = surveyWeek)
        survey.markSummaryImported(Instant.parse("2026-06-13T12:00:00Z"))
        return survey
    }
}
