package com.anpfuel.app.locale

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AppLocalesTest {

    @Test
    fun exposesEightLocalesInGridOrder() {
        assertEquals(8, AppLocales.all.size)
        assertEquals(
            listOf("pt-BR", "en", "es", "fr", "zh-CN", "ru", "de", "ja"),
            AppLocales.supportedLocaleTags,
        )
    }

    @Test
    fun migratesRetiredLocaleTags() {
        assertEquals("ja", AppLocales.normalizeLocaleTag("hi"))
        assertEquals("de", AppLocales.normalizeLocaleTag("ar"))
        assertEquals("ru", AppLocales.normalizeLocaleTag("bn"))
    }
}
