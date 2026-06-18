package com.anpfuel.app.startup

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.anpfuel.app.MainActivity
import com.anpfuel.data.local.preferences.UserPreferencesDataStore
import com.anpfuel.domain.model.UserPreferences
import com.anpfuel.domain.valueobject.BrazilianState
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
                UserPreferencesDataStore(context).write(
                    UserPreferences(
                        onboardingCompleted = true,
                        preferredMunicipality = "Curitiba",
                        preferredState = BrazilianState.PARANA,
                    ),
                )
            }
        }
    }
}
