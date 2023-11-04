package io.github.krisbitney.yuli.state.follows.store

import com.arkivanov.mvikotlin.core.store.Store
import io.github.krisbitney.yuli.models.Profile
import io.github.krisbitney.yuli.state.follows.store.YuliFollowsStore.Intent
import io.github.krisbitney.yuli.state.follows.store.YuliFollowsStore.State

internal interface YuliFollowsStore : Store<Intent, State, Nothing> {

    sealed class Intent {
        data class Sort(val sortBy: Profile.SortBy) : Intent()
    }

    data class State(
        val follows: List<Profile> = emptyList(),
        val sortedBy: Profile.SortBy = Profile.SortBy.NAME
    )
}