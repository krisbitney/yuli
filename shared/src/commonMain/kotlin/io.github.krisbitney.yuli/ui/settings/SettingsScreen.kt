package io.github.krisbitney.yuli.ui.settings

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import io.github.krisbitney.yuli.state.settings.YuliSettings

@Composable
fun SettingsScreen(component: YuliSettings) {
    val model = component.model.collectAsState()
    Text("TODO: Settings screen")
}