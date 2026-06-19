package com.anpfuel.app.ui.location

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import com.anpfuel.app.ui.components.AnpScaffold
import com.anpfuel.app.ui.components.AnpTopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anpfuel.app.R
import com.anpfuel.app.mapper.BrazilianStateI18n
import com.anpfuel.app.mapper.DataAvailabilityI18n
import com.anpfuel.application.usecase.location.CatalogMunicipalityItem
import com.anpfuel.app.ui.components.AnpAttributionFooter
import com.anpfuel.app.ui.components.EmptyState
import com.anpfuel.app.ui.components.ErrorState
import com.anpfuel.app.ui.components.LoadingState
import com.anpfuel.app.ui.theme.AnpFuelTheme
import com.anpfuel.domain.valueobject.BrazilianState

@Composable
fun LocationPickerScreen(
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LocationPickerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.navigation.collect { destination ->
            when (destination) {
                LocationPickerNavigation.ToHome -> onNavigateToHome()
            }
        }
    }

    LocationPickerContent(
        uiState = uiState,
        onStateSelected = viewModel::onStateSelected,
        onMunicipalitySelected = viewModel::onMunicipalitySelected,
        onBackToStates = viewModel::onBackToStates,
        onRetry = viewModel::loadStates,
        onStateSearchQueryChange = viewModel::onStateSearchQueryChange,
        onMunicipalitySearchQueryChange = viewModel::onMunicipalitySearchQueryChange,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LocationPickerContent(
    uiState: LocationPickerUiState,
    onStateSelected: (BrazilianState) -> Unit,
    onMunicipalitySelected: (String) -> Unit,
    onBackToStates: () -> Unit,
    onRetry: () -> Unit,
    onStateSearchQueryChange: (String) -> Unit,
    onMunicipalitySearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val step = uiState.step
    val showBackToStates = step is LocationPickerStep.MunicipalityList

    AnpScaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AnpTopAppBar(
                title = {
                    Text(text = stringResource(R.string.location_picker_title))
                },
                navigationIcon = {
                    if (showBackToStates) {
                        IconButton(onClick = onBackToStates) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.action_back),
                            )
                        }
                    }
                },
            )
        },
        bottomBar = { AnpAttributionFooter() },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when {
                uiState.errorMessage != null && uiState.states.isEmpty() -> {
                    ErrorState(
                        message = uiState.errorMessage,
                        modifier = Modifier.align(Alignment.Center),
                        onRetry = onRetry,
                    )
                }

                uiState.isLoading && uiState.states.isEmpty() -> {
                    LoadingState(modifier = Modifier.align(Alignment.Center))
                }

                step is LocationPickerStep.StateList -> {
                    StateListContent(
                        states = uiState.states,
                        searchQuery = uiState.stateSearchQuery,
                        preferredLocation = uiState.preferredLocation,
                        onStateSelected = onStateSelected,
                        onSearchQueryChange = onStateSearchQueryChange,
                    )
                }

                step is LocationPickerStep.MunicipalityList -> {
                    MunicipalityListContent(
                        state = step.state,
                        municipalities = uiState.municipalities,
                        searchQuery = uiState.municipalitySearchQuery,
                        isLoading = uiState.isLoading,
                        isEmpty = uiState.municipalitiesEmpty,
                        preferredMunicipality = uiState.preferredLocation
                            ?.takeIf { it.state == step.state }
                            ?.municipality,
                        onMunicipalitySelected = onMunicipalitySelected,
                        onRetry = { onStateSelected(step.state) },
                        onSearchQueryChange = onMunicipalitySearchQueryChange,
                        errorMessage = uiState.errorMessage,
                    )
                }
            }

            if (uiState.isSaving) {
                LoadingState(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun StateListContent(
    states: List<BrazilianState>,
    searchQuery: String,
    preferredLocation: com.anpfuel.application.usecase.location.PreferredLocation?,
    onStateSelected: (BrazilianState) -> Unit,
    onSearchQueryChange: (String) -> Unit,
) {
    val stateLabels = states.associateWith { state ->
        stringResource(BrazilianStateI18n.toStringRes(state))
    }
    val filteredStates = LocationPickerFilter.filterStates(
        states = states,
        query = searchQuery,
        stateLabel = stateLabels::getValue,
    )

    Column(modifier = Modifier.fillMaxSize()) {
        LocationPickerSearchField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            label = stringResource(R.string.location_search_state_hint),
        )
        Text(
            text = stringResource(R.string.location_state_label),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
        if (preferredLocation != null) {
            Text(
                text = stringResource(
                    R.string.location_current_selection,
                    preferredLocation.municipality,
                    preferredLocation.state.abbreviation,
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            )
        }
        if (filteredStates.isEmpty()) {
            EmptyState(
                message = stringResource(R.string.location_search_no_matches),
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredStates, key = { it.name }) { state ->
                    ListItem(
                        headlineContent = {
                            Text(text = stateLabels.getValue(state))
                        },
                        supportingContent = {
                            Text(text = state.abbreviation)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onStateSelected(state) },
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun LocationPickerSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        label = { Text(text = label) },
        singleLine = true,
    )
}

@Composable
internal fun MunicipalityListContent(
    state: BrazilianState,
    municipalities: List<CatalogMunicipalityItem>,
    searchQuery: String,
    isLoading: Boolean,
    isEmpty: Boolean,
    preferredMunicipality: String?,
    onMunicipalitySelected: (String) -> Unit,
    onRetry: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    errorMessage: String?,
) {
    val filteredMunicipalities = remember(municipalities, searchQuery) {
        LocationPickerFilter.filterMunicipalities(municipalities, searchQuery)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LocationPickerSearchField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            label = stringResource(R.string.location_search_municipality_hint),
        )
        Text(
            text = stringResource(
                R.string.location_municipality_label,
                stringResource(BrazilianStateI18n.toStringRes(state)),
            ),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )

        when {
            errorMessage != null -> {
                ErrorState(
                    message = errorMessage,
                    modifier = Modifier.fillMaxWidth(),
                    onRetry = onRetry,
                )
            }

            isLoading -> {
                LoadingState(modifier = Modifier.fillMaxWidth())
            }

            isEmpty -> {
                EmptyState(
                    message = stringResource(R.string.location_empty_state),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            filteredMunicipalities.isEmpty() -> {
                EmptyState(
                    message = stringResource(R.string.location_search_no_matches),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            else -> {
                val sections = remember(filteredMunicipalities) {
                    groupMunicipalitiesBySectionLetter(filteredMunicipalities)
                }
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    sections.forEach { section ->
                        item(key = "header-${section.letter}") {
                            Surface(color = MaterialTheme.colorScheme.surfaceContainerHigh, modifier = Modifier.fillMaxWidth()) {
                                Text(section.letter.toString(), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
                            }
                        }
                        items(section.items, key = { it.municipality }) { item ->
                            val subtitleRes = DataAvailabilityI18n.toLocationSubtitleStringRes(item.dataAvailability)
                            ListItem(
                                headlineContent = { Text(item.municipality, color = if (item.municipality == preferredMunicipality) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface) },
                                supportingContent = if (subtitleRes != null) { { Text(stringResource(subtitleRes), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) } } else null,
                                modifier = Modifier.fillMaxWidth().clickable { onMunicipalitySelected(item.municipality) },
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LocationPickerStatesPreview() {
    AnpFuelTheme {
        LocationPickerContent(
            uiState = LocationPickerUiState(
                isLoading = false,
                states = listOf(BrazilianState.PARANA, BrazilianState.SAO_PAULO),
            ),
            onStateSelected = {},
            onMunicipalitySelected = {},
            onBackToStates = {},
            onRetry = {},
            onStateSearchQueryChange = {},
            onMunicipalitySearchQueryChange = {},
        )
    }
}
