package com.anpfuel.application.usecase.location

import com.anpfuel.application.error.AppError
import com.anpfuel.domain.exception.DomainException
import com.anpfuel.domain.model.MunicipalitySearchResult
import com.anpfuel.domain.repository.MunicipalitySearchRepository
import com.anpfuel.domain.repository.PriceTableRepository
import com.anpfuel.domain.valueobject.BrazilianState
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
    private val priceTableRepository = mockk<PriceTableRepository>()

    private lateinit var useCase: SearchMunicipalityUseCase

    @BeforeEach
    fun setUp() {
        useCase = SearchMunicipalityUseCase(
            municipalitySearchRepository = municipalitySearchRepository,
            priceTableRepository = priceTableRepository,
        )
        coEvery { priceTableRepository.countImportedSurveyWeeks() } returns 1
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
    fun searchesFtsWhenQueryHasMinimumLength() = runTest {
        val matches = listOf(
            MunicipalitySearchResult(
                municipality = "São Paulo",
                state = BrazilianState.SAO_PAULO,
            ),
        )
        coEvery { municipalitySearchRepository.search("sao", 20) } returns matches

        val outcome = useCase.search("  sao ")

        assertInstanceOf(SearchMunicipalityOutcome.Success::class.java, outcome)
        assertEquals(matches, (outcome as SearchMunicipalityOutcome.Success).results)
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
