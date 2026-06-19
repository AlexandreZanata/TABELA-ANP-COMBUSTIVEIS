package com.anpfuel.app.startup

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.room.Room
import com.anpfuel.app.MainActivity
import com.anpfuel.data.local.AnpFuelDatabase
import com.anpfuel.data.local.AnpFuelDatabaseMigrations
import com.anpfuel.data.local.entity.SurveyWeekEntity
import com.anpfuel.data.local.preferences.UserPreferencesDataStore
import com.anpfuel.domain.model.UserPreferences
import com.anpfuel.domain.valueobject.BrazilianState
import com.anpfuel.domain.valueobject.DomainId
import com.anpfuel.domain.valueobject.SurveyWeek
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.system.measureTimeMillis

/**
 * Phase 9.2.3 — Cold start to home must complete within 2 seconds when cache/preferences exist.
 */
@RunWith(AndroidJUnit4::class)
class AppStartupPerformanceTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun reachesHomeScreenWithinTwoSecondsWithCachedPreferences() {
        val elapsedMs = measureTimeMillis {
            composeTestRule.waitUntil(timeoutMillis = STARTUP_LIMIT_MS) {
                try {
                    composeTestRule.onNodeWithText(HOME_TITLE).assertExists()
                    true
                } catch (_: AssertionError) {
                    false
                }
            }
        }

        println("STARTUP_TO_HOME_MS=$elapsedMs")
        assertTrue(
            "Home screen not reached within ${STARTUP_LIMIT_MS}ms (took ${elapsedMs}ms)",
            elapsedMs < STARTUP_LIMIT_MS,
        )
    }

    companion object {
        const val HOME_TITLE = "ANP Fuel Prices"
        const val STARTUP_LIMIT_MS = 2_000L

        @BeforeClass
        @JvmStatic
        fun seedCachedPreferences() {
            runBlocking {
                val context = ApplicationProvider.getApplicationContext<android.content.Context>()
                val surveyWeek = SurveyWeek.fromIsoDates("2026-06-07", "2026-06-13")
                UserPreferencesDataStore(context).write(
                    UserPreferences(
                        onboardingCompleted = true,
                        activeSurveyWeek = surveyWeek,
                        preferredMunicipality = "Curitiba",
                        preferredState = BrazilianState.PARANA,
                    ),
                )
                Room.databaseBuilder(context, AnpFuelDatabase::class.java, "anp_fuel.db")
                    .addMigrations(
                        AnpFuelDatabaseMigrations.MIGRATION_1_2,
                        AnpFuelDatabaseMigrations.MIGRATION_2_3,
                    )
                    .build()
                    .surveyWeekDao()
                    .insert(
                        SurveyWeekEntity(
                            id = DomainId.forSurveyWeek(surveyWeek).value,
                            startDate = surveyWeek.startDate.toString(),
                            endDate = surveyWeek.endDate.toString(),
                            summaryImportedAt = 1_718_284_800_000L,
                            stationImportedAt = null,
                        ),
                    )
            }
        }
    }
}
