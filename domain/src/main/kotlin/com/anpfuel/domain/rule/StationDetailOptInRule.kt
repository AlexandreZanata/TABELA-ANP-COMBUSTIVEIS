package com.anpfuel.domain.rule

/**
 * BR-008 — Station detail files are not auto-downloaded unless opted in or already cached.
 */
object StationDetailOptInRule {

    fun requiresOnDemandDownload(
        syncStationDetail: Boolean,
        hasLocalStationData: Boolean,
    ): Boolean = !syncStationDetail && !hasLocalStationData
}
