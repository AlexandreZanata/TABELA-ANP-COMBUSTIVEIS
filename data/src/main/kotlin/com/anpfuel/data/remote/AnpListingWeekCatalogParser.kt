package com.anpfuel.data.remote

import com.anpfuel.domain.model.SurveyWeekCatalogEntry
import com.anpfuel.domain.valueobject.PriceTableType
import com.anpfuel.domain.valueobject.SurveyWeek
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.jsoup.select.NodeTraversor
import org.jsoup.select.NodeVisitor

/**
 * Groups ANP listing links under week section headers (Phase 12.2.1).
 */
internal object AnpListingWeekCatalogParser {

    private val HEADER_TAGS = setOf("h1", "h2", "h3", "h4", "h5", "h6", "strong", "p")

    fun parse(html: String, baseUrl: String): List<SurveyWeekCatalogEntry> {
        val document = org.jsoup.Jsoup.parse(html, baseUrl)
        val body = document.body() ?: return emptyList()

        var currentWeek: SurveyWeek? = null
        val buckets = linkedMapOf<SurveyWeek, WeekUrlBucket>()

        NodeTraversor.traverse(
            object : NodeVisitor {
                override fun head(node: Node, depth: Int) {
                    when (node) {
                        is TextNode -> updateCurrentWeek(node.wholeText, currentWeek) { currentWeek = it }
                        is Element -> {
                            if (node.tagName() in HEADER_TAGS) {
                                updateCurrentWeek(node.ownText(), currentWeek) { currentWeek = it }
                            }
                            if (node.tagName() == "a") {
                                val href = node.absUrl("href").ifBlank { node.attr("href") }
                                val priceTable = AnpPriceTableUrlParser.toPriceTable(href) ?: return
                                val week = currentWeek ?: priceTable.surveyWeek
                                val bucket = buckets.getOrPut(week) { WeekUrlBucket(week) }
                                when (priceTable.tableType) {
                                    PriceTableType.WEEKLY_SUMMARY ->
                                        bucket.summaryUrl = bucket.summaryUrl ?: priceTable.sourceUrl
                                    PriceTableType.STATION_DETAIL ->
                                        bucket.stationUrl = bucket.stationUrl ?: priceTable.sourceUrl
                                }
                            }
                        }
                    }
                }

                override fun tail(node: Node, depth: Int) = Unit
            },
            body,
        )

        return buckets.values
            .filter { it.summaryUrl != null && it.stationUrl != null }
            .map {
                SurveyWeekCatalogEntry.create(
                    surveyWeek = it.surveyWeek,
                    summaryUrl = requireNotNull(it.summaryUrl),
                    stationUrl = requireNotNull(it.stationUrl),
                )
            }
            .sortedByDescending { it.surveyWeek.endDate }
    }

    private fun updateCurrentWeek(
        text: String,
        currentWeek: SurveyWeek?,
        onParsed: (SurveyWeek) -> Unit,
    ) {
        val parsed = AnpListingWeekHeaderParser.parseWeekHeader(text) ?: return
        if (parsed != currentWeek) {
            onParsed(parsed)
        }
    }

    private data class WeekUrlBucket(
        val surveyWeek: SurveyWeek,
        var summaryUrl: String? = null,
        var stationUrl: String? = null,
    )
}
