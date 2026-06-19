package com.anpfuel.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anpfuel.app.navigation.Routes
import com.anpfuel.application.usecase.navigation.ResolveAppStartDestinationUseCase
import com.anpfuel.domain.navigation.AppStartDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AppStartViewModel @Inject constructor(
    private val resolveAppStartDestinationUseCase: ResolveAppStartDestinationUseCase,
) : ViewModel() {

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            _startDestination.value = resolveAppStartDestinationUseCase().toRoute()
        }
    }

    private fun AppStartDestination.toRoute(): String = when (this) {
        AppStartDestination.ONBOARDING -> Routes.ONBOARDING
        AppStartDestination.HOME -> Routes.HOME
        AppStartDestination.WEEK_PICKER -> Routes.WEEK_PICKER
    }
}
