package com.anpfuel.application.usecase.location

import com.anpfuel.application.error.AppError
import com.anpfuel.domain.exception.DomainException
import com.anpfuel.domain.model.MunicipalitySearchResult
import com.anpfuel.domain.repository.AveragePriceRepository
import com.anpfuel.domain.repository.MunicipalityCatalogRepository
import com.anpfuel.domain.repository.MunicipalitySearchRepository
import com.anpfuel.domain.repository.PriceTableRepository
import com.anpfuel.domain.valueobject.BrazilianState
import com.anpfuel.domain.valueobject.DataAvailability
import com.anpfuel.domain.valueobject.SurveyWeek
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SearchMunicipalityUseCaseTest {

    private val municipalitySearchRepository = mockk<MunicipalitySearchRepository>()
    private val municipalityCatalogRepository = mockk<MunicipalityCatalogRepository>()
    private val averagePriceRepository = mockk<AveragePriceRepository>()
    private val priceTableRepository = mockk<PriceTableRepository>()

    private lateinit var useCase: SearchMunicipalityUseCase

    private val surveyWeek = SurveyWeek.fromIsoDates("2026-06-07", "2026-06-13")

    @BeforeEach
    fun setUp() {
        useCase = SearchMunicipalityUseCase(
            municipalitySearchRepository = municipalitySearchRepository,
            municipalityCatalogRepository = municipalityCatalogRepository,
            averagePriceRepository = averagePriceRepository,
            priceTableRepository = priceTableRepository,
        )
        coEvery { priceTableRepository.countImportedSurveyWeeks() } returns 1
        coEvery { averagePriceRepository.getLatestImportedSurveyWeek() } returns surveyWeek
    }

    @Test
    fun br007ShortQueryReturnsWithoutCallingFts() = runTest {
        val outcome = useCase.search("a")

        assertEquals(SearchMunicipalityOutcome.QueryTooShort, outcome)
        coVerify(exactly = 0) { municipalitySearchRepository.search(any(), any()) }
    }

    @Test
    fun br007SingleCharacterAfterTrimReturnsWithoutCallingFts() = runTest {
        val outcome = useCase.search("  s  ")

        assertEquals(SearchMunicipalityOutcome.QueryTooShort, outcome)
        coVerify(exactly = 0) { municipalitySearchRepository.search(any(), any()) }
    }

    @Test
    fun annotatesResultsWithDataAvailability() = runTest {
        val matches = listOf(
            MunicipalitySearchResult(
                municipality = "SÃO PAULO",
                state = BrazilianState.SAO_PAULO,
                dataAvailability = DataAvailability.HAS_DATA,
            ),
            MunicipalitySearchResult(
                municipality = "ACRELÂNDIA",
                state = BrazilianState.ACRE,
                dataAvailability = DataAvailability.HAS_DATA,
            ),
        )
        coEvery { municipalitySearchRepository.search("sao", 20) } returns matches
        coEvery {
            municipalityCatalogRepository.resolveDataAvailability(
                BrazilianState.SAO_PAULO,
                "SÃO PAULO",
                surveyWeek,
            )
        } returns DataAvailability.HAS_DATA
        coEvery {
            municipalityCatalogRepository.resolveDataAvailability(
                BrazilianState.ACRE,
                "ACRELÂNDIA",
                surveyWeek,
            )
        } returns DataAvailability.NEVER_IN_ANP

        val outcome = useCase.search("  sao ")

        assertInstanceOf(SearchMunicipalityOutcome.Success::class.java, outcome)
        val results = (outcome as SearchMunicipalityOutcome.Success).results
        assertEquals(DataAvailability.HAS_DATA, results[0].dataAvailability)
        assertEquals(DataAvailability.NEVER_IN_ANP, results[1].dataAvailability)
        coVerify(exactly = 1) { municipalitySearchRepository.search("sao", 20) }
    }

    @Test
    fun returnsSearchNoResultsWhenFtsIsEmpty() = runTest {
        coEvery { municipalitySearchRepository.search("xyz", 20) } returns emptyList()

        val outcome = useCase.search("xyz")

        assertInstanceOf(SearchMunicipalityOutcome.NoResults::class.java, outcome)
        assertEquals(
            AppError.SearchNoResults,
            (outcome as SearchMunicipalityOutcome.NoResults).error,
        )
    }

    @Test
    fun rejectsSearchWhenNoImportedData() = runTest {
        coEvery { priceTableRepository.countImportedSurveyWeeks() } returns 0

        assertThrows<DomainException> {
            useCase.search("sao")
        }

        coVerify(exactly = 0) { municipalitySearchRepository.search(any(), any()) }
    }

    @Test
    fun respectsCustomResultLimit() = runTest {
        coEvery { municipalitySearchRepository.search("camp", 5) } returns emptyList()

        useCase.search("camp", limit = 5)

        coVerify(exactly = 1) { municipalitySearchRepository.search("camp", 5) }
    }
}
