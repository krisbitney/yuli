package io.github.krisbitney.yuli.ui.history

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import io.github.krisbitney.yuli.state.history.YuliHistory

@Composable
fun HistoryScreen(component: YuliHistory) {
    val model = component.model.collectAsState()
    Text("TODO")
}