package com.anpfuel.app.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anpfuel.app.navigation.Routes
import com.anpfuel.application.error.AppError
import com.anpfuel.application.usecase.onboarding.CompleteOnboardingResult
import com.anpfuel.application.usecase.onboarding.CompleteOnboardingUseCase
import com.anpfuel.application.usecase.sync.SyncPriceTablesUseCase
import com.anpfuel.domain.event.SyncJobOutcome
import com.anpfuel.domain.event.SyncRequestSource
import com.anpfuel.domain.repository.UserPreferencesRepository
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

data class OnboardingUiState(
    val pageIndex: Int = 0,
    val pageCount: Int = OnboardingViewModel.PAGE_COUNT,
    val isSyncing: Boolean = false,
    val error: AppError? = null,
) {
    val isOnLastPage: Boolean
        get() = pageIndex == pageCount - 1
}

sealed interface OnboardingNavigation {
    data object ToHome : OnboardingNavigation
    data object ToLocation : OnboardingNavigation
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val syncPriceTablesUseCase: SyncPriceTablesUseCase,
    private val completeOnboardingUseCase: CompleteOnboardingUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private val _navigation = MutableSharedFlow<OnboardingNavigation>(extraBufferCapacity = 1)
    val navigation: SharedFlow<OnboardingNavigation> = _navigation.asSharedFlow()

    fun onNextPage() {
        _uiState.update { state ->
            if (state.pageIndex < state.pageCount - 1) {
                state.copy(pageIndex = state.pageIndex + 1, error = null)
            } else {
                state
            }
        }
    }

    fun onPreviousPage() {
        _uiState.update { state ->
            if (state.pageIndex > 0) {
                state.copy(pageIndex = state.pageIndex - 1, error = null)
            } else {
                state
            }
        }
    }

    fun onPageSelected(index: Int) {
        _uiState.update { it.copy(pageIndex = index.coerceIn(0, it.pageCount - 1), error = null) }
    }

    fun startSync() {
        if (_uiState.value.isSyncing) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, error = null) }

            val syncResult = syncPriceTablesUseCase(SyncRequestSource.FIRST_LAUNCH)
            when (syncResult.outcome) {
                SyncJobOutcome.FAILED -> {
                    _uiState.update {
                        it.copy(
                            isSyncing = false,
                            error = syncResult.error ?: AppError.SyncNetworkError,
                        )
                    }
                    return@launch
                }
                else -> handleSyncCompletion(syncResult)
            }
        }
    }

    fun skipSync() {
        viewModelScope.launch {
            completeOnboardingUseCase.skipSync()
            _navigation.emit(OnboardingNavigation.ToHome)
        }
    }

    private suspend fun handleSyncCompletion(
        syncResult: com.anpfuel.application.usecase.sync.SyncPriceTablesResult,
    ) {
        when (completeOnboardingUseCase.completeAfterSync(syncResult)) {
            CompleteOnboardingResult.Completed,
            CompleteOnboardingResult.AlreadyCompleted,
            -> navigateAfterCompletion()

            CompleteOnboardingResult.NotReady -> {
                _uiState.update {
                    it.copy(
                        isSyncing = false,
                        error = syncResult.error ?: AppError.SyncNetworkError,
                    )
                }
            }

            CompleteOnboardingResult.Skipped -> {
                _uiState.update { it.copy(isSyncing = false) }
            }
        }
    }

    private suspend fun navigateAfterCompletion() {
        _uiState.update { it.copy(isSyncing = false, error = null) }
        val preferences = userPreferencesRepository.getPreferences()
        val destination = if (preferences.preferredMunicipality.isNullOrBlank()) {
            OnboardingNavigation.ToLocation
        } else {
            OnboardingNavigation.ToHome
        }
        _navigation.emit(destination)
    }

    companion object {
        const val PAGE_COUNT = 3
    }
}
