package com.anpfuel.app.ui.model

import com.anpfuel.domain.model.TankFillCostUnitPriceSource
import com.anpfuel.domain.valueobject.DomainId
import com.anpfuel.domain.valueobject.FuelProduct

data class TankFillCostEstimateUiModel(
    val vehicleId: DomainId,
    val displayName: String,
    val tankCapacityLiters: Int,
    val fuelProduct: FuelProduct,
    val totalCostFormatted: String?,
    val stationDisplayName: String?,
    val unitPriceSource: TankFillCostUnitPriceSource?,
)
