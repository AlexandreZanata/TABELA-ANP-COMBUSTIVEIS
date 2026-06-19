package com.anpfuel.domain.model

import com.anpfuel.domain.valueobject.BrazilianState
import com.anpfuel.domain.valueobject.FuelProduct
import com.anpfuel.domain.valueobject.SurveyWeek

/**
 * Local user preferences stored on device (UC-008).
 */
data class UserPreferences(
    val preferredState: BrazilianState? = null,
    val preferredMunicipality: String? = null,
    val preferredFuelProduct: FuelProduct? = null,
    val localeTag: String = "en",
    val syncStationDetail: Boolean = false,
    val stationDetailRetentionWeeks: Int = DEFAULT_RETENTION_WEEKS,
    val autoSyncOnWifi: Boolean = true,
    val showPriceHistory: Boolean = true,
    val onboardingCompleted: Boolean = false,
    val activeSurveyWeek: SurveyWeek? = null,
) {
    companion object {
        const val DEFAULT_RETENTION_WEEKS = 12
    }
}
