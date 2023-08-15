package io.github.krisbitney.yuli.repository

import io.github.krisbitney.yuli.api.SocialApi
import io.github.krisbitney.yuli.database.SocialDatabase
import io.github.krisbitney.yuli.database.models.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

// TODO: must test login before returning success in each method
class LoginManager(private val api: SocialApi, private val db: SocialDatabase) {

    suspend fun createSession(
        username: String,
        password: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val state = db.stateQueries.select().executeAsOneOrNull()
            ?: State(0, isLoggedIn = false, isLocked = false, 0)

        if (state.isLoggedIn && !state.isLocked) {
            val isRestored = restoreSession()
            if (isRestored.isSuccess) {
                return@withContext Result.success(Unit)
            }
        }

        val isLoggedIn = api.login(username, password)
        if (isLoggedIn.isFailure) {
            val e = isLoggedIn.exceptionOrNull()!!
            var newState = state.copy(isLoggedIn = false, isLocked = false)
            val exception = if (e.message!!.contains("factor")) {
                Exception("This account requires 2-factor-authentication.")
            } else if (e.message!!.contains("few minutes")) {
                Exception("Wait a few minutes and try again.")
            } else if (e.message!!.contains("password")) {
                Exception("Username or password is incorrect")
            } else if (e.message!!.contains("challenge")) {
                newState = newState.copy(isLocked = true)
                Exception("Your account is locked. Open https://i.instagram.com/challenge to verify your account.")
            }  else {
                e
            }
            db.stateQueries.replace(newState)
            return@withContext Result.failure(exception)
        }

        db.stateQueries.replace(state.copy(isLoggedIn = true, isLocked = false))
        Result.success(Unit)
    }

    suspend fun restoreSession(): Result<Unit> = withContext(Dispatchers.IO) {
        val state = db.stateQueries.select().executeAsOneOrNull()
            ?: return@withContext Result.failure(Exception("User has never logged in"))

        if (!state.isLoggedIn) {
            return@withContext Result.failure(Exception("User is not logged in"))
        }

        if (state.isLocked) {
            return@withContext Result.failure(Exception("Your account is locked. Open https://i.instagram.com/challenge to verify your account."))
        }

        val isRestored = api.restoreSession()
        if (isRestored.isFailure) {
            return@withContext Result.failure(isRestored.exceptionOrNull()!!)
        }

        if (!isRestored.getOrThrow()) {
            db.stateQueries.replace(state.copy(isLoggedIn = false))
            return@withContext Result.failure(Exception("Session could not be restored. User must log in again."))
        }

        Result.success(Unit)
    }
}