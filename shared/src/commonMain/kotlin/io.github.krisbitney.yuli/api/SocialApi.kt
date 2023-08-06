package io.github.krisbitney.yuli.api

import io.github.krisbitney.yuli.models.Profile
import io.github.krisbitney.yuli.models.User

expect object SocialApiFactory {
    fun <C>get(context: C): SocialApi
}

interface SocialApi {
    suspend fun login(username: String, password: String): Result<Unit>
    suspend fun restoreSession(): Result<Boolean>
    suspend fun fetchUser(): Result<User>
    suspend fun fetchFollowers(pageDelay: Long): Result<List<Profile>>
    suspend fun fetchFollowings(pageDelay: Long): Result<List<Profile>>
}
