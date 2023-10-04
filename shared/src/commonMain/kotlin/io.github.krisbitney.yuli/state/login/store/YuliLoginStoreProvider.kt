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
import kotlinx.coroutines.launch
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
        data class SetUsername(val username: String) : Msg()
        data class SuccessfulLogin(val user: User) : Msg()
        data class FailedLogin(val message: String) : Msg()
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
                is Intent.Login -> login(intent.username, intent.password)
            }

        fun login(username: String, password: String) {
            scope.launch {
                val result = withContext(Dispatchers.IO) { apiHandler.createSession(username, password) }
                if (result.isSuccess) {
                    dispatch(Msg.SuccessfulLogin(result.getOrThrow()))
                } else {
                    val message = result.exceptionOrNull()!!.message ?: "Unknown error occurred"
                    dispatch(Msg.FailedLogin(message))
                }
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.SetUsername -> copy(username = msg.username)
                is Msg.SuccessfulLogin -> copy(loginUser = msg.user)
                is Msg.FailedLogin -> copy(errorMsg = msg.message)
            }
    }

    interface Database {
        fun selectUser(): User?
    }
}