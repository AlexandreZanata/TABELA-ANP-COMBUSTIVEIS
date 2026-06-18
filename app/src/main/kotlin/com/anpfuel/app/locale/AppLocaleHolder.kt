package com.anpfuel.app.locale

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

/**
 * Holds the active app locale tag for [AppLocaleApplier] (UC-008).
 */
object AppLocaleHolder {
    var localeTag: String = DEFAULT_LOCALE_TAG

    const val DEFAULT_LOCALE_TAG = "en"
    const val PORTUGUESE_BRAZIL_TAG = "pt-BR"

    val supportedLocaleTags: List<String> = listOf(DEFAULT_LOCALE_TAG, PORTUGUESE_BRAZIL_TAG)
}

object AppLocaleApplier {

    fun wrap(context: Context, localeTag: String): Context {
        val locale = Locale.forLanguageTag(localeTag)
        Locale.setDefault(locale)
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }
}
