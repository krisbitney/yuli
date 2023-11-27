package io.github.krisbitney.yuli.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
expect fun getColorScheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false
): ColorScheme

val LightColorScheme = lightColorScheme(
    primary = Color(0xFFFBEFE3), // light creamy background
    onPrimary = Color(0xFF0C253B), // dark greenish-bluish text
    primaryContainer = Color(0xFF809790), // dark green button color
    onPrimaryContainer = Color(0xFFFEFEFD), // slightly off-white text
    inversePrimary = Color(0xFFE8C9B6),
    secondary = Color(0xFFEBCCB9),
    onSecondary = Color(0xFFFEE2D5),
    secondaryContainer = Color(0xFFFEEDF),
    onSecondaryContainer = Color(0xFFEECFBE),
    tertiary = Color(0xFFEACAB7),
    onTertiary = Color(0xFFE8DB),
    tertiaryContainer = Color(0xFFEDCEBD),
    onTertiaryContainer = Color(0xFFE6C7B3),
    background = Color(0xFFCFD1C7), // light greenish background
    onBackground = Color(0xFFFEFEFD), // slightly off-white text
    surface = Color(0xFFFBEFE3), // light creamy background
    onSurface = Color(0xFF0C253B), // dark greenish-bluish text
    surfaceVariant = Color(0xFFE4C5B2),
    onSurfaceVariant = Color(0xFFFBD5CE),
    surfaceTint = Color(0xFFFEE3D6),
    inverseSurface = Color(0xFFE7DA),
    inverseOnSurface = Color(0xFFEEADF),
    error = Color(0xFFBA1B1B),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFDE0E0),
    onErrorContainer = Color(0xFF410001),
    outline = Color.Black.copy(alpha = 0.1f), // semi-transparent black
    outlineVariant = Color(0xFF5F5F5F),
    scrim = Color(0xBF000000)
)

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF1F2937),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF293B4D),
    onPrimaryContainer = Color(0xFFFFFFFF),
    inversePrimary = Color(0xFF1F2937),
    secondary = Color(0xFF374151),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFF485A6E),
    onSecondaryContainer = Color(0xFFFFFFFF),
    tertiary = Color(0xFF4B5563),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFF5F6B7B),
    onTertiaryContainer = Color(0xFFFFFFFF),
    background = Color(0xFF111827),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF1F2937),
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF293B4D),
    onSurfaceVariant = Color(0xFFFFFFFF),
    surfaceTint = Color(0xFF2D3B4F),
    inverseSurface = Color(0xFF1F2937),
    inverseOnSurface = Color(0xFFFFFFFF),
    error = Color(0xFFDC2626),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFECACA),
    onErrorContainer = Color(0xFF7F1D1D),
    outline = Color(0xFF6B7280),
    outlineVariant = Color(0xFF9CA3AF),
    scrim = Color(0xBF000000)
)