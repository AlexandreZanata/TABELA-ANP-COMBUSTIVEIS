package com.anpfuel.domain.sync

import com.anpfuel.domain.model.PriceTable
import com.anpfuel.domain.valueobject.PriceTableType

/**
 * Selects which discovered [PriceTable] files belong to the latest survey week (UC-001).
 */
object PriceTableSyncPlanner {

    fun selectLatestWeekTables(discovered: List<PriceTable>): List<PriceTable> {
        val latestWeek = discovered.maxByOrNull { it.surveyWeek.endDate }?.surveyWeek
            ?: return emptyList()
        return discovered.filter { it.surveyWeek == latestWeek }
    }

    fun resolveTablesForSync(
        latestWeekTables: List<PriceTable>,
        syncStationDetail: Boolean,
    ): List<PriceTable> {
        val summary = latestWeekTables.firstOrNull { it.tableType == PriceTableType.WEEKLY_SUMMARY }
            ?: return emptyList()

        if (!syncStationDetail) {
            return listOf(summary)
        }

        val station = latestWeekTables.firstOrNull { it.tableType == PriceTableType.STATION_DETAIL }
        return if (station == null) {
            listOf(summary)
        } else {
            listOf(summary, station)
        }
    }
}
