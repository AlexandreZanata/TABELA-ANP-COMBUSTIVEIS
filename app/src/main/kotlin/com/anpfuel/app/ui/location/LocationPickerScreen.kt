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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anpfuel.app.R
import com.anpfuel.app.mapper.BrazilianStateI18n
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
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocationPickerContent(
    uiState: LocationPickerUiState,
    onStateSelected: (BrazilianState) -> Unit,
    onMunicipalitySelected: (String) -> Unit,
    onBackToStates: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val step = uiState.step
    val showBackToStates = step is LocationPickerStep.MunicipalityList

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
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
                        preferredLocation = uiState.preferredLocation,
                        onStateSelected = onStateSelected,
                    )
                }

                step is LocationPickerStep.MunicipalityList -> {
                    MunicipalityListContent(
                        state = step.state,
                        municipalities = uiState.municipalities,
                        isLoading = uiState.isLoading,
                        isEmpty = uiState.municipalitiesEmpty,
                        preferredMunicipality = uiState.preferredLocation
                            ?.takeIf { it.state == step.state }
                            ?.municipality,
                        onMunicipalitySelected = onMunicipalitySelected,
                        onRetry = { onStateSelected(step.state) },
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
    preferredLocation: com.anpfuel.application.usecase.location.PreferredLocation?,
    onStateSelected: (BrazilianState) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
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
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(states, key = { it.name }) { state ->
                ListItem(
                    headlineContent = {
                        Text(text = stringResource(BrazilianStateI18n.toStringRes(state)))
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

@Composable
private fun MunicipalityListContent(
    state: BrazilianState,
    municipalities: List<String>,
    isLoading: Boolean,
    isEmpty: Boolean,
    preferredMunicipality: String?,
    onMunicipalitySelected: (String) -> Unit,
    onRetry: () -> Unit,
    errorMessage: String?,
) {
    Column(modifier = Modifier.fillMaxSize()) {
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

            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(municipalities, key = { it }) { municipality ->
                        val isPreferred = municipality == preferredMunicipality
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = municipality,
                                    color = if (isPreferred) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    },
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onMunicipalitySelected(municipality) },
                        )
                        HorizontalDivider()
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
        )
    }
}
