package com.anpfuel.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anpfuel.app.R
import com.anpfuel.app.mapper.FuelProductI18n
import com.anpfuel.app.ui.model.TankFillCostEstimateUiModel
import com.anpfuel.app.ui.theme.AnpFuelTheme
import com.anpfuel.domain.model.TankFillCostUnitPriceSource
import com.anpfuel.domain.valueobject.DomainId
import com.anpfuel.domain.valueobject.FuelProduct

@Composable
fun TankFillCostCard(
    estimate: TankFillCostEstimateUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val fuelLabel = stringResource(FuelProductI18n.toStringRes(estimate.fuelProduct))
    val totalCost = estimate.totalCostFormatted
        ?: stringResource(R.string.home_tank_fill_cost_unavailable)
    val contentDescription = stringResource(
        R.string.a11y_tank_fill_cost_card,
        estimate.displayName,
        totalCost,
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 112.dp)
            .semantics(mergeDescendants = true) {
                this.contentDescription = contentDescription
                role = Role.Button
            }
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = stringResource(R.string.home_tank_fill_cost_title),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = totalCost,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = estimate.displayName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(
                    R.string.home_tank_fill_cost_liters_format,
                    fuelLabel,
                    estimate.tankCapacityLiters,
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            estimate.stationDisplayName?.let { stationName ->
                Text(
                    text = stringResource(R.string.home_tank_fill_cost_station_name, stationName),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Tank fill cost available")
@Composable
private fun TankFillCostCardAvailablePreview() {
    AnpFuelTheme(darkTheme = false, dynamicColor = false) {
        TankFillCostCard(
            estimate = TankFillCostEstimateUiModel(
                vehicleId = DomainId.generate(),
                displayName = "Gol",
                tankCapacityLiters = 50,
                fuelProduct = FuelProduct.GASOLINE_REGULAR,
                totalCostFormatted = "R$ 274,50",
                stationDisplayName = "Posto Cheap",
                unitPriceSource = TankFillCostUnitPriceSource.CHEAPEST_STATION,
            ),
            onClick = {},
        )
    }
}

@Preview(showBackground = true, name = "Tank fill cost unavailable")
@Composable
private fun TankFillCostCardUnavailablePreview() {
    AnpFuelTheme(darkTheme = true, dynamicColor = false) {
        TankFillCostCard(
            estimate = TankFillCostEstimateUiModel(
                vehicleId = DomainId.generate(),
                displayName = "Onix",
                tankCapacityLiters = 44,
                fuelProduct = FuelProduct.ETHANOL,
                totalCostFormatted = null,
                stationDisplayName = null,
                unitPriceSource = null,
            ),
            onClick = {},
        )
    }
}
