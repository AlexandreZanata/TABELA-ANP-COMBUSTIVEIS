package com.anpfuel.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.anpfuel.data.local.dao.AveragePriceDao
import com.anpfuel.data.local.dao.MunicipalityFtsDao
import com.anpfuel.data.local.dao.SurveyWeekDao
import com.anpfuel.data.local.entity.AveragePriceEntity
import com.anpfuel.data.local.entity.SurveyWeekEntity
import com.anpfuel.data.local.fts.MunicipalityFtsIndexer
import com.anpfuel.data.local.fts.MunicipalityFtsMatchExpression
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MunicipalityFtsSearchTest {

    private lateinit var database: AnpFuelDatabase
    private lateinit var surveyWeekDao: SurveyWeekDao
    private lateinit var averagePriceDao: AveragePriceDao
    private lateinit var municipalityFtsDao: MunicipalityFtsDao
    private lateinit var ftsIndexer: MunicipalityFtsIndexer

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AnpFuelDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        surveyWeekDao = database.surveyWeekDao()
        averagePriceDao = database.averagePriceDao()
        municipalityFtsDao = database.municipalityFtsDao()
        ftsIndexer = MunicipalityFtsIndexer(municipalityFtsDao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun saoPauloQueryReturnsSaoPauloCity() = runBlocking {
        seedMunicipalities()
        ftsIndexer.syncAfterBatchInsert()

        val matchQuery = MunicipalityFtsMatchExpression.fromUserQuery("SAO PAULO")
        val results = municipalityFtsDao.search(matchQuery, limit = 10)

        assertTrue(results.any { it.municipality == "SÃO PAULO" && it.state == "SP" })
    }

    @Test
    fun campPrefixReturnsCampinasAndCampoGrande() = runBlocking {
        seedMunicipalities()
        ftsIndexer.syncAfterBatchInsert()

        val matchQuery = MunicipalityFtsMatchExpression.fromUserQuery("CAMP")
        val results = municipalityFtsDao.search(matchQuery, limit = 20)

        assertTrue(results.any { it.municipality == "CAMPINAS" && it.state == "SP" })
        assertTrue(results.any { it.municipality == "CAMPO GRANDE" && it.state == "MS" })
    }

    @Test
    fun saoQueryMatchesMunicipalityWithDiacritics() = runBlocking {
        seedMunicipalities()
        ftsIndexer.syncAfterBatchInsert()

        val matchQuery = MunicipalityFtsMatchExpression.fromUserQuery("SAO")
        val results = municipalityFtsDao.search(matchQuery, limit = 20)

        assertTrue(results.any { it.municipality == "SÃO PAULO" && it.state == "SP" })
    }

    @Test
    fun rebuildSyncsFtsAfterBatchInsert() = runBlocking {
        val surveyWeek = surveyWeekEntity()
        surveyWeekDao.insert(surveyWeek)
        averagePriceDao.insertAll(
            listOf(
                averagePriceEntity(
                    id = "avg-sp-gas",
                    surveyWeekId = surveyWeek.id,
                    municipality = "SÃO PAULO",
                    state = "SP",
                    fuelProduct = "GASOLINE",
                ),
                averagePriceEntity(
                    id = "avg-sp-ethanol",
                    surveyWeekId = surveyWeek.id,
                    municipality = "SÃO PAULO",
                    state = "SP",
                    fuelProduct = "HYDRATED_ETHANOL",
                ),
            ),
        )

        ftsIndexer.syncAfterBatchInsert()

        val results = municipalityFtsDao.search(
            MunicipalityFtsMatchExpression.fromUserQuery("SAO PAULO"),
            limit = 10,
        )

        assertEquals(1, results.size)
        assertEquals("SÃO PAULO", results.single().municipality)
    }

    private suspend fun seedMunicipalities() {
        val surveyWeek = surveyWeekEntity()
        surveyWeekDao.insert(surveyWeek)
        averagePriceDao.insertAll(
            listOf(
                averagePriceEntity(
                    id = "avg-sao-paulo-gas",
                    surveyWeekId = surveyWeek.id,
                    municipality = "SÃO PAULO",
                    state = "SP",
                    fuelProduct = "GASOLINE",
                ),
                averagePriceEntity(
                    id = "avg-sao-paulo-ethanol",
                    surveyWeekId = surveyWeek.id,
                    municipality = "SÃO PAULO",
                    state = "SP",
                    fuelProduct = "HYDRATED_ETHANOL",
                ),
                averagePriceEntity(
                    id = "avg-campinas",
                    surveyWeekId = surveyWeek.id,
                    municipality = "CAMPINAS",
                    state = "SP",
                    fuelProduct = "GASOLINE",
                ),
                averagePriceEntity(
                    id = "avg-campo-grande",
                    surveyWeekId = surveyWeek.id,
                    municipality = "CAMPO GRANDE",
                    state = "MS",
                    fuelProduct = "GASOLINE",
                ),
                averagePriceEntity(
                    id = "avg-sao-jose",
                    surveyWeekId = surveyWeek.id,
                    municipality = "SÃO JOSÉ DOS CAMPOS",
                    state = "SP",
                    fuelProduct = "GASOLINE",
                ),
            ),
        )
    }

    private fun surveyWeekEntity(): SurveyWeekEntity =
        SurveyWeekEntity(
            id = "week-2025-01",
            startDate = "2025-01-05",
            endDate = "2025-01-11",
            summaryImportedAt = 1_735_689_600_000L,
            stationImportedAt = null,
        )

    private fun averagePriceEntity(
        id: String,
        surveyWeekId: String,
        municipality: String,
        state: String,
        fuelProduct: String,
    ): AveragePriceEntity =
        AveragePriceEntity(
            id = id,
            surveyWeekId = surveyWeekId,
            state = state,
            municipality = municipality,
            fuelProduct = fuelProduct,
            stationCount = 10,
            unit = "R$/l",
            avgPrice = 5.50,
            minPrice = 5.00,
            maxPrice = 6.00,
            stdDev = 0.10,
        )
}
