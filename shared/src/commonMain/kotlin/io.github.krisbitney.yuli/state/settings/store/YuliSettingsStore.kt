package io.github.krisbitney.yuli.state.settings.store

import com.arkivanov.mvikotlin.core.store.Store
import io.github.krisbitney.yuli.state.settings.store.YuliSettingsStore.Intent
import io.github.krisbitney.yuli.state.settings.store.YuliSettingsStore.State

interface YuliSettingsStore : Store<Intent, State, Nothing> {

    sealed class Intent {
        data class SetLanguage(val language: String) : Intent()
    }

    data class State(
        val language: String = "english"
    )
}