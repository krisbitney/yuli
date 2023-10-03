package io.github.krisbitney.yuli.state.home.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import io.github.krisbitney.yuli.models.User
import io.github.krisbitney.yuli.state.home.store.YuliHomeStore.Intent
import io.github.krisbitney.yuli.state.home.store.YuliHomeStore.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class YuliHomeStoreProvider(
    private val storeFactory: StoreFactory,
    private val database: Database
) {

    fun provide(): YuliHomeStore =
        object : YuliHomeStore, Store<Intent, State, Nothing> by storeFactory.create(
            name = "YuliHomeStore",
            initialState = State(),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed class Action {
        data class WithUser(val value: User) : Action()
        data object CountFollows : Action()
    }

    private sealed class Msg {
        data class SetUser(val value: User) : Msg()
        data class SetMutualsCount(val value: Long) : Msg()
        data class SetNonFollowersCount(val value: Long) : Msg()
        data class SetFansCount(val value: Long) : Msg()
        data class SetFormerConnectionsCount(val value: Long) : Msg()
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {

        override fun invoke() {
            scope.launch {
                val user = withContext(Dispatchers.IO) { database.selectUser() }
                if (user != null) {
                    dispatch(Action.WithUser(user))
                    dispatch(Action.CountFollows)
                } else {
                    // TODO: handle case where user is not logged in
                }
            }
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Nothing>() {

        override fun executeAction(action: Action, getState: () -> State) {
            when (action) {
                is Action.WithUser -> dispatch(Msg.SetUser(action.value))
                is Action.CountFollows -> scope.launch {
                    launchSubscription(database::countMutuals, Msg::SetMutualsCount)
                    launchSubscription(database::countNonfollowers, Msg::SetNonFollowersCount)
                    launchSubscription(database::countFans, Msg::SetFansCount)
                    launchSubscription(database::countFormerConnections, Msg::SetFormerConnectionsCount)
                }
            }
        }

        // TODO: implement (e.g. handle case where user is not logged in)
        override fun executeIntent(intent: Intent, getState: () -> State): Unit =
            when (intent) {
                is Intent.OpenSettings -> Unit
            }

        private suspend fun launchSubscription(
            producer: () -> Flow<Long>,
            holder: (value: Long) -> Msg
        ) = scope.launch(Dispatchers.IO) {
            producer().stateIn(this).collectLatest {
                withContext(Dispatchers.Main) {
                    dispatch(holder(it))
                }
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.SetUser -> copy(user = msg.value)
                is Msg.SetMutualsCount -> copy(mutualsCount = msg.value)
                is Msg.SetNonFollowersCount -> copy(nonfollowersCount = msg.value)
                is Msg.SetFansCount -> copy(fansCount = msg.value)
                is Msg.SetFormerConnectionsCount -> copy(formerConnectionsCount = msg.value)
            }
    }

    interface Database {
        fun selectUser(): User?

        fun countMutuals(): Flow<Long>

        fun countNonfollowers(): Flow<Long>

        fun countFans(): Flow<Long>

        fun countFormerConnections(): Flow<Long>
    }
}