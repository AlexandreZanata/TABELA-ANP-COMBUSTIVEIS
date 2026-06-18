package com.anpfuel.app.ui.prices

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import com.anpfuel.app.navigation.Routes
import com.anpfuel.app.mapper.AppErrorMapper
import com.anpfuel.app.mapper.SurveyWeekFormatter
import com.anpfuel.app.ui.components.AnpAttributionFooter
import com.anpfuel.app.ui.components.EmptyState
import com.anpfuel.app.ui.components.ErrorState
import com.anpfuel.app.ui.components.LoadingState
import com.anpfuel.app.ui.components.MunicipalityPriceDetailRow
import com.anpfuel.app.ui.components.OfflineBanner
import com.anpfuel.app.ui.components.SyncStatusBanner
import com.anpfuel.app.ui.model.AveragePriceUiModel
import com.anpfuel.app.ui.theme.AnpFuelTheme
import com.anpfuel.domain.state.DataReadinessState
import com.anpfuel.domain.valueobject.BrazilianState
import com.anpfuel.domain.valueobject.FuelProduct
import com.anpfuel.domain.valueobject.SurveyWeek

@Composable
fun PricesScreen(
    onNavigate: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: PricesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val locale = LocalConfiguration.current.locales[0]

    LaunchedEffect(locale) {
        viewModel.load(locale)
    }

    PricesContent(
        uiState = uiState,
        onNavigate = onNavigate,
        onRetry = { viewModel.load(locale) },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PricesContent(
    uiState: PricesUiState,
    onNavigate: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.prices_title)) },
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
            SyncStatusBanner(readiness = uiState.readiness)

            if (uiState.isOffline) {
                OfflineBanner()
            }

            when {
                uiState.isLoading -> LoadingState(modifier = Modifier.fillMaxWidth())

                uiState.error != null -> {
                    ErrorState(
                        message = stringResource(AppErrorMapper.toStringRes(uiState.error)),
                        modifier = Modifier.fillMaxWidth(),
                        onRetry = onRetry,
                    )
                }

                uiState.municipality != null && uiState.state != null -> {
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
            }

            uiState.surveyWeek?.let { week ->
                val locale = LocalConfiguration.current.locales[0]
                Text(
                    text = stringResource(
                        R.string.prices_survey_week_label,
                        SurveyWeekFormatter.formatRange(week, locale),
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            when {
                !uiState.isLoading && uiState.error == null && uiState.isEmptyMunicipality -> {
                    EmptyState(
                        message = stringResource(R.string.prices_empty_municipality),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                !uiState.isLoading && uiState.error == null && uiState.prices.isNotEmpty() -> {
                    uiState.prices.forEach { price ->
                        MunicipalityPriceDetailRow(price = price)
                    }
                    TextButton(
                        onClick = { onNavigate(Routes.HISTORY) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(text = stringResource(R.string.prices_view_history))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PricesScreenPreview() {
    AnpFuelTheme {
        PricesContent(
            uiState = PricesUiState(
                isLoading = false,
                readiness = DataReadinessState.READY,
                municipality = "Curitiba",
                state = BrazilianState.PARANA,
                surveyWeek = SurveyWeek.fromIsoDates("2026-06-07", "2026-06-13"),
                prices = listOf(
                    AveragePriceUiModel(
                        fuelProduct = FuelProduct.GASOLINE_REGULAR,
                        averageFormatted = "R$ 5,89",
                        minimumFormatted = "R$ 5,50",
                        maximumFormatted = "R$ 6,20",
                        stationCount = 128,
                    ),
                ),
            ),
            onRetry = {},
            onNavigate = {},
        )
    }
}
