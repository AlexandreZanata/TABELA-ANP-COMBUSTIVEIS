package com.anpfuel.app.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.anpfuel.app.mapper.FuelProductI18n
import com.anpfuel.app.ui.theme.AnpFuelTheme
import com.anpfuel.domain.valueobject.FuelProduct

@Composable
fun FuelProductLabel(
    product: FuelProduct,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(FuelProductI18n.toStringRes(product)),
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Preview(showBackground = true)
@Composable
private fun FuelProductLabelPreview() {
    AnpFuelTheme {
        FuelProductLabel(product = FuelProduct.GASOLINE_REGULAR)
    }
}
