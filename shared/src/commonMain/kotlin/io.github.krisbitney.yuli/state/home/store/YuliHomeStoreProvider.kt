package io.github.krisbitney.yuli.state.home.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import io.github.krisbitney.yuli.models.FollowType
import io.github.krisbitney.yuli.models.User
import io.github.krisbitney.yuli.repository.ApiHandler
import io.github.krisbitney.yuli.state.home.store.YuliHomeStore.Intent
import io.github.krisbitney.yuli.state.home.store.YuliHomeStore.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

internal class YuliHomeStoreProvider(
    private val storeFactory: StoreFactory,
    private val database: Database,
    private val apiHandler: ApiHandler
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
        data class SetFormerFollowsCount(val value: Long) : Msg()
        data class SetUpdateInProgress(val value: Boolean) : Msg()
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {

        override fun invoke() {
            scope.launch {
                val user = withContext(Dispatchers.IO) { database.selectUser() }
                if (user != null) {
                    dispatch(Action.WithUser(user))
                    dispatch(Action.CountFollows)
                }
            }
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Nothing>() {

        override fun executeAction(action: Action, getState: () -> State) {
            when (action) {
                is Action.WithUser -> dispatch(Msg.SetUser(action.value))
                is Action.CountFollows -> scope.launch {
                    launchSubscription({ database.countFollows(FollowType.MUTUAL) }, Msg::SetMutualsCount)
                    launchSubscription({ database.countFollows(FollowType.NONFOLLOWER) }, Msg::SetNonFollowersCount)
                    launchSubscription({ database.countFollows(FollowType.FAN) }, Msg::SetFansCount)
                    launchSubscription({ database.countFollows(FollowType.FORMER) }, Msg::SetFormerFollowsCount)
                }
            }
        }

        override fun executeIntent(intent: Intent, getState: () -> State): Unit =
            when (intent) {
                is Intent.OpenSettings -> Unit
                is Intent.RefreshFollowsData -> refreshFollowsData()
                is Intent.SetIsUpdating -> {
                    dispatch(Msg.SetUpdateInProgress(intent.isUpdating))
                    if (intent.isUpdating) {
                        watchLastUpdate()
                    }
                    Unit
                }
            }

        private fun refreshFollowsData() {
            scope.launch {
                dispatch(Msg.SetUpdateInProgress(true))
                apiHandler.inBackground.updateFollowsAndNotify()
                watchLastUpdate()
            }
        }

        // TODO: this needs testing to make sure there is no race condition where the background update happens too quickly
        private fun watchLastUpdate() = scope.launch {
            withContext(Dispatchers.IO) {
                val lastUpdate = database.watchLastUpdate()
                var startUpdateTime = 0L
                withTimeoutOrNull(600_000) {
                    lastUpdate.collect {
                        // the first value should be the prior update time
                        if (startUpdateTime == 0L) {
                            startUpdateTime = it
                        // subsequent values should be the current update time
                        } else {
                            if (it > startUpdateTime) {
                                withContext(Dispatchers.Main) {
                                    dispatch(Msg.SetUpdateInProgress(false))
                                }
                                cancel()
                            }
                        }
                    }
                }
            }
        }

        private suspend fun launchSubscription(
            producer: suspend () -> Flow<Long>,
            holder: (value: Long) -> Msg
        ) = scope.launch(Dispatchers.IO) {
            producer().stateIn(this).collectLatest {
                withContext(Dispatchers.Main) {
                    dispatch(holder(it))
                    dispatch(Msg.SetUpdateInProgress(false))
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
                is Msg.SetFormerFollowsCount -> copy(formerFollowsCount = msg.value)
                is Msg.SetUpdateInProgress -> copy(updateInProgress = msg.value)
            }
    }

    interface Database {
        suspend fun selectUser(): User?

        suspend fun countFollows(type: FollowType): Flow<Long>

        suspend fun watchLastUpdate(): Flow<Long>
    }
}