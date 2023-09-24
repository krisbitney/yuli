package io.github.krisbitney.yuli.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import io.polywrap.ipfsdemo.ui.theme.DarkColorScheme
import io.polywrap.ipfsdemo.ui.theme.LightColorScheme

@Composable
actual fun getColorScheme(
    darkTheme: Boolean,
    dynamicColor: Boolean
): ColorScheme = when {
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
}