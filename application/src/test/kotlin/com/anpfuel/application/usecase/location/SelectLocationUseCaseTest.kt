package com.anpfuel.application.usecase.location

import com.anpfuel.domain.event.CitySelected
import com.anpfuel.domain.exception.DomainException
import com.anpfuel.domain.model.UserPreferences
import com.anpfuel.domain.repository.AveragePriceRepository
import com.anpfuel.domain.repository.DomainEventPublisher
import com.anpfuel.domain.repository.PriceTableRepository
import com.anpfuel.domain.repository.UserPreferencesRepository
import com.anpfuel.domain.valueobject.BrazilianState
import com.anpfuel.domain.valueobject.DomainId
import com.anpfuel.domain.valueobject.SurveyWeek
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SelectLocationUseCaseTest {

    private val averagePriceRepository = mockk<AveragePriceRepository>()
    private val priceTableRepository = mockk<PriceTableRepository>()
    private val userPreferencesRepository = mockk<UserPreferencesRepository>()
    private val eventPublisher = mockk<DomainEventPublisher>()

    private lateinit var useCase: SelectLocationUseCase

    private val surveyWeek = SurveyWeek.fromIsoDates("2026-06-07", "2026-06-13")
    private val state = BrazilianState.SAO_PAULO
    private val municipality = "São Paulo"

    @BeforeEach
    fun setUp() {
        useCase = SelectLocationUseCase(
            averagePriceRepository = averagePriceRepository,
            priceTableRepository = priceTableRepository,
            userPreferencesRepository = userPreferencesRepository,
            eventPublisher = eventPublisher,
        )

        coEvery { priceTableRepository.countImportedSurveyWeeks() } returns 1
        coEvery { averagePriceRepository.getLatestImportedSurveyWeek() } returns surveyWeek
        coEvery { eventPublisher.publish(any()) } returns Unit
    }

    @Test
    fun listsStatesWithDataForLatestSurveyWeek() = runTest {
        coEvery { averagePriceRepository.getStatesWithData(surveyWeek) } returns listOf(
            BrazilianState.SAO_PAULO,
            BrazilianState.RIO_DE_JANEIRO,
        )

        val result = useCase.getStatesWithData()

        assertEquals(surveyWeek, result.surveyWeek)
        assertEquals(2, result.states.size)
        coVerify(exactly = 1) { averagePriceRepository.getLatestImportedSurveyWeek() }
    }

    @Test
    fun listsMunicipalitiesByStateForLatestSurveyWeek() = runTest {
        coEvery {
            averagePriceRepository.getMunicipalitiesWithData(state, surveyWeek)
        } returns listOf("São Paulo", "Campinas")

        val result = useCase.getMunicipalities(state)

        assertEquals(surveyWeek, result.surveyWeek)
        assertEquals(state, result.state)
        assertEquals(listOf("São Paulo", "Campinas"), result.municipalities)
    }

    @Test
    fun emptyMunicipalityListIsNotAnError() = runTest {
        coEvery {
            averagePriceRepository.getMunicipalitiesWithData(state, surveyWeek)
        } returns emptyList()

        val result = useCase.getMunicipalities(state)

        assertTrue(result.isEmpty)
        assertTrue(result.municipalities.isEmpty())
    }

    @Test
    fun persistsPreferredLocationAndEmitsCitySelected() = runTest {
        val savedPreferences = slot<UserPreferences>()
        coEvery { userPreferencesRepository.getPreferences() } returns UserPreferences()
        coEvery { userPreferencesRepository.savePreferences(capture(savedPreferences)) } returns Unit
        coEvery {
            averagePriceRepository.getMunicipalitiesWithData(state, surveyWeek)
        } returns listOf(municipality)

        val result = useCase.selectLocation(state, municipality)

        assertEquals(state, savedPreferences.captured.preferredState)
        assertEquals(municipality, savedPreferences.captured.preferredMunicipality)
        assertEquals(municipality, result.event.payload.municipality)
        assertEquals(state, result.event.payload.state)
        assertEquals(DomainId.forSurveyWeek(surveyWeek), result.event.payload.surveyWeekId)
        coVerify(exactly = 1) { eventPublisher.publish(any<CitySelected>()) }
    }

    @Test
    fun rejectsSelectionWhenNoImportedData() = runTest {
        coEvery { priceTableRepository.countImportedSurveyWeeks() } returns 0

        assertThrows<DomainException> {
            useCase.getStatesWithData()
        }
    }

    @Test
    fun rejectsUnknownMunicipalitySelection() = runTest {
        coEvery { userPreferencesRepository.getPreferences() } returns UserPreferences()
        coEvery {
            averagePriceRepository.getMunicipalitiesWithData(state, surveyWeek)
        } returns listOf("Campinas")

        assertThrows<DomainException> {
            useCase.selectLocation(state, municipality)
        }

        coVerify(exactly = 0) { userPreferencesRepository.savePreferences(any()) }
        coVerify(exactly = 0) { eventPublisher.publish(any()) }
    }

    @Test
    fun returnsPreferredLocationForExistingSelection() = runTest {
        coEvery { userPreferencesRepository.getPreferences() } returns UserPreferences(
            preferredState = state,
            preferredMunicipality = municipality,
        )

        val preferred = useCase.getPreferredLocation()

        assertEquals(state, preferred?.state)
        assertEquals(municipality, preferred?.municipality)
    }

    @Test
    fun returnsNullPreferredLocationWhenIncomplete() = runTest {
        coEvery { userPreferencesRepository.getPreferences() } returns UserPreferences(
            preferredState = state,
            preferredMunicipality = null,
        )

        assertNull(useCase.getPreferredLocation())
    }
}
