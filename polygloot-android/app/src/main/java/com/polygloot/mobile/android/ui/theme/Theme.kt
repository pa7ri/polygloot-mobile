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


val AccentPurple = Color(0xFF6200EE)
val AccentPurpleLight = Color(0xFFBB86FC)
val AccentOrange = Color(0xFFFF5722)
val AccentOrangeLight = Color(0xFFFFAB91)

val LightColorScheme = lightColorScheme(
    primary = AccentPurple,
    onPrimary = Color.White,
    primaryContainer = AccentPurpleLight,
    onPrimaryContainer = Color.Black,
    secondary = AccentOrange,
    onSecondary = Color.White,
    secondaryContainer = AccentOrangeLight,
    onSecondaryContainer = Color.Black,
    background = Color(0xFFF5F5F5),
    onBackground = Color(0xFF000000),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF000000),
    surfaceVariant = Color(0xFFE0E0E0),
    onSurfaceVariant = Color(0xFF000000),
    error = Color(0xFFB00020),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD4),
    onErrorContainer = Color(0xFF410002),
    outline = Color(0xFF737373),
    outlineVariant = Color(0xFFBDBDBD),
    inverseOnSurface = Color(0xFFE0E0E0),
    inverseSurface = Color(0xFF303030),
    inversePrimary = AccentPurpleLight,
    surfaceTint = AccentPurple,
    scrim = Color.Black
)

val DarkColorScheme = darkColorScheme(
    primary = AccentPurpleLight,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF3700B3),
    onPrimaryContainer = Color.White,
    secondary = AccentOrangeLight,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFDD2C00),
    onSecondaryContainer = Color.White,
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF121212),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF303030),
    onSurfaceVariant = Color(0xFFE0E0E0),
    error = Color(0xFFCF6679),
    onError = Color.Black,
    errorContainer = Color(0xFF8B0000),
    onErrorContainer = Color.White,
    outline = Color(0xFFBDBDBD),
    outlineVariant = Color(0xFF737373),
    inverseOnSurface = Color(0xFF121212),
    inverseSurface = Color(0xFFE0E0E0),
    inversePrimary = AccentPurple,
    surfaceTint = AccentPurpleLight,
    scrim = Color.Black
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