package io.github.krisbitney.yuli.api

import io.github.krisbitney.yuli.models.Profile
import io.github.krisbitney.yuli.models.User

expect object SocialApiFactory {
    fun <AndroidContext> get(context: AndroidContext): SocialApi
}

interface SocialApi {
    val context: Any?
    suspend fun login(username: String, password: String, onChallenge: () -> String): Result<Unit>
    suspend fun restoreSession(username: String): Result<Boolean>
    suspend fun fetchUser(): Result<User>
    suspend fun fetchFollowers(pageDelay: Long): Result<List<Profile>>
    suspend fun fetchFollowings(pageDelay: Long): Result<List<Profile>>
}
