package com.anpfuel.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.anpfuel.data.local.catalog.MunicipalityCatalogSeeder
import com.anpfuel.data.local.dao.MunicipalityFtsDao
import com.anpfuel.data.local.entity.MunicipalityCatalogEntity
import com.anpfuel.data.local.fts.MunicipalityFtsIndexer
import com.anpfuel.data.local.fts.MunicipalityFtsMatchExpression
import com.anpfuel.data.local.importing.ImportTestCatalogSupport
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MunicipalityFtsSearchTest {

    private lateinit var context: Context
    private lateinit var database: AnpFuelDatabase
    private lateinit var municipalityFtsDao: MunicipalityFtsDao
    private lateinit var catalogSeeder: MunicipalityCatalogSeeder
    private lateinit var ftsIndexer: MunicipalityFtsIndexer

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, AnpFuelDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        municipalityFtsDao = database.municipalityFtsDao()
        catalogSeeder = ImportTestCatalogSupport.createCatalogSeeder(context, database)
        ftsIndexer = MunicipalityFtsIndexer(municipalityFtsDao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun saoPauloQueryReturnsSaoPauloCity() = runBlocking {
        seedCatalogMunicipalities()
        ftsIndexer.syncAfterCatalogChange()

        val matchQuery = MunicipalityFtsMatchExpression.fromUserQuery("SAO PAULO")
        val results = municipalityFtsDao.search(matchQuery, limit = 10)

        assertTrue(results.any { it.municipality == "SÃO PAULO" && it.state == "SP" })
    }

    @Test
    fun campPrefixReturnsCampinasAndCampoGrande() = runBlocking {
        seedCatalogMunicipalities()
        ftsIndexer.syncAfterCatalogChange()

        val matchQuery = MunicipalityFtsMatchExpression.fromUserQuery("CAMP")
        val results = municipalityFtsDao.search(matchQuery, limit = 20)

        assertTrue(results.any { it.municipality == "CAMPINAS" && it.state == "SP" })
        assertTrue(results.any { it.municipality == "CAMPO GRANDE" && it.state == "MS" })
    }

    @Test
    fun saoQueryMatchesMunicipalityWithDiacritics() = runBlocking {
        seedCatalogMunicipalities()
        ftsIndexer.syncAfterCatalogChange()

        val matchQuery = MunicipalityFtsMatchExpression.fromUserQuery("SAO")
        val results = municipalityFtsDao.search(matchQuery, limit = 20)

        assertTrue(results.any { it.municipality == "SÃO PAULO" && it.state == "SP" })
    }

    @Test
    fun rebuildSyncsFtsAfterCatalogInsert() = runBlocking {
        database.municipalityCatalogDao().insertAll(
            listOf(
                catalogEntity(
                    id = "3550308",
                    state = "SP",
                    municipality = "SÃO PAULO",
                    normalizedName = "SAO PAULO",
                ),
            ),
        )

        ftsIndexer.syncAfterCatalogChange()

        val results = municipalityFtsDao.search(
            MunicipalityFtsMatchExpression.fromUserQuery("SAO PAULO"),
            limit = 10,
        )

        assertEquals(1, results.size)
        assertEquals("SÃO PAULO", results.single().municipality)
    }

    private suspend fun seedCatalogMunicipalities() {
        database.municipalityCatalogDao().insertAll(
            listOf(
                catalogEntity("3550308", "SP", "SÃO PAULO", "SAO PAULO"),
                catalogEntity("3509502", "SP", "CAMPINAS", "CAMPINAS"),
                catalogEntity("5002704", "MS", "CAMPO GRANDE", "CAMPO GRANDE"),
                catalogEntity("3549904", "SP", "SÃO JOSÉ DOS CAMPOS", "SAO JOSE DOS CAMPOS"),
            ),
        )
    }

    private fun catalogEntity(
        id: String,
        state: String,
        municipality: String,
        normalizedName: String,
    ): MunicipalityCatalogEntity =
        MunicipalityCatalogEntity(
            id = id,
            ibgeCode = id,
            state = state,
            municipality = municipality,
            normalizedName = normalizedName,
            anpAlias = null,
        )
}
