package io.github.krisbitney.yuli.state.login

import io.github.krisbitney.yuli.models.User
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
        val loggedInUser: User? = null,
        val errorMsg: String? = null,
    )

    sealed class Output {
        data class Login(val user: User) : Output()
        data object Closed : Output()
    }
}