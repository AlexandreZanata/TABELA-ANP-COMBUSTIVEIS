package com.anpfuel.domain.rule

import com.anpfuel.domain.model.RetailStation
import com.anpfuel.domain.valueobject.BrazilianState

/**
 * BR-026 — Builds a [StationNavigationQuery] string for external map apps from ANP station data.
 */
object StationAddressNormalizationRule {

    private val WHITESPACE_REGEX = Regex("\\s+")
    private val CONTROL_CHARS_REGEX = Regex("\\p{C}+")

    private const val MIN_SUFFICIENT_ADDRESS_LENGTH = 5

    fun normalizeAddress(raw: String): String =
        raw.replace(CONTROL_CHARS_REGEX, "")
            .trim()
            .replace(WHITESPACE_REGEX, " ")

    fun buildNavigationQuery(
        station: RetailStation,
        preferredMunicipality: String? = null,
        preferredState: BrazilianState? = null,
    ): String {
        val municipality = station.municipality.ifBlank {
            preferredMunicipality?.trim().orEmpty()
        }
        val state = station.state
        val normalizedAddress = normalizeAddress(station.address)
        val displayName = station.displayName().trim()
        val queryBody = if (isAddressSufficient(normalizedAddress)) {
            prependStationNameIfMissing(
                displayName = displayName,
                query = appendLocationIfMissing(
                    address = normalizedAddress,
                    municipality = municipality,
                    state = state,
                ),
            )
        } else {
            "${displayName}, ${formatLocationSuffix(municipality, state)}"
        }

        return appendCountry(queryBody)
    }

    internal fun prependStationNameIfMissing(displayName: String, query: String): String {
        if (displayName.isBlank()) {
            return query
        }
        if (query.contains(displayName, ignoreCase = true)) {
            return query
        }
        return "$displayName, $query"
    }

    internal fun isAddressSufficient(normalizedAddress: String): Boolean =
        normalizedAddress.length >= MIN_SUFFICIENT_ADDRESS_LENGTH

    private fun formatLocationSuffix(municipality: String, state: BrazilianState): String =
        "$municipality - ${state.abbreviation}"

    private fun appendLocationIfMissing(
        address: String,
        municipality: String,
        state: BrazilianState,
    ): String {
        val hasMunicipality = municipality.isNotBlank() &&
            address.contains(municipality, ignoreCase = true)
        val hasState = address.contains(state.abbreviation, ignoreCase = true)

        return when {
            hasMunicipality && hasState -> address
            hasMunicipality -> "$address, ${state.abbreviation}"
            else -> "$address, ${formatLocationSuffix(municipality, state)}"
        }
    }

    private fun appendCountry(query: String): String {
        val trimmed = query.trim()
        if (trimmed.endsWith("Brazil", ignoreCase = true) || trimmed.endsWith("Brasil", ignoreCase = true)) {
            return trimmed
        }
        return "$trimmed, Brazil"
    }
}
