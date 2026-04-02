package com.example.praktam_2417051056.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Indigo50  = Color(0xFFEEF2FF)
val Indigo100 = Color(0xFFE0E7FF)
val Indigo200 = Color(0xFFC7D2FE)
val Indigo400 = Color(0xFF818CF8)
val Indigo500 = Color(0xFF6366F1)
val Indigo600 = Color(0xFF4F46E5)
val Indigo700 = Color(0xFF4338CA)
val Indigo900 = Color(0xFF312E81)

val Violet400 = Color(0xFFA78BFA)
val Violet600 = Color(0xFF7C3AED)
val Violet900 = Color(0xFF4C1D95)

val Green400  = Color(0xFF4ADE80)
val Green500  = Color(0xFF22C55E)

val Amber500  = Color(0xFFF59E0B)

val Red500    = Color(0xFFEF4444)

val Slate50   = Color(0xFFF8FAFC)
val Slate100  = Color(0xFFF1F5F9)
val Slate200  = Color(0xFFE2E8F0)
val Slate400  = Color(0xFF94A3B8)
val Slate500  = Color(0xFF64748B)
val Slate700  = Color(0xFF334155)
val Slate800  = Color(0xFF1E293B)
val Slate900  = Color(0xFF0F172A)

val DarkSurface   = Color(0xFF1A1830)
val DarkBackground = Color(0xFF0F0E1A)
val DarkCard      = Color(0xFF1E1D2E)
val DarkBorder    = Color(0xFF2A2840)

private val DailyDoLightColorScheme = lightColorScheme(
    primary          = Indigo600,
    onPrimary        = Color.White,
    primaryContainer = Indigo100,
    onPrimaryContainer = Indigo900,

    secondary        = Violet600,
    onSecondary      = Color.White,
    secondaryContainer = Violet400.copy(alpha = 0.2f),
    onSecondaryContainer = Violet900,

    tertiary         = Green500,
    onTertiary       = Color.White,

    background       = Slate50,
    onBackground     = Slate800,
    surface          = Color.White,
    onSurface        = Slate800,
    surfaceVariant   = Slate100,
    onSurfaceVariant = Slate500,

    outline          = Slate200,
    outlineVariant   = Slate100,

    error            = Red500,
    onError          = Color.White,
)

private val DailyDoDarkColorScheme = darkColorScheme(
    primary          = Indigo400,
    onPrimary        = Indigo900,
    primaryContainer = Indigo700,
    onPrimaryContainer = Indigo100,

    secondary        = Violet400,
    onSecondary      = Violet900,

    tertiary         = Green400,
    onTertiary       = Color.Black,

    background       = DarkBackground,
    onBackground     = Slate100,
    surface          = DarkSurface,
    onSurface        = Slate100,
    surfaceVariant   = DarkCard,
    onSurfaceVariant = Slate400,

    outline          = DarkBorder,
    outlineVariant   = DarkBorder.copy(alpha = 0.5f),

    error            = Red500,
    onError          = Color.White,
)

@Composable
fun PrakTAM_2417051056Theme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DailyDoDarkColorScheme else DailyDoLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = DailyDoTypography,
        shapes      = DailyDoShapes,
        content     = content
    )
}