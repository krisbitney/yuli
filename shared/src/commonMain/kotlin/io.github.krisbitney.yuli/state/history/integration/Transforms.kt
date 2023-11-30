package io.github.krisbitney.yuli.state.history.integration

import io.github.krisbitney.yuli.state.history.YuliHistory
import io.github.krisbitney.yuli.state.history.store.YuliHistoryStore

internal val stateToModel: (YuliHistoryStore.State) -> YuliHistory.Model = {
    YuliHistory.Model(
        events = it.events,
        timePeriod = it.timePeriod
    )
}