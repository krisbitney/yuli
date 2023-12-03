package io.github.krisbitney.yuli.state.settings.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import io.github.krisbitney.yuli.state.settings.store.YuliSettingsStore.Intent
import io.github.krisbitney.yuli.state.settings.store.YuliSettingsStore.State

class YuliSettingsStoreProvider(private val storeFactory: StoreFactory) {

    fun provide(): YuliSettingsStore =
        object : YuliSettingsStore, Store<Intent, State, Nothing> by storeFactory.create(
            name = "YuliSettingsStore",
            initialState = State(),
            bootstrapper = SimpleBootstrapper(Unit),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed class Msg {
        data class SetLanguage(val language: String) : Msg()
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Unit, State, Msg, Nothing>() {

        override fun executeIntent(
            intent: Intent,
            getState: () -> State
        ): Unit =
            when (intent) {
                is Intent.SetLanguage -> dispatch(Msg.SetLanguage(intent.language))
            }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.SetLanguage -> copy(language = msg.language)
            }
    }
}