package io.github.krisbitney.yuli.state.login.store

import com.arkivanov.mvikotlin.core.store.Store
import io.github.krisbitney.yuli.state.login.store.YuliLoginStore.Intent
import io.github.krisbitney.yuli.state.login.store.YuliLoginStore.State

internal interface YuliLoginStore : Store<Intent, State, Nothing> {

    sealed class Intent {
        data class Login(val username: String, val password: String) : Intent()
        data class SetUsername(val username: String?) : Intent()
        data class SetChallenge(val challenge: String?) : Intent()
    }

    data class State(
        val username: String? = null,
        val isLoggedIn: Boolean = false,
        val errorMsg: String? = null,
        val isLoading: Boolean = false,
        val isChallenge: Boolean = false,
        val challenge: String? = null,
    )
}