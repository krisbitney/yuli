package io.github.krisbitney.yuli.state.history.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import io.github.krisbitney.yuli.models.Event
import io.github.krisbitney.yuli.state.history.store.YuliHistoryStore.Intent
import io.github.krisbitney.yuli.state.history.store.YuliHistoryStore.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class YuliHistoryStoreProvider(
    private val storeFactory: StoreFactory,
    private val database: Database
) {

    fun provide(): YuliHistoryStore =
        object : YuliHistoryStore, Store<Intent, State, Nothing> by storeFactory.create(
                name = "YuliHistoryStore",
                initialState = State(),
                bootstrapper = SimpleBootstrapper(Unit),
                executorFactory = ::ExecutorImpl,
                reducer = ReducerImpl
            ) {}

    private sealed class Msg {
        data class SetEvents(val events: List<Event>, val timePeriod: Event.TimePeriod) : Msg()
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Unit, State, Msg, Nothing>() {

        override fun executeAction(action: Unit, getState: () -> State) {
            setEvents(getState().timePeriod)
        }

        override fun executeIntent(
            intent: Intent,
            getState: () -> State
        ): Unit =
            when (intent) {
                is Intent.FilterTime -> {
                    setEvents(intent.timePeriod)
                    Unit
                }
            }

        private fun setEvents(timePeriod: Event.TimePeriod) = scope.launch {
            val events = withContext(Dispatchers.IO) { database.selectEvents(timePeriod) }
            dispatch(Msg.SetEvents(events, timePeriod))
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.SetEvents -> copy(events = msg.events, timePeriod = msg.timePeriod)
            }
    }

    interface Database {
        suspend fun selectEvents(timePeriod: Event.TimePeriod): List<Event>
    }
}