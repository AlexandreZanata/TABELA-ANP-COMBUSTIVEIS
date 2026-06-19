package com.anpfuel.data.remote

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.LocalDate

class AnpListingUpdatedAtParserTest {

    @Test
    fun parsesParenthesizedUpdatedAtSuffix() {
        val date = AnpListingUpdatedAtParser.parseUpdatedAt(
            "Preços médios semanais (Atualizado em 12/6/2026)",
        )

        assertEquals(LocalDate.of(2026, 6, 12), date)
    }

    @Test
    fun parsesSingleDigitDayAndMonth() {
        val date = AnpListingUpdatedAtParser.parseUpdatedAt(
            "Preços por posto (Atualizado em 11/6/2026)",
        )

        assertEquals(LocalDate.of(2026, 6, 11), date)
    }

    @Test
    fun ignoresTextWithoutUpdatedAtSuffix() {
        assertNull(AnpListingUpdatedAtParser.parseUpdatedAt("Preços médios semanais"))
    }
}
