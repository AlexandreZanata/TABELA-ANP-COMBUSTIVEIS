package com.anpfuel.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anpfuel.app.R
import com.anpfuel.app.navigation.Routes
import com.anpfuel.app.ui.components.AnpAttributionFooter
import com.anpfuel.app.ui.components.EmptyState
import com.anpfuel.app.ui.components.FuelProductLabel
import com.anpfuel.app.ui.components.OfflineBanner
import com.anpfuel.app.ui.components.SyncStatusBanner
import com.anpfuel.app.ui.theme.AnpFuelTheme
import com.anpfuel.domain.state.DataReadinessState
import com.anpfuel.domain.valueobject.FuelProduct

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.home_title)) },
                actions = {
                    IconButton(onClick = onToggleTheme) {
                        Icon(
                            imageVector = if (darkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = stringResource(
                                if (darkTheme) R.string.action_switch_light_theme else R.string.action_switch_dark_theme,
                            ),
                        )
                    }
                },
            )
        },
        bottomBar = { AnpAttributionFooter() },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SyncStatusBanner(readiness = DataReadinessState.EMPTY)
            OfflineBanner()

            EmptyState(
                message = stringResource(R.string.home_empty_message),
                hint = stringResource(R.string.home_empty_hint),
            )

            Text(
                text = stringResource(R.string.home_fuel_products_preview),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
            for (product in FuelProduct.entries) {
                AssistChip(
                    onClick = {},
                    label = { FuelProductLabel(product = product) },
                )
            }
            }

            Text(
                text = stringResource(R.string.home_navigation_preview),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                NavigationChip(labelRes = R.string.nav_search, route = Routes.SEARCH, onNavigate = onNavigate)
                NavigationChip(labelRes = R.string.nav_location, route = Routes.LOCATION, onNavigate = onNavigate)
                NavigationChip(labelRes = R.string.nav_prices, route = Routes.PRICES, onNavigate = onNavigate)
                NavigationChip(labelRes = R.string.nav_history, route = Routes.HISTORY, onNavigate = onNavigate)
                NavigationChip(labelRes = R.string.nav_stations, route = Routes.STATIONS, onNavigate = onNavigate)
                NavigationChip(labelRes = R.string.nav_settings, route = Routes.SETTINGS, onNavigate = onNavigate)
            }
        }
    }
}

@Composable
private fun NavigationChip(
    labelRes: Int,
    route: String,
    onNavigate: (String) -> Unit,
) {
    AssistChip(
        onClick = { onNavigate(route) },
        label = { Text(text = stringResource(labelRes)) },
    )
}

@Preview(showBackground = true, name = "Home EN")
@Composable
private fun HomeScreenLightPreview() {
    AnpFuelTheme(darkTheme = false) {
        HomeScreen(
            darkTheme = false,
            onToggleTheme = {},
            onNavigate = {},
        )
    }
}

@Preview(showBackground = true, name = "Home PT", locale = "pt-rBR")
@Composable
private fun HomeScreenPtPreview() {
    AnpFuelTheme(darkTheme = false) {
        HomeScreen(
            darkTheme = false,
            onToggleTheme = {},
            onNavigate = {},
        )
    }
}
