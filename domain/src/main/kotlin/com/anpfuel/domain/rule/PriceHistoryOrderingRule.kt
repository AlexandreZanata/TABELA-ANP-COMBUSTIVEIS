package com.anpfuel.domain.rule

import com.anpfuel.domain.model.AveragePrice

/**
 * UC-006 / BR-003 — Present immutable average price history ordered by survey week start date.
 */
object PriceHistoryOrderingRule {

    fun orderBySurveyWeekStartDate(prices: List<AveragePrice>): List<AveragePrice> =
        prices.sortedBy { it.surveyWeek.startDate }
}
