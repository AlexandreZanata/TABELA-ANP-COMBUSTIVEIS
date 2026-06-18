package com.anpfuel.application.usecase.location

import com.anpfuel.application.error.AppError
import com.anpfuel.domain.exception.DomainException
import com.anpfuel.domain.model.MunicipalitySearchResult
import com.anpfuel.domain.repository.AveragePriceRepository
import com.anpfuel.domain.repository.MunicipalityCatalogRepository
import com.anpfuel.domain.repository.MunicipalitySearchRepository
import com.anpfuel.domain.repository.PriceTableRepository
import com.anpfuel.domain.rule.MinimumSearchLengthRule
import com.anpfuel.domain.rule.SearchRequiresImportedDataRule
import com.anpfuel.domain.valueobject.DataAvailability

/**
 * UC-004 — FTS municipality search. Debounce (300 ms) is handled in the ViewModel; this use case is pure.
 */
sealed class SearchMunicipalityOutcome {
    data object QueryTooShort : SearchMunicipalityOutcome()

    data class Success(
        val results: List<MunicipalitySearchResult>,
    ) : SearchMunicipalityOutcome()

    data class NoResults(
        val error: AppError = AppError.SearchNoResults,
    ) : SearchMunicipalityOutcome()
}

class SearchMunicipalityUseCase(
    private val municipalitySearchRepository: MunicipalitySearchRepository,
    private val municipalityCatalogRepository: MunicipalityCatalogRepository,
    private val averagePriceRepository: AveragePriceRepository,
    private val priceTableRepository: PriceTableRepository,
) {

    suspend fun search(
        query: String,
        limit: Int = MunicipalitySearchRepository.DEFAULT_LIMIT,
    ): SearchMunicipalityOutcome {
        SearchRequiresImportedDataRule.validate(
            priceTableRepository.countImportedSurveyWeeks(),
        )

        if (!MinimumSearchLengthRule.isSearchAllowed(query)) {
            return SearchMunicipalityOutcome.QueryTooShort
        }

        val surveyWeek = averagePriceRepository.getLatestImportedSurveyWeek()
            ?: throw DomainException("BR-006: No successfully imported SurveyWeek is available")

        val results = municipalitySearchRepository.search(
            query = query.trim(),
            limit = limit,
        ).map { result ->
            result.copy(
                dataAvailability = municipalityCatalogRepository.resolveDataAvailability(
                    state = result.state,
                    municipality = result.municipality,
                    surveyWeek = surveyWeek,
                ),
            )
        }

        if (results.isEmpty()) {
            return SearchMunicipalityOutcome.NoResults()
        }

        return SearchMunicipalityOutcome.Success(results = results)
    }
}
