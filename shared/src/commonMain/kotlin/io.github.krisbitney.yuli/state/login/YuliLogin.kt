package io.github.krisbitney.yuli.state.login

import kotlinx.coroutines.flow.StateFlow

interface YuliLogin {
    val model: StateFlow<Model>
    var usernameInput: String
    var passwordInput: String

    fun onLoginClicked(username: String, password: String)

    fun onCloseClicked()

    data class Model(
        val username: String? = null,
        val isLoggedIn: Boolean = false,
        val errorMsg: String? = null,
        val isLoading: Boolean = false
    )

    sealed class Output {
        data class Close(val isUpdating: Boolean) : Output()
    }
}