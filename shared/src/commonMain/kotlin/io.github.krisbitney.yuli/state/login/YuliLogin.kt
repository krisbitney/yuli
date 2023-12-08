package io.github.krisbitney.yuli.state.login

import kotlinx.coroutines.flow.StateFlow

interface YuliLogin {
    val model: StateFlow<Model>

    fun onLoginClicked(username: String, password: String)

    fun onCloseClicked()

    fun onSubmitChallenge(challenge: String)

    data class Model(
        val username: String? = null,
        val isLoggedIn: Boolean = false,
        val errorMsg: String? = null,
        val isLoading: Boolean = false,
        val isChallenge: Boolean = false
    )

    sealed class Output {
        data class Close(val isUpdating: Boolean) : Output()
    }
}