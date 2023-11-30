package io.github.krisbitney.yuli.state.history.store

import com.arkivanov.mvikotlin.core.store.Store
import io.github.krisbitney.yuli.models.Event
import io.github.krisbitney.yuli.state.history.store.YuliHistoryStore.Intent
import io.github.krisbitney.yuli.state.history.store.YuliHistoryStore.State

interface YuliHistoryStore : Store<Intent, State, Nothing> {

    sealed class Intent {
        data class FilterTime(val timePeriod: Event.TimePeriod) : Intent()
    }

    data class State(
        val events: List<Event> = emptyList(),
        val timePeriod: Event.TimePeriod = Event.TimePeriod.LAST_7_DAYS
    )
}