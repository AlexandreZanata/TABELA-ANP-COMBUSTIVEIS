package com.anpfuel.app.ui.stations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anpfuel.application.error.AppError
import com.anpfuel.application.error.AppErrorResolver
import com.anpfuel.application.usecase.location.SelectLocationUseCase
import com.anpfuel.application.usecase.network.ObserveNetworkConnectivityUseCase
import com.anpfuel.application.usecase.price.GetStationPricesUseCase
import com.anpfuel.application.usecase.price.StationPricesOutcome
import com.anpfuel.application.usecase.sync.DownloadStationDetailUseCase
import com.anpfuel.app.mapper.StationPriceUiMapper
import com.anpfuel.app.ui.model.StationPriceUiModel
import com.anpfuel.domain.event.SyncJobOutcome
import com.anpfuel.domain.valueobject.BrazilianState
import com.anpfuel.domain.valueobject.FuelProduct
import com.anpfuel.domain.valueobject.SurveyWeek
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StationsUiState(
    val isLoading: Boolean = true,
    val isDownloading: Boolean = false,
    val isOffline: Boolean = false,
    val selectedFuelProduct: FuelProduct = FuelProduct.GASOLINE_REGULAR,
    val municipality: String? = null,
    val state: BrazilianState? = null,
    val surveyWeek: SurveyWeek? = null,
    val stations: List<StationPriceUiModel> = emptyList(),
    val showDownloadPrompt: Boolean = false,
    val showEmpty: Boolean = false,
    val showNoLocation: Boolean = false,
    val error: AppError? = null,
    val errorMessage: String? = null,
)

@HiltViewModel
class StationsViewModel @Inject constructor(
    private val getStationPricesUseCase: GetStationPricesUseCase,
    private val downloadStationDetailUseCase: DownloadStationDetailUseCase,
    private val selectLocationUseCase: SelectLocationUseCase,
    observeNetworkConnectivityUseCase: ObserveNetworkConnectivityUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(StationsUiState())
    val uiState: StateFlow<StationsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeNetworkConnectivityUseCase().collect { isConnected ->
                _uiState.update { it.copy(isOffline = !isConnected) }
            }
        }
    }

    fun load(locale: Locale) {
        loadForFuel(_uiState.value.selectedFuelProduct, locale)
    }

    fun onFuelProductSelected(fuelProduct: FuelProduct, locale: Locale) {
        if (_uiState.value.selectedFuelProduct == fuelProduct) {
            return
        }
        _uiState.update { it.copy(selectedFuelProduct = fuelProduct) }
        loadForFuel(fuelProduct, locale)
    }

    fun downloadStationDetail(locale: Locale) {
        val state = _uiState.value.state ?: return
        val municipality = _uiState.value.municipality ?: return
        if (_uiState.value.isDownloading) {
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isDownloading = true,
                    error = null,
                    errorMessage = null,
                )
            }

            runCatching {
                val result = downloadStationDetailUseCase(
                    state = state,
                    municipality = municipality,
                    surveyWeek = _uiState.value.surveyWeek,
                )

                if (result.outcome == SyncJobOutcome.FAILED) {
                    _uiState.update {
                        it.copy(
                            isDownloading = false,
                            error = result.error ?: AppError.StationDetailNotSynced,
                        )
                    }
                    return@launch
                }

                _uiState.update { it.copy(isDownloading = false) }
                loadForFuel(_uiState.value.selectedFuelProduct, locale)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isDownloading = false,
                        error = AppErrorResolver.fromThrowable(error),
                        errorMessage = error.message ?: error.javaClass.simpleName,
                    )
                }
            }
        }
    }

    private fun loadForFuel(fuelProduct: FuelProduct, locale: Locale) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    errorMessage = null,
                    showDownloadPrompt = false,
                    showEmpty = false,
                    showNoLocation = false,
                    stations = emptyList(),
                )
            }

            runCatching {
                val preferred = selectLocationUseCase.getPreferredLocation()
                if (preferred == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            showNoLocation = true,
                        )
                    }
                    return@launch
                }

                when (val outcome = getStationPricesUseCase(fuelProduct = fuelProduct)) {
                    is StationPricesOutcome.StationDetailMissing -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                showDownloadPrompt = true,
                                municipality = preferred.municipality,
                                state = preferred.state,
                                selectedFuelProduct = fuelProduct,
                            )
                        }
                    }

                    is StationPricesOutcome.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                municipality = outcome.municipality,
                                state = outcome.state,
                                surveyWeek = outcome.surveyWeek,
                                selectedFuelProduct = outcome.fuelProduct,
                                stations = StationPriceUiMapper.toUiModels(
                                    stations = outcome.stations,
                                    locale = locale,
                                ),
                                showEmpty = outcome.isEmpty,
                            )
                        }
                    }
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = AppErrorResolver.fromThrowable(error),
                        errorMessage = error.message ?: error.javaClass.simpleName,
                    )
                }
            }
        }
    }
}
