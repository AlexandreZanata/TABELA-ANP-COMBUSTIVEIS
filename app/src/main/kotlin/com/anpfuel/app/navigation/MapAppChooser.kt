package com.anpfuel.app.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri

sealed interface MapNavigationResult {
    data object Launched : MapNavigationResult
    data object NoAppFound : MapNavigationResult
}

/**
 * UC-013 — Opens system geo chooser (Maps, Waze, etc.) for a normalized station query.
 */
object MapAppChooser {

    fun openNavigation(context: Context, query: String): MapNavigationResult {
        val geoUri = Uri.parse("geo:0,0?q=${Uri.encode(query)}")
        val intent = Intent(Intent.ACTION_VIEW, geoUri)
        if (intent.resolveActivity(context.packageManager) == null) {
            return MapNavigationResult.NoAppFound
        }
        context.startActivity(Intent.createChooser(intent, null))
        return MapNavigationResult.Launched
    }
}
