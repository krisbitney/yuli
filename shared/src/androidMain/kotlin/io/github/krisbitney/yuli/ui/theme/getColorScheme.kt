package io.github.krisbitney.yuli.ui.theme

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import io.polywrap.ipfsdemo.ui.theme.DarkColorScheme
import io.polywrap.ipfsdemo.ui.theme.LightColorScheme

@Composable
actual fun getColorScheme(
    darkTheme: Boolean,
    dynamicColor: Boolean
): ColorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        dynamicDarkColorScheme(LocalContext.current)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
}