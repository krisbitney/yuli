package io.github.krisbitney.yuli.state.follows.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import io.github.krisbitney.yuli.models.FollowType
import io.github.krisbitney.yuli.models.Profile
import io.github.krisbitney.yuli.state.follows.store.YuliFollowsStore.Intent
import io.github.krisbitney.yuli.state.follows.store.YuliFollowsStore.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class YuliFollowsStoreProvider(
    private val storeFactory: StoreFactory,
    private val database: Database,
    private val type: FollowType
) {

    fun provide(): YuliFollowsStore =
        object : YuliFollowsStore, Store<Intent, State, Nothing> by storeFactory.create(
            name = "YuliFollowsStore",
            initialState = State(),
            bootstrapper = SimpleBootstrapper(Unit),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed class Msg {
        data class SetFollows(
            val profiles: List<Profile>,
            val sortedBy: Profile.SortBy
        ) : Msg()
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Unit, State, Msg, Nothing>() {

        override fun executeAction(action: Unit, getState: () -> State) {
            scope.launch {
                // TODO: observe follows?
                val follows = withContext(Dispatchers.IO) { database.selectProfiles(type) }
                val state = getState()
                val sorted = follows.sortedBy {
                    when (state.sortedBy) {
                        Profile.SortBy.USERNAME -> it.username
                        Profile.SortBy.NAME -> it.name
                    }
                }
                dispatch(Msg.SetFollows(sorted, state.sortedBy))
            }
        }

        override fun executeIntent(intent: Intent, getState: () -> State): Unit =
            when (intent) {
                is Intent.Sort -> {
                    val sorted = getState().follows.sortedBy {
                        when (intent.sortBy) {
                            Profile.SortBy.USERNAME -> it.username
                            Profile.SortBy.NAME -> it.name
                        }
                    }
                    dispatch(Msg.SetFollows(sorted, intent.sortBy))
                }
            }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.SetFollows -> copy(follows = msg.profiles, sortedBy = msg.sortedBy)
            }
    }

    interface Database {
        suspend fun selectProfiles(type: FollowType): List<Profile>
    }
}