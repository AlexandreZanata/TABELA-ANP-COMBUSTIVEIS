package com.anpfuel.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = AnpBlue,
    onPrimary = ColorTokens.White,
    primaryContainer = ColorTokens.BlueContainerLight,
    onPrimaryContainer = AnpBlueDark,
    secondary = AnpGreen,
    onSecondary = ColorTokens.White,
    secondaryContainer = ColorTokens.GreenContainerLight,
    onSecondaryContainer = AnpGreenDark,
)

private val DarkColorScheme = darkColorScheme(
    primary = ColorTokens.BlueLight,
    onPrimary = AnpBlueDark,
    primaryContainer = AnpBlueDark,
    onPrimaryContainer = ColorTokens.BlueContainerLight,
    secondary = ColorTokens.GreenLight,
    onSecondary = AnpGreenDark,
    secondaryContainer = AnpGreenDark,
    onSecondaryContainer = ColorTokens.GreenContainerLight,
)

@Composable
fun AnpFuelTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}

private object ColorTokens {
    val White = androidx.compose.ui.graphics.Color.White
    val BlueLight = androidx.compose.ui.graphics.Color(0xFF90CAF9)
    val BlueContainerLight = androidx.compose.ui.graphics.Color(0xFFBBDEFB)
    val GreenLight = androidx.compose.ui.graphics.Color(0xFFA5D6A7)
    val GreenContainerLight = androidx.compose.ui.graphics.Color(0xFFC8E6C9)
}
