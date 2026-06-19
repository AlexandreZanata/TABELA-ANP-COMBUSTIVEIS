package com.anpfuel.app.ui.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anpfuel.application.usecase.location.PreferredLocation
import com.anpfuel.application.usecase.location.SelectLocationUseCase
import com.anpfuel.domain.valueobject.BrazilianState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface LocationPickerStep {
    data object StateList : LocationPickerStep
    data class MunicipalityList(val state: BrazilianState) : LocationPickerStep
}

data class LocationPickerUiState(
    val isLoading: Boolean = true,
    val step: LocationPickerStep = LocationPickerStep.StateList,
    val states: List<BrazilianState> = emptyList(),
    val municipalities: List<String> = emptyList(),
    val preferredLocation: PreferredLocation? = null,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
) {
    val municipalitiesEmpty: Boolean
        get() = step is LocationPickerStep.MunicipalityList && municipalities.isEmpty() && !isLoading
}

sealed interface LocationPickerNavigation {
    data object ToHome : LocationPickerNavigation
}

@HiltViewModel
class LocationPickerViewModel @Inject constructor(
    private val selectLocationUseCase: SelectLocationUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationPickerUiState())
    val uiState: StateFlow<LocationPickerUiState> = _uiState.asStateFlow()

    private val _navigation = MutableSharedFlow<LocationPickerNavigation>(extraBufferCapacity = 1)
    val navigation: SharedFlow<LocationPickerNavigation> = _navigation.asSharedFlow()

    init {
        loadStates()
    }

    fun loadStates() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, step = LocationPickerStep.StateList) }
            runCatching {
                val preferred = selectLocationUseCase.getPreferredLocation()
                val result = selectLocationUseCase.getStatesWithData()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        states = result.states.sortedBy { state -> state.abbreviation },
                        preferredLocation = preferred,
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = error.message ?: error.javaClass.simpleName)
                }
            }
        }
    }

    fun onStateSelected(state: BrazilianState) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    step = LocationPickerStep.MunicipalityList(state),
                    municipalities = emptyList(),
                )
            }
            runCatching {
                val result = selectLocationUseCase.getMunicipalities(state)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        municipalities = result.municipalities.map { it.municipality },
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = error.message ?: error.javaClass.simpleName)
                }
            }
        }
    }

    fun onBackToStates() {
        _uiState.update {
            it.copy(
                step = LocationPickerStep.StateList,
                municipalities = emptyList(),
                errorMessage = null,
            )
        }
    }

    fun onMunicipalitySelected(municipality: String) {
        val state = (_uiState.value.step as? LocationPickerStep.MunicipalityList)?.state ?: return
        if (_uiState.value.isSaving) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            runCatching {
                selectLocationUseCase.selectLocation(state = state, municipality = municipality)
                _uiState.update { it.copy(isSaving = false) }
                _navigation.emit(LocationPickerNavigation.ToHome)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = error.message ?: error.javaClass.simpleName,
                    )
                }
            }
        }
    }
}
