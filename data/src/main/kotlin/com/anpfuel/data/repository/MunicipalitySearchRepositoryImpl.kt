package com.anpfuel.data.repository

import com.anpfuel.data.local.catalog.MunicipalityCatalogSeeder
import com.anpfuel.data.local.dao.MunicipalityCatalogDao
import com.anpfuel.data.local.dao.MunicipalityFtsDao
import com.anpfuel.data.local.fts.MunicipalityFtsMatchExpression
import com.anpfuel.data.mapper.MunicipalityCatalogMapper
import com.anpfuel.domain.model.MunicipalitySearchResult
import com.anpfuel.domain.repository.MunicipalitySearchRepository
import com.anpfuel.domain.rule.IntelligentSearchRankingRule
import com.anpfuel.domain.valueobject.BrazilianState
import com.anpfuel.domain.valueobject.MunicipalityCatalogEntry
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MunicipalitySearchRepositoryImpl @Inject constructor(
    private val municipalityFtsDao: MunicipalityFtsDao,
    private val municipalityCatalogDao: MunicipalityCatalogDao,
    private val catalogSeeder: MunicipalityCatalogSeeder,
) : MunicipalitySearchRepository {

    override suspend fun search(query: String, limit: Int): List<MunicipalitySearchResult> {
        catalogSeeder.seedIfEmpty()

        val catalogEntries = municipalityCatalogDao.findAll()
            .mapNotNull(MunicipalityCatalogMapper::toDomain)

        val ftsCandidates = searchFtsCandidates(query, catalogEntries)
        val ranked = IntelligentSearchRankingRule.rank(query, ftsCandidates)

        return ranked.take(limit).map { candidate ->
            MunicipalitySearchResult(
                municipality = candidate.entry.municipality,
                state = candidate.entry.state,
            )
        }
    }

    private suspend fun searchFtsCandidates(
        query: String,
        catalogEntries: List<MunicipalityCatalogEntry>,
    ): List<MunicipalityCatalogEntry> {
        val matchQuery = MunicipalityFtsMatchExpression.fromUserQuery(query)
        if (matchQuery.isBlank()) {
            return emptyList()
        }

        val ftsRows = municipalityFtsDao.search(matchQuery, limit = FTS_PREFILTER_LIMIT)
        if (ftsRows.isEmpty()) {
            return catalogEntries.filter { entry ->
                IntelligentSearchRankingRule.classifyMatch(query, entry.municipality) != null
            }
        }

        val ftsKeys = ftsRows.map { row ->
            row.state to row.municipality
        }.toSet()

        val ftsMatches = catalogEntries.filter { entry ->
            entry.state.abbreviation to entry.municipality in ftsKeys
        }

        val supplementalMatches = catalogEntries.filter { entry ->
            entry !in ftsMatches &&
                IntelligentSearchRankingRule.classifyMatch(query, entry.municipality) != null
        }

        return ftsMatches + supplementalMatches
    }

    companion object {
        private const val FTS_PREFILTER_LIMIT = 200
    }
}
