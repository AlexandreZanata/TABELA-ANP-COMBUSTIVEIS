package com.anpfuel.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.anpfuel.app.ui.home.HomeScreen
import com.anpfuel.app.ui.placeholder.HistoryPlaceholderScreen
import com.anpfuel.app.ui.placeholder.LocationPlaceholderScreen
import com.anpfuel.app.ui.placeholder.OnboardingPlaceholderScreen
import com.anpfuel.app.ui.placeholder.PricesPlaceholderScreen
import com.anpfuel.app.ui.placeholder.SearchPlaceholderScreen
import com.anpfuel.app.ui.placeholder.SettingsPlaceholderScreen
import com.anpfuel.app.ui.placeholder.StationsPlaceholderScreen

@Composable
fun AnpNavGraph(
    navController: NavHostController,
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier,
    ) {
        composable(Routes.ONBOARDING) {
            OnboardingPlaceholderScreen()
        }
        composable(Routes.HOME) {
            HomeScreen(
                darkTheme = darkTheme,
                onToggleTheme = onToggleTheme,
                onNavigate = navController::navigate,
            )
        }
        composable(Routes.SEARCH) {
            SearchPlaceholderScreen()
        }
        composable(Routes.LOCATION) {
            LocationPlaceholderScreen()
        }
        composable(Routes.PRICES) {
            PricesPlaceholderScreen()
        }
        composable(Routes.HISTORY) {
            HistoryPlaceholderScreen()
        }
        composable(Routes.STATIONS) {
            StationsPlaceholderScreen()
        }
        composable(Routes.SETTINGS) {
            SettingsPlaceholderScreen()
        }
    }
}
