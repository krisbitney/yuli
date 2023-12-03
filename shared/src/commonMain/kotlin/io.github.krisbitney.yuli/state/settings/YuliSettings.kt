package io.github.krisbitney.yuli.state.settings

import kotlinx.coroutines.flow.StateFlow

interface YuliSettings {
    val model: StateFlow<Model>

    fun onBackClicked()

    data class Model(
        val language: String
    )

    sealed class Output {
        data object Back : Output()
    }
}