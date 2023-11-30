package io.github.krisbitney.yuli.state.history

import io.github.krisbitney.yuli.models.Event
import kotlinx.coroutines.flow.StateFlow

interface YuliHistory {
    val model: StateFlow<Model>

    fun onBackClicked()

    fun onTimePeriodClicked(timePeriod: Event.TimePeriod)

    data class Model(
        val events: List<Event>
    )

    sealed class Output {
        data object Back : Output()
    }
}