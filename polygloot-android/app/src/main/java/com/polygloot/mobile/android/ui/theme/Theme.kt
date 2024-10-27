package com.polygloot.mobile.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

data object Dimensions {
    val small = 8.dp
    val medium = 16.dp
    val large = 24.dp
}

val LightColorScheme = lightColorScheme(
    primary = AccentPurple,
    onPrimary = Color.White,
    background = NeutralGrayLight,
    onBackground = TextGrayLight,
    surface = NeutralGrayLight,
    onSurface = TextGrayLight,
    secondary = AccentOrange,
    onSecondary = Color.White
)

val DarkColorScheme = darkColorScheme(
    primary = AccentOrange,
    onPrimary = Color.Black,
    background = NeutralGrayDark,
    onBackground = TextGrayDark,
    surface = NeutralGrayDark,
    onSurface = TextGrayDark,
    secondary = AccentPurple,
    onSecondary = Color.Black
)

@Composable
fun PolyglootTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}