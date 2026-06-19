package com.anpfuel.data.remote

import com.anpfuel.domain.valueobject.SurveyWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.ResolverStyle
import java.time.temporal.ChronoField

/**
 * Parses pt-BR ANP listing week section headers (Phase 12.2.1).
 * Example: "07/06/2026 a 13/06/2026"
 */
internal object AnpListingWeekHeaderParser {

    private val WEEK_HEADER_PATTERN = Regex(
        """(\d{1,2}/\d{1,2}/\d{4})\s+a\s+(\d{1,2}/\d{1,2}/\d{4})""",
        RegexOption.IGNORE_CASE,
    )

    private val BRAZILIAN_DATE: DateTimeFormatter = DateTimeFormatterBuilder()
        .appendValue(ChronoField.DAY_OF_MONTH, 1, 2, java.time.format.SignStyle.NOT_NEGATIVE)
        .appendLiteral('/')
        .appendValue(ChronoField.MONTH_OF_YEAR, 1, 2, java.time.format.SignStyle.NOT_NEGATIVE)
        .appendLiteral('/')
        .appendValue(ChronoField.YEAR, 4)
        .toFormatter()
        .withResolverStyle(ResolverStyle.STRICT)

    fun parseWeekHeader(text: String): SurveyWeek? {
        val match = WEEK_HEADER_PATTERN.find(text.trim()) ?: return null
        return try {
            val startDate = LocalDate.parse(match.groupValues[1], BRAZILIAN_DATE)
            val endDate = LocalDate.parse(match.groupValues[2], BRAZILIAN_DATE)
            SurveyWeek(startDate = startDate, endDate = endDate)
        } catch (_: Exception) {
            null
        }
    }
}
