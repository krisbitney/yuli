package io.github.krisbitney.yuli.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
expect fun getColorScheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false
): ColorScheme

@Composable
fun YuliTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = getColorScheme(darkTheme, dynamicColor)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

