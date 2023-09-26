package io.github.krisbitney.yuli

import androidx.compose.runtime.Composable
import io.github.krisbitney.yuli.ui.home.HomeScreen
import io.github.krisbitney.yuli.ui.theme.YuliTheme

@Composable
fun App() {
    YuliTheme {
        HomeScreen()
    }
}
