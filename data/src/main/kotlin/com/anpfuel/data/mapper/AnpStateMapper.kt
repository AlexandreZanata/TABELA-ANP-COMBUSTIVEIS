package com.anpfuel.data.mapper

import com.anpfuel.domain.valueobject.BrazilianState
import java.text.Normalizer

/**
 * Maps ANP state labels (abbreviation or Portuguese name) to [BrazilianState].
 */
object AnpStateMapper {

    private val DIACRITIC_MARKS_REGEX = Regex("\\p{M}+")

    private val ANP_PORTUGUESE_LABELS = mapOf(
        "Distrito Federal" to BrazilianState.DISTRICT_FEDERAL,
        "Espírito Santo" to BrazilianState.ESPIRITO_SANTO,
        "Goiás" to BrazilianState.GOIAS,
        "Maranhão" to BrazilianState.MARANHAO,
        "Pará" to BrazilianState.PARA,
        "Paraíba" to BrazilianState.PARAIBA,
        "Paraná" to BrazilianState.PARANA,
        "Piauí" to BrazilianState.PIAUI,
        "Rondônia" to BrazilianState.RONDONIA,
        "São Paulo" to BrazilianState.SAO_PAULO,
        "Amapá" to BrazilianState.AMAPA,
        "Ceará" to BrazilianState.CEARA,
    )

    private val labelToState: Map<String, BrazilianState> = buildMap {
        BrazilianState.entries.forEach { state ->
            put(normalizeLabel(state.abbreviation), state)
            put(normalizeLabel(state.name.replace('_', ' ')), state)
        }
        ANP_PORTUGUESE_LABELS.forEach { (label, state) ->
            put(normalizeLabel(label), state)
        }
    }

    fun toBrazilianState(rawLabel: String): BrazilianState? =
        labelToState[normalizeLabel(rawLabel)]

    fun toBrazilianStateOrThrow(rawLabel: String): BrazilianState =
        toBrazilianState(rawLabel)
            ?: error("Unknown ANP state label: $rawLabel")

    fun toAbbreviation(rawLabel: String): String = toBrazilianStateOrThrow(rawLabel).abbreviation

    private fun normalizeLabel(raw: String): String =
        Normalizer.normalize(raw.trim(), Normalizer.Form.NFD)
            .replace(DIACRITIC_MARKS_REGEX, "")
            .uppercase()
}
