package io.github.krisbitney.yuli.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
actual fun getColorScheme(
    darkTheme: Boolean,
    dynamicColor: Boolean
): ColorScheme = when {
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
}