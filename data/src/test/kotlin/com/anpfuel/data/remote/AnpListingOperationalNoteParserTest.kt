package com.anpfuel.data.remote

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class AnpListingOperationalNoteParserTest {

    @Test
    fun parsesNotaPrefix() {
        val note = AnpListingOperationalNoteParser.parseOperationalNote(
            "NOTA: Os preços médios de Belo Horizonte não foram publicados entre 26/04/2026 e 16/05/2026.",
        )

        assertEquals(
            "Os preços médios de Belo Horizonte não foram publicados entre 26/04/2026 e 16/05/2026.",
            note,
        )
    }

    @Test
    fun parsesAvisoPrefixCaseInsensitive() {
        val note = AnpListingOperationalNoteParser.parseOperationalNote(
            "aviso: Correção de preços em Goiatuba para a semana de 21/05/2023 a 27/05/2023.",
        )

        assertEquals(
            "Correção de preços em Goiatuba para a semana de 21/05/2023 a 27/05/2023.",
            note,
        )
    }

    @Test
    fun ignoresRegularParagraphText() {
        assertNull(
            AnpListingOperationalNoteParser.parseOperationalNote(
                "Preços médios semanais: Brasil, regiões, estados e municípios",
            ),
        )
    }
}
