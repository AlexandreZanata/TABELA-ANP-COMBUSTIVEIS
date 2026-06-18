package com.anpfuel.data.repository

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.anpfuel.data.local.AnpFuelDatabase
import com.anpfuel.data.local.importing.ImportAuditLogger
import com.anpfuel.data.local.importing.ImportTestAssets
import com.anpfuel.data.local.importing.PriceTableBatchImporter
import com.anpfuel.data.local.fts.MunicipalityFtsIndexer
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StorageStatsRepositoryIntegrationTest {

    private lateinit var database: AnpFuelDatabase
    private lateinit var repository: StorageStatsRepositoryImpl

    @Before
    fun setUp() {
        val context = ImportTestAssets.applicationContext()
        database = Room.inMemoryDatabaseBuilder(context, AnpFuelDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        val auditLogger = ImportAuditLogger(database.importAuditLogDao())
        val ftsIndexer = MunicipalityFtsIndexer(database.municipalityFtsDao())
        val batchImporter = PriceTableBatchImporter(
            database = database,
            surveyWeekDao = database.surveyWeekDao(),
            averagePriceDao = database.averagePriceDao(),
            stationPriceDao = database.stationPriceDao(),
            importAuditLogger = auditLogger,
            ftsIndexer = ftsIndexer,
        )

        runBlocking {
            val context = ImportTestAssets.applicationContext()
            val summaryResult = batchImporter.importWeeklySummary(
                ImportTestAssets.resolveSampleFile(context, ImportTestAssets.SUMMARY_SAMPLE),
            )
            batchImporter.importStationDetail(
                file = ImportTestAssets.resolveSampleFile(context, ImportTestAssets.STATION_SAMPLE),
                surveyWeekId = summaryResult.surveyWeekId,
            )
        }

        repository = StorageStatsRepositoryImpl(
            averagePriceDao = database.averagePriceDao(),
            stationPriceDao = database.stationPriceDao(),
            surveyWeekDao = database.surveyWeekDao(),
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getStorageUsageReflectsImportedSampleCounts() = runBlocking {
        val usage = repository.getStorageUsage()

        assertEquals(2344, usage.summaryRowCount)
        assertEquals(19676, usage.stationRowCount)
        assertEquals(1, usage.importedWeekCount)
    }
}
