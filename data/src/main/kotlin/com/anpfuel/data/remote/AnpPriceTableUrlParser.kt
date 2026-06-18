package com.anpfuel.data.remote

import com.anpfuel.domain.model.PriceTable
import com.anpfuel.domain.valueobject.DomainId
import com.anpfuel.domain.valueobject.PriceTableType
import com.anpfuel.domain.valueobject.SurveyWeek
import java.util.UUID

/**
 * Parses ANP XLSX URLs and file names into domain [PriceTable] metadata (BR-001, UC-001).
 */
object AnpPriceTableUrlParser {

    private val URL_PATTERN = Regex(
        """arquivos-lpc/\d{4}/(resumo_semanal|revendas)_lpc_(\d{4}-\d{2}-\d{2})[-_](\d{4}-\d{2}-\d{2})\.xlsx""",
        RegexOption.IGNORE_CASE,
    )

    fun isPriceTableUrl(url: String): Boolean = URL_PATTERN.containsMatchIn(url)

    fun toPriceTable(sourceUrl: String): PriceTable? {
        val match = URL_PATTERN.find(sourceUrl.trim()) ?: return null
        val typeToken = match.groupValues[1]
        val startDate = match.groupValues[2]
        val endDate = match.groupValues[3]

        val tableType = when (typeToken.lowercase()) {
            "resumo_semanal" -> PriceTableType.WEEKLY_SUMMARY
            "revendas" -> PriceTableType.STATION_DETAIL
            else -> return null
        }

        return try {
            val normalizedUrl = sourceUrl.trim()
            val surveyWeek = SurveyWeek.fromIsoDates(startDate, endDate)
            PriceTable.create(
                id = priceTableId(normalizedUrl),
                surveyWeek = surveyWeek,
                tableType = tableType,
                sourceUrl = normalizedUrl,
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun priceTableId(sourceUrl: String): DomainId =
        DomainId.from(UUID.nameUUIDFromBytes(sourceUrl.toByteArray()).toString())
}
