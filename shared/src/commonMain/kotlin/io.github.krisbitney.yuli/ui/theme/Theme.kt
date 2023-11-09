package io.github.krisbitney.yuli.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun YuliTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = getColorScheme(darkTheme, dynamicColor),
        shapes = shapes,
        typography = getTypography(),
        content = content
    )
}

