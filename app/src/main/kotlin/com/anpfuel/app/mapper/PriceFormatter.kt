package com.anpfuel.app.mapper

import com.anpfuel.domain.valueobject.PriceAmount
import com.anpfuel.domain.valueobject.SurveyWeek
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Currency
import java.util.Locale

object PriceFormatter {

    private val currency = Currency.getInstance("BRL")

    fun formatAmount(amount: PriceAmount?, locale: Locale): String? {
        amount ?: return null
        val formatter = NumberFormat.getCurrencyInstance(locale).apply {
            this.currency = currency
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
        return formatter.format(amount.value)
    }
}

object SurveyWeekFormatter {

    fun formatRange(week: SurveyWeek, locale: Locale): String {
        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
        return "${week.startDate.format(formatter)} – ${week.endDate.format(formatter)}"
    }
}

object LastSyncFormatter {

    fun format(instant: Instant?, locale: Locale, zoneId: ZoneId = ZoneId.systemDefault()): String? {
        instant ?: return null
        val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
            .withLocale(locale)
            .withZone(zoneId)
        return formatter.format(instant)
    }
}
