package io.github.krisbitney.yuli.state.login

import io.github.krisbitney.yuli.models.User
import kotlinx.coroutines.flow.StateFlow

interface YuliLogin {
    val model: StateFlow<Model>

    suspend fun onLoginClicked()

    data class Model(
        val username: String? = null,
        val loginUser: User? = null,
        val errorMsg: String? = null,
    )

    sealed class Output {
        data class Login(val user: User) : Output()
    }
}