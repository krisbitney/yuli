package io.github.krisbitney.yuli.state.login

import kotlinx.coroutines.flow.StateFlow

interface YuliLogin {
    val model: StateFlow<Model>
    var showWarning: Boolean
    var usernameInput: String
    var passwordInput: String

    fun onLoginClicked(username: String, password: String)

    fun onCloseClicked()

    fun showConfirmation()

    fun onConfirmationClosed()

    data class Model(
        val username: String? = null,
        val isLoggedIn: Boolean = false,
        val errorMsg: String? = null,
    )

    sealed class Output {
        data object Close : Output()
    }
}