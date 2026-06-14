package com.example.appdortarchile20.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary          = Azul600,
    onPrimary        = Color.White,
    primaryContainer = Azul100,
    onPrimaryContainer = Azul800,

    secondary        = Verde600,
    onSecondary      = Color.White,
    secondaryContainer = Verde100,
    onSecondaryContainer = Verde700,

    tertiary         = Azul500,
    onTertiary       = Color.White,
    tertiaryContainer = Azul50,
    onTertiaryContainer = Azul700,

    background       = FondoGris,
    onBackground     = OscuroTexto,
    surface          = FondoCard,
    onSurface        = OscuroTexto,
    surfaceVariant   = Azul50,
    onSurfaceVariant = GrisTexto,

    error            = RojoError,
    onError          = Color.White,

    outline          = Color(0xFFBBCCEE),
    outlineVariant   = Color(0xFFDDE5F5),
)

private val DarkColorScheme = darkColorScheme(
    primary          = AzulClaro,
    onPrimary        = Color(0xFF001A6B),
    primaryContainer = Azul800,
    onPrimaryContainer = Azul100,

    secondary        = VerdeClaroDark,
    onSecondary      = Color(0xFF1B3A1D),
    secondaryContainer = Verde800,
    onSecondaryContainer = Verde100,

    tertiary         = AzulClaro,
    onTertiary       = Color(0xFF001A6B),

    background       = FondoOscuro,
    onBackground     = Color(0xFFE8EEF8),
    surface          = SuperfOscura,
    onSurface        = Color(0xFFE8EEF8),
    surfaceVariant   = Color(0xFF1A2540),
    onSurfaceVariant = Color(0xFFAABBDD),

    error            = Color(0xFFFF8A80),
    onError          = Color(0xFF690000),

    outline          = Color(0xFF3A4E6E),
    outlineVariant   = Color(0xFF1A2540),
)

@Composable
fun AppdortarChile20Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}