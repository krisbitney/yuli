package io.github.krisbitney.yuli.state.login.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import io.github.krisbitney.yuli.models.User
import io.github.krisbitney.yuli.repository.ApiHandler
import io.github.krisbitney.yuli.state.login.store.YuliLoginStore.Intent
import io.github.krisbitney.yuli.state.login.store.YuliLoginStore.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

internal class YuliLoginStoreProvider(
    private val storeFactory: StoreFactory,
    private val database: Database,
    private val apiHandler: ApiHandler
) {

    fun provide(): YuliLoginStore =
        object : YuliLoginStore, Store<Intent, State, Nothing> by storeFactory.create(
            name = "YuliLoginStore",
            initialState = State(),
            bootstrapper = SimpleBootstrapper(Unit),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed class Msg {
        data class SetUsername(val username: String?) : Msg()
        data class SetLoginAttempt(val isLoggedIn: Boolean, val errorMsg: String?) : Msg()
        data class SetIsLoading(val isLoading: Boolean) : Msg()
        data class SetIsChallenge(val isChallenge: Boolean) : Msg()
        data class SetChallenge(val challenge: String?) : Msg()
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Unit, State, Msg, Nothing>() {

        override fun executeAction(action: Unit, getState: () -> State) {
            scope.launch {
                val currentUser = withContext(Dispatchers.IO) { database.selectUser() }
                if (currentUser != null && currentUser.username.isNotEmpty()) {
                    dispatch(Msg.SetUsername(currentUser.username))
                }
            }
        }

        override fun executeIntent(intent: Intent, getState: () -> State): Unit =
            when (intent) {
                is Intent.Login -> {
                    dispatch(Msg.SetIsLoading(true))
                    dispatch(Msg.SetLoginAttempt(false, null))
                    login(intent.username, intent.password, getState)
                }
                is Intent.SetUsername -> dispatch(Msg.SetUsername(intent.username))
                is Intent.SetChallenge -> dispatch(Msg.SetChallenge(intent.challenge))
            }

        private fun login(username: String, password: String, getState: () -> State) {
            scope.launch {
                val result = withContext(Dispatchers.IO) {
                    apiHandler.createSession(username, password) { onChallenge(getState) }
                }
                dispatch(Msg.SetLoginAttempt(result.isSuccess, result.exceptionOrNull()?.message))
                dispatch(Msg.SetIsLoading(false))
            }
        }

        private fun onChallenge(getState: () -> State): String = runBlocking(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                dispatch(Msg.SetIsChallenge(true))
            }
            while (getState().challenge == null) {
                delay(100L)
            }
            val challenge = getState().challenge?.trim()
            withContext(Dispatchers.Main) {
                dispatch(Msg.SetIsChallenge(false))
                dispatch(Msg.SetChallenge(null))
            }
            challenge ?: ""
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.SetUsername -> copy(username = msg.username)
                is Msg.SetLoginAttempt -> copy(
                    isLoggedIn = msg.isLoggedIn,
                    errorMsg = msg.errorMsg
                )
                is Msg.SetIsLoading -> copy(isLoading = msg.isLoading)
                is Msg.SetIsChallenge -> copy(isChallenge = msg.isChallenge)
                is Msg.SetChallenge -> copy(challenge = msg.challenge)
            }
    }

    interface Database {
        suspend fun selectUser(): User?
    }
}