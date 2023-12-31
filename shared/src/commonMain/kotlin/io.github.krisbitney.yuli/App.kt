package io.github.krisbitney.yuli

import androidx.compose.runtime.Composable
import io.github.krisbitney.yuli.state.YuliRoot
import io.github.krisbitney.yuli.ui.RootContent
import io.github.krisbitney.yuli.ui.theme.YuliTheme

@Composable
fun App(component: YuliRoot) {
    YuliTheme {
        RootContent(component)
    }
}

// TODO: error message localization
// TODO: notification UI
// TODO: setting: clear former follows
// TODO: setting: dark mode
// TODO: setting: data update interval
// TODO: setting: use default portrait instead of own pic, or allow upload pic
