package com.anpfuel.app.support

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.anpfuel.data.local.AnpFuelDatabase
import com.anpfuel.data.local.AnpFuelDatabaseMigrations
import com.anpfuel.data.local.entity.AveragePriceEntity
import com.anpfuel.data.local.entity.SurveyWeekEntity
import com.anpfuel.data.local.preferences.UserPreferencesDataStore
import com.anpfuel.domain.model.UserPreferences
import com.anpfuel.domain.valueobject.BrazilianState
import com.anpfuel.domain.valueobject.DomainId
import com.anpfuel.domain.valueobject.SurveyWeek
import java.io.File

/**
 * Seeds on-device preferences and Room data for instrumented app flows.
 *
 * Always closes the database instance so Hilt can open the production singleton safely.
 */
object InstrumentedAppDataSeeder {

    private const val DATABASE_NAME = "anp_fuel.db"

    suspend fun seedReturningUserHomeState(context: Context) {
        clearAppStorage(context)

        val surveyWeek = SurveyWeek.fromIsoDates("2026-06-07", "2026-06-13")
        val surveyWeekId = DomainId.forSurveyWeek(surveyWeek).value

        UserPreferencesDataStore(context).write(
            UserPreferences(
                onboardingCompleted = true,
                activeSurveyWeek = surveyWeek,
                preferredMunicipality = "Curitiba",
                preferredState = BrazilianState.PARANA,
            ),
        )

        val database = Room.databaseBuilder(context, AnpFuelDatabase::class.java, DATABASE_NAME)
            .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
            .addMigrations(
                AnpFuelDatabaseMigrations.MIGRATION_1_2,
                AnpFuelDatabaseMigrations.MIGRATION_2_3,
            )
            .allowMainThreadQueries()
            .build()

        try {
            database.surveyWeekDao().insert(
                SurveyWeekEntity(
                    id = surveyWeekId,
                    startDate = surveyWeek.startDate.toString(),
                    endDate = surveyWeek.endDate.toString(),
                    summaryImportedAt = 1_718_284_800_000L,
                    stationImportedAt = null,
                ),
            )
            database.averagePriceDao().insertAll(
                listOf(
                    AveragePriceEntity(
                        id = "avg-curitiba-ethanol",
                        surveyWeekId = surveyWeekId,
                        state = "PR",
                        municipality = "CURITIBA",
                        fuelProduct = "ETHANOL",
                        stationCount = 42,
                        unit = "R$/l",
                        avgPrice = 3.42,
                        minPrice = 3.10,
                        maxPrice = 3.80,
                        stdDev = 0.12,
                    ),
                ),
            )
        } finally {
            database.close()
        }
    }

    fun clearAppStorage(context: Context) {
        context.deleteDatabase(DATABASE_NAME)
        context.filesDir.resolve("datastore/user_preferences.preferences_pb").delete()
        context.getDatabasePath(DATABASE_NAME).parentFile
            ?.listFiles()
            ?.filter { it.name.startsWith(DATABASE_NAME) }
            ?.forEach(File::delete)
    }
}
