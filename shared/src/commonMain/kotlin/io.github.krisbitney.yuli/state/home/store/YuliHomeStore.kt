package io.github.krisbitney.yuli.state.home.store

import com.arkivanov.mvikotlin.core.store.Store
import io.github.krisbitney.yuli.models.User
import io.github.krisbitney.yuli.state.home.store.YuliHomeStore.Intent
import io.github.krisbitney.yuli.state.home.store.YuliHomeStore.State

internal interface YuliHomeStore : Store<Intent, State, Nothing> {

    sealed class Intent {
        data object OpenSettings : Intent()
        data object RefreshFollowsData : Intent()
        data class SetUpdateInProgress(val value: Boolean) : Intent()
    }

    data class State(
        val user: User? = null,
        val mutualsCount: Long = 0L,
        val nonfollowersCount: Long = 0L,
        val fansCount: Long = 0L,
        val formerConnectionsCount: Long = 0L,
        val updateInProgress: Boolean = false
    )
}