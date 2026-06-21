package com.anpfuel.app.mapper

import com.anpfuel.app.ui.model.TankFillCostEstimateUiModel
import com.anpfuel.application.usecase.vehicle.VehicleTankFillCostEstimate
import java.util.Locale

object TankFillCostUiMapper {

    fun toUiModels(
        items: List<VehicleTankFillCostEstimate>,
        locale: Locale,
    ): List<TankFillCostEstimateUiModel> =
        items.map { item -> toUiModel(item, locale) }

    fun toUiModel(
        item: VehicleTankFillCostEstimate,
        locale: Locale,
    ): TankFillCostEstimateUiModel {
        val estimate = item.estimate
        return TankFillCostEstimateUiModel(
            vehicleId = item.vehicle.id,
            displayName = item.vehicle.displayName,
            tankCapacityLiters = item.vehicle.tankCapacity.liters.toInt(),
            fuelProduct = item.vehicle.fuelProduct,
            totalCostFormatted = estimate?.totalCost?.let { PriceFormatter.formatAmount(it, locale) },
            stationDisplayName = estimate?.stationDisplayName,
            unitPriceSource = estimate?.unitPriceSource,
        )
    }
}
