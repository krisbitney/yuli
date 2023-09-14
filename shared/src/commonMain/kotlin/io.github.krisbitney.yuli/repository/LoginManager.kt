package io.github.krisbitney.yuli.repository

import io.github.krisbitney.yuli.api.SocialApi
import io.github.krisbitney.yuli.api.requestTimeout
import io.github.krisbitney.yuli.database.SocialDatabase
import io.github.krisbitney.yuli.models.User
import io.github.krisbitney.yuli.models.UserState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

@OptIn(ExperimentalStdlibApi::class)
class LoginManager(private val api: SocialApi, private val db: SocialDatabase) {

    suspend fun createSession(
        username: String,
        password: String
    ): Result<User> = withContext(Dispatchers.IO) {
        val state = db.selectState(username)
            ?: UserState(username, isLoggedIn = false, isLocked = false, 0)

        val isLoggedIn = api.login(username, password)
        if (isLoggedIn.isFailure) {
            val e = isLoggedIn.exceptionOrNull()!!
            var newState = state.copy(isLoggedIn = false, isLocked = false)
            val exception = if (e.message!!.contains("factor")) {
                Exception("This account requires 2-factor-authentication.")
            } else if (e.message!!.contains("few minutes")) {
                Exception("Please wait a few minutes and try again.")
            } else if (e.message!!.contains("password")) {
                Exception("Username or password is incorrect")
            } else if (e.message!!.contains("challenge")) {
                newState = newState.copy(isLocked = true)
                Exception("Your account is locked. Open https://i.instagram.com/challenge to verify your account.")
            }  else {
                e
            }
            db.insertOrReplaceState(newState)
            return@withContext Result.failure(exception)
        }

        // fetch user to check login success
        val user = withTimeout(requestTimeout) { api.fetchUser() }

        // update state
        if (user.isSuccess) {
            db.insertOrReplaceState(state.copy(isLoggedIn = true, isLocked = false))
        }

        user
    }

    suspend fun restoreSession(username: String): Result<User> = withContext(Dispatchers.IO) {
        val state = db.selectState(username)
            ?: return@withContext Result.failure(Exception("User has never logged in"))

        if (!state.isLoggedIn) {
            return@withContext Result.failure(Exception("User is not logged in"))
        }

        if (state.isLocked) {
            return@withContext Result.failure(Exception("Your account is locked. Open https://i.instagram.com/challenge to verify your account."))
        }

        val isRestored = api.restoreSession(username).getOrElse {
            return@withContext Result.failure(it)
        }

        if (!isRestored) {
            db.insertOrReplaceState(state.copy(isLoggedIn = false))
            return@withContext Result.failure(Exception("Session could not be restored. User must log in again."))
        }

        val user = withTimeout(requestTimeout) { api.fetchUser() }

        if (user.isSuccess) {
            db.insertOrReplaceState(state.copy(isLoggedIn = true, isLocked = false))
        }

        user
    }
}