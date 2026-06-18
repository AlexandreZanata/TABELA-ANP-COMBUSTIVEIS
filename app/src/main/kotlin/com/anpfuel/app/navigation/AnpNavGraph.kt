package com.anpfuel.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.anpfuel.app.ui.components.LoadingState
import com.anpfuel.app.ui.home.HomeScreen
import com.anpfuel.app.ui.onboarding.OnboardingScreen
import com.anpfuel.app.ui.placeholder.HistoryPlaceholderScreen
import com.anpfuel.app.ui.placeholder.LocationPlaceholderScreen
import com.anpfuel.app.ui.placeholder.PricesPlaceholderScreen
import com.anpfuel.app.ui.placeholder.SearchPlaceholderScreen
import com.anpfuel.app.ui.placeholder.SettingsPlaceholderScreen
import com.anpfuel.app.ui.placeholder.StationsPlaceholderScreen
import com.anpfuel.app.viewmodel.AppStartViewModel

@Composable
fun AnpAppNavHost(
    navController: NavHostController,
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier,
    appStartViewModel: AppStartViewModel = hiltViewModel(),
) {
    val startDestination by appStartViewModel.startDestination.collectAsStateWithLifecycle()

    if (startDestination == null) {
        LoadingState(modifier = modifier)
        return
    }

    AnpNavGraph(
        navController = navController,
        startDestination = startDestination!!,
        darkTheme = darkTheme,
        onToggleTheme = onToggleTheme,
        modifier = modifier,
    )
}

@Composable
fun AnpNavGraph(
    navController: NavHostController,
    startDestination: String,
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                },
                onNavigateToLocation = {
                    navController.navigate(Routes.LOCATION) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                },
            )
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
