package com.anpfuel.data.remote

import com.anpfuel.domain.model.SurveyWeekCatalogEntry
import com.anpfuel.domain.valueobject.PriceTableType
import com.anpfuel.domain.valueobject.SurveyWeek
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.jsoup.select.NodeTraversor
import org.jsoup.select.NodeVisitor
import java.time.LocalDate

/**
 * Groups ANP listing links under week section headers (Phase 12.2.1–12.2.3).
 */
internal object AnpListingWeekCatalogParser {

    private val HEADER_TAGS = setOf("h1", "h2", "h3", "h4", "h5", "h6", "strong", "p")
    private val NOTE_TAGS = setOf("p", "div", "em", "small", "span")

    fun parse(html: String, baseUrl: String): List<SurveyWeekCatalogEntry> {
        val document = org.jsoup.Jsoup.parse(html, baseUrl)
        val body = document.body() ?: return emptyList()

        var currentWeek: SurveyWeek? = null
        val buckets = linkedMapOf<SurveyWeek, WeekUrlBucket>()

        NodeTraversor.traverse(
            object : NodeVisitor {
                override fun head(node: Node, depth: Int) {
                    when (node) {
                        is TextNode -> {
                            updateCurrentWeek(node.wholeText, currentWeek) { currentWeek = it }
                            attachOperationalNote(node.wholeText, currentWeek, buckets)
                        }
                        is Element -> {
                            if (node.tagName() in HEADER_TAGS) {
                                updateCurrentWeek(node.ownText(), currentWeek) { currentWeek = it }
                            }
                            if (node.tagName() in NOTE_TAGS) {
                                attachOperationalNote(node.text(), currentWeek, buckets)
                            }
                            if (node.tagName() == "a") {
                                val href = node.absUrl("href").ifBlank { node.attr("href") }
                                val priceTable = AnpPriceTableUrlParser.toPriceTable(href) ?: return
                                val week = currentWeek ?: priceTable.surveyWeek
                                val bucket = buckets.getOrPut(week) { WeekUrlBucket(week) }
                                val linkContextText = anchorContextText(node)
                                val updatedAt = AnpListingUpdatedAtParser.parseUpdatedAt(linkContextText)
                                when (priceTable.tableType) {
                                    PriceTableType.WEEKLY_SUMMARY -> {
                                        bucket.summaryUrl = bucket.summaryUrl ?: priceTable.sourceUrl
                                        bucket.summaryPublishedAt = updatedAt ?: bucket.summaryPublishedAt
                                    }
                                    PriceTableType.STATION_DETAIL -> {
                                        bucket.stationUrl = bucket.stationUrl ?: priceTable.sourceUrl
                                        bucket.stationPublishedAt = updatedAt ?: bucket.stationPublishedAt
                                    }
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
                    publishedAt = it.resolvePublishedAt(),
                    operationalNote = it.operationalNote,
                )
            }
            .sortedByDescending { it.surveyWeek.endDate }
    }

    private fun attachOperationalNote(
        text: String,
        currentWeek: SurveyWeek?,
        buckets: MutableMap<SurveyWeek, WeekUrlBucket>,
    ) {
        val week = currentWeek ?: return
        val note = AnpListingOperationalNoteParser.parseOperationalNote(text) ?: return
        val bucket = buckets.getOrPut(week) { WeekUrlBucket(week) }
        if (bucket.operationalNote == null) {
            bucket.operationalNote = note
        }
    }

    private fun anchorContextText(anchor: Element): String {
        val listItem = anchor.parent()?.takeIf { it.tagName() == "li" }
        return listItem?.text() ?: anchor.text()
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
        var summaryPublishedAt: LocalDate? = null,
        var stationPublishedAt: LocalDate? = null,
        var operationalNote: String? = null,
    ) {
        fun resolvePublishedAt(): LocalDate? =
            listOfNotNull(summaryPublishedAt, stationPublishedAt).maxOrNull()
    }
}
