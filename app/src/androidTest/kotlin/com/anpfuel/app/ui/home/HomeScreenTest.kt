package com.anpfuel.app.ui.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.anpfuel.app.ui.model.AveragePriceUiModel
import com.anpfuel.app.ui.theme.AnpFuelTheme
import com.anpfuel.domain.state.DataReadinessState
import com.anpfuel.domain.valueobject.BrazilianState
import com.anpfuel.domain.valueobject.FuelProduct
import com.anpfuel.domain.valueobject.SurveyWeek
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsMunicipalityPricesWhenDataIsAvailable() {
        composeTestRule.setContent {
            AnpFuelTheme {
                HomeContent(
                    uiState = HomeUiState(
                        isLoading = false,
                        readiness = DataReadinessState.READY,
                        hasCachedData = true,
                        hasLocation = true,
                        municipality = "Curitiba",
                        state = BrazilianState.PARANA,
                        surveyWeek = SurveyWeek.fromIsoDates("2026-06-07", "2026-06-13"),
                        prices = listOf(
                            AveragePriceUiModel(
                                fuelProduct = FuelProduct.ETHANOL,
                                averageFormatted = "R$ 3,42",
                                minimumFormatted = "R$ 3,10",
                                maximumFormatted = "R$ 3,80",
                                stationCount = 42,
                            ),
                        ),
                    ),
                    darkTheme = false,
                    onToggleTheme = {},
                    onNavigate = {},
                    onRefresh = {},
                    onRetry = {},
                    onWeekChanged = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Curitiba, PR").assertIsDisplayed()
        composeTestRule.onNodeWithText("R$ 3,42", substring = true).assertIsDisplayed()
    }

    @Test
    fun showsEmptyStateWhenNoCachedData() {
        composeTestRule.setContent {
            AnpFuelTheme {
                HomeContent(
                    uiState = HomeUiState(
                        isLoading = false,
                        readiness = DataReadinessState.EMPTY,
                        hasCachedData = false,
                    ),
                    darkTheme = false,
                    onToggleTheme = {},
                    onNavigate = {},
                    onRefresh = {},
                    onRetry = {},
                    onWeekChanged = {},
                )
            }
        }

        composeTestRule.onNodeWithText("No fuel price data synced yet.").assertIsDisplayed()
    }
}
