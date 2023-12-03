package io.github.krisbitney.yuli.state.settings

import io.github.krisbitney.yuli.settings.Language
import kotlinx.coroutines.flow.StateFlow

interface YuliSettings {
    val model: StateFlow<Model>

    fun onBackClicked()

    fun onLanguageChanged(language: Language)

    data class Model(
        val language: Language
    )

    sealed class Output {
        data object Back : Output()
    }
}