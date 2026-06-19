package com.anpfuel.app.ui.stations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.anpfuel.app.ui.components.AnpScaffold
import com.anpfuel.app.ui.components.AnpTopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anpfuel.app.R
import com.anpfuel.app.mapper.AppErrorMapper
import com.anpfuel.app.ui.components.AnpAttributionFooter
import com.anpfuel.app.ui.components.EmptyState
import com.anpfuel.app.ui.components.ErrorState
import com.anpfuel.app.ui.components.FuelProductLabel
import com.anpfuel.app.ui.components.LoadingState
import com.anpfuel.app.ui.components.OfflineBanner
import com.anpfuel.app.ui.components.StationPriceRow
import com.anpfuel.app.ui.weekpicker.SurveyWeekChipAction
import com.anpfuel.app.ui.model.StationPriceUiModel
import com.anpfuel.app.ui.theme.AnpFuelTheme
import com.anpfuel.domain.valueobject.BrazilianState
import com.anpfuel.domain.valueobject.FuelProduct

@Composable
fun StationsScreen(
    modifier: Modifier = Modifier,
    viewModel: StationsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val locale = LocalConfiguration.current.locales[0]

    LaunchedEffect(locale) {
        viewModel.load(locale)
    }

    StationsContent(
        uiState = uiState,
        onFuelProductSelected = { fuelProduct -> viewModel.onFuelProductSelected(fuelProduct, locale) },
        onDownloadStationDetail = { viewModel.downloadStationDetail(locale) },
        onRetry = { viewModel.load(locale) },
        onWeekChanged = { viewModel.load(locale) },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun StationsContent(
    uiState: StationsUiState,
    onFuelProductSelected: (FuelProduct) -> Unit,
    onDownloadStationDetail: () -> Unit,
    onRetry: () -> Unit,
    onWeekChanged: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnpScaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AnpTopAppBar(
                title = { Text(text = stringResource(R.string.stations_title)) },
                actions = {
                    SurveyWeekChipAction(onWeekChanged = onWeekChanged)
                },
            )
        },
        bottomBar = { AnpAttributionFooter() },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (uiState.isOffline) {
                OfflineBanner()
            }

            Text(
                text = stringResource(R.string.history_fuel_product_label),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                for (product in FuelProduct.entries) {
                    FilterChip(
                        selected = uiState.selectedFuelProduct == product,
                        onClick = { onFuelProductSelected(product) },
                        label = { FuelProductLabel(product = product) },
                        enabled = !uiState.isDownloading,
                    )
                }
            }

            when {
                uiState.isLoading || uiState.isDownloading -> {
                    LoadingState(
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                uiState.error != null -> {
                    ErrorState(
                        message = stringResource(AppErrorMapper.toStringRes(uiState.error)),
                        modifier = Modifier.fillMaxWidth(),
                        onRetry = onRetry,
                    )
                }

                uiState.errorMessage != null -> {
                    ErrorState(
                        message = uiState.errorMessage,
                        modifier = Modifier.fillMaxWidth(),
                        onRetry = onRetry,
                    )
                }

                uiState.showNoLocation -> {
                    EmptyState(
                        message = stringResource(R.string.home_no_location_message),
                        hint = stringResource(R.string.home_no_location_hint),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                uiState.showDownloadPrompt -> {
                    if (uiState.municipality != null && uiState.state != null) {
                        Text(
                            text = stringResource(
                                R.string.home_location_format,
                                uiState.municipality,
                                uiState.state.abbreviation,
                            ),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                    EmptyState(
                        message = stringResource(R.string.stations_download_prompt),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Button(
                        onClick = onDownloadStationDetail,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isOffline,
                    ) {
                        Text(text = stringResource(R.string.stations_download_action))
                    }
                }

                uiState.showEmpty -> {
                    EmptyState(
                        message = stringResource(R.string.stations_empty),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                uiState.stations.isNotEmpty() -> {
                    if (uiState.municipality != null && uiState.state != null) {
                        Text(
                            text = stringResource(
                                R.string.home_location_format,
                                uiState.municipality,
                                uiState.state.abbreviation,
                            ),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                    Text(
                        text = stringResource(R.string.stations_sort_by_price),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    uiState.stations.forEach { station ->
                        StationPriceRow(station = station)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StationsScreenPreview() {
    AnpFuelTheme {
        StationsContent(
            uiState = StationsUiState(
                isLoading = false,
                municipality = "Curitiba",
                state = BrazilianState.PARANA,
                stations = listOf(
                    StationPriceUiModel(
                        displayName = "Posto Centro",
                        brand = "BR",
                        address = "Rua XV de Novembro, 1000",
                        priceFormatted = "R$ 5,79",
                        collectedAtLabel = "Jun 10, 2026",
                    ),
                ),
            ),
            onFuelProductSelected = {},
            onDownloadStationDetail = {},
            onRetry = {},
            onWeekChanged = {},
        )
    }
}
