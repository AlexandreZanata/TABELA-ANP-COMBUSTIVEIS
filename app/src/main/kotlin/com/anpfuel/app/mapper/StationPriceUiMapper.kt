package com.anpfuel.app.mapper

import com.anpfuel.app.ui.model.StationPriceUiModel
import com.anpfuel.domain.model.StationPrice
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

object StationPriceUiMapper {

    fun toUiModels(
        stations: List<StationPrice>,
        locale: Locale,
    ): List<StationPriceUiModel> =
        stations.map { station -> toUiModel(station, locale) }

    fun toUiModel(stationPrice: StationPrice, locale: Locale): StationPriceUiModel {
        val collectedAtLabel = stationPrice.collectedAt?.let { date ->
            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                .withLocale(locale)
                .format(date)
        }

        return StationPriceUiModel(
            displayName = stationPrice.station.displayName(),
            brand = stationPrice.station.brand,
            address = stationPrice.station.address,
            priceFormatted = PriceFormatter.formatAmount(stationPrice.price, locale)
                ?: stationPrice.price.value.toPlainString(),
            collectedAtLabel = collectedAtLabel,
        )
    }
}
