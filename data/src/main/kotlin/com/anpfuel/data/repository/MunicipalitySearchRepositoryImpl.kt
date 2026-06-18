package com.anpfuel.data.repository

import com.anpfuel.data.local.dao.MunicipalityFtsDao
import com.anpfuel.data.local.fts.MunicipalityFtsMatchExpression
import com.anpfuel.domain.model.MunicipalitySearchResult
import com.anpfuel.domain.repository.MunicipalitySearchRepository
import com.anpfuel.domain.valueobject.BrazilianState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MunicipalitySearchRepositoryImpl @Inject constructor(
    private val municipalityFtsDao: MunicipalityFtsDao,
) : MunicipalitySearchRepository {

    override suspend fun search(query: String, limit: Int): List<MunicipalitySearchResult> {
        val matchQuery = MunicipalityFtsMatchExpression.fromUserQuery(query)
        if (matchQuery.isBlank()) {
            return emptyList()
        }

        return municipalityFtsDao.search(matchQuery, limit)
            .mapNotNull { row ->
                val state = BrazilianState.fromAbbreviation(row.state) ?: return@mapNotNull null
                MunicipalitySearchResult(
                    municipality = row.municipality,
                    state = state,
                )
            }
    }
}
