package com.anpfuel.domain.rule

import java.text.Normalizer

/**
 * Accent-insensitive normalization for municipality FTS ranking (BR-017).
 */
object MunicipalitySearchTextNormalizer {

    fun normalize(text: String): String =
        Normalizer.normalize(text.trim(), Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")
            .uppercase()
}
