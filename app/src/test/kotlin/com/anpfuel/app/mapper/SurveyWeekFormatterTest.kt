package com.anpfuel.app.mapper

import com.anpfuel.domain.valueobject.SurveyWeek
import java.util.Locale
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SurveyWeekFormatterTest {

    @Test
    fun formatRangeUsesPortugueseConnectorForPtLocale() {
        val week = SurveyWeek.fromIsoDates("2026-05-31", "2026-06-06")

        val formatted = SurveyWeekFormatter.formatRange(week, Locale.forLanguageTag("pt-BR"))

        assertTrue(formatted.contains(" a "))
    }

    @Test
    fun formatRangeUsesEnDashForEnglishLocale() {
        val week = SurveyWeek.fromIsoDates("2026-05-31", "2026-06-06")

        val formatted = SurveyWeekFormatter.formatRange(week, Locale.US)

        assertTrue(formatted.contains("–"))
    }
}
