package com.anpfuel.data.remote

/**
 * Parses optional ANP operational warnings attached to a week block (Phase 12.2.3).
 * Examples: "NOTA: …", "Aviso: …"
 */
internal object AnpListingOperationalNoteParser {

    private val OPERATIONAL_NOTE_PATTERN = Regex(
        """^\s*(NOTA|Aviso)\s*:\s*(.+)\s*$""",
        setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL),
    )

    fun parseOperationalNote(text: String): String? {
        val normalized = text.replace(Regex("\\s+"), " ").trim()
        val match = OPERATIONAL_NOTE_PATTERN.find(normalized) ?: return null
        return match.groupValues[2].trim().takeIf { it.isNotBlank() }
    }
}
