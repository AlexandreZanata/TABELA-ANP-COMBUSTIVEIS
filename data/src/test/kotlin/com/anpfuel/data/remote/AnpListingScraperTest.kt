package com.anpfuel.data.remote

import com.anpfuel.domain.valueobject.PriceTableType
import com.anpfuel.domain.valueobject.SurveyWeek
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AnpListingScraperTest {

    private val scraper = AnpListingScraper(OkHttpClient())

    @Test
    fun parsesFixtureAndFindsLatestWeekSummaryAndStationUrls() {
        val html = AnpFixtureFiles.readListingHtml()

        val priceTables = scraper.parsePriceTablesFromHtml(html)

        assertTrue(priceTables.size >= 4)
        val latestWeek = SurveyWeek.fromIsoDates("2026-06-07", "2026-06-13")
        val latestWeekTables = priceTables.filter { it.surveyWeek == latestWeek }

        assertEquals(2, latestWeekTables.size)
        assertTrue(latestWeekTables.any { it.tableType == PriceTableType.WEEKLY_SUMMARY })
        assertTrue(latestWeekTables.any { it.tableType == PriceTableType.STATION_DETAIL })
    }

    @Test
    fun resolvesRelativeHrefUsingListingBaseUrl() {
        val html = AnpFixtureFiles.readListingHtml()

        val priceTables = scraper.parsePriceTablesFromHtml(html)

        assertTrue(
            priceTables.any {
                it.sourceUrl.endsWith("resumo_semanal_lpc_2026-04-12-2026-04-18.xlsx")
            },
        )
    }

    @Test
    fun mapsFixtureLinksToValidSurveyWeekMetadata() {
        val html = AnpFixtureFiles.readListingHtml()

        val priceTables = scraper.parsePriceTablesFromHtml(html)

        priceTables.forEach { priceTable ->
            assertTrue(priceTable.surveyWeek.inclusiveDayCount <= 7)
            assertTrue(priceTable.sourceUrl.startsWith("https://"))
        }
    }
}
