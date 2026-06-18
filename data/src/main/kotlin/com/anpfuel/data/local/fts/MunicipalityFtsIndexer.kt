package com.anpfuel.data.local.fts

import com.anpfuel.data.local.dao.MunicipalityFtsDao

/**
 * Keeps the external-content FTS index aligned after bulk average_price imports.
 */
class MunicipalityFtsIndexer(
    private val municipalityFtsDao: MunicipalityFtsDao,
) {

    suspend fun syncAfterBatchInsert() {
        municipalityFtsDao.rebuildIndex()
    }
}
