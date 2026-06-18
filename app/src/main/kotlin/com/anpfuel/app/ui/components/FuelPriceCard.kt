package com.anpfuel.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anpfuel.app.R
import com.anpfuel.app.ui.model.AveragePriceUiModel
import com.anpfuel.app.ui.theme.AnpFuelTheme
import com.anpfuel.domain.valueobject.FuelProduct

@Composable
fun FuelPriceCard(
    price: AveragePriceUiModel,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FuelProductLabel(product = price.fuelProduct)
            Text(
                text = price.averageFormatted ?: stringResource(R.string.prices_not_available),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
fun MunicipalityPriceDetailRow(
    price: AveragePriceUiModel,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FuelProductLabel(product = price.fuelProduct)
            Text(
                text = stringResource(
                    R.string.prices_average_label,
                    price.averageFormatted ?: stringResource(R.string.prices_not_available),
                ),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (price.minimumFormatted != null || price.maximumFormatted != null) {
                Text(
                    text = stringResource(
                        R.string.prices_min_max_range,
                        price.minimumFormatted ?: stringResource(R.string.prices_not_available),
                        price.maximumFormatted ?: stringResource(R.string.prices_not_available),
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            price.stationCount?.let { count ->
                Text(
                    text = stringResource(R.string.prices_station_count, count),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FuelPriceCardPreview() {
    AnpFuelTheme {
        FuelPriceCard(
            price = AveragePriceUiModel(
                fuelProduct = FuelProduct.ETHANOL,
                averageFormatted = "R$ 3,42",
                minimumFormatted = "R$ 3,10",
                maximumFormatted = "R$ 3,80",
                stationCount = 42,
            ),
            onClick = {},
        )
    }
}
