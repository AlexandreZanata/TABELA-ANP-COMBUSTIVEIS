package com.anpfuel.app.ui.weekpicker

import com.anpfuel.application.error.AppError
import com.anpfuel.domain.model.SurveyWeekCatalogEntry

data class WeekPickerUiState(
    val catalog: List<SurveyWeekCatalogEntry> = emptyList(),
    val isLoadingCatalog: Boolean = false,
    val catalogError: AppError? = null,
    val syncError: AppError? = null,
    val showSkipAction: Boolean = false,
)
