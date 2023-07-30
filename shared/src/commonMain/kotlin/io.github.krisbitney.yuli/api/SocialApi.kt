package io.github.krisbitney.yuli.api

import kotlinx.coroutines.Deferred
import io.github.krisbitney.yuli.models.Profile
import io.github.krisbitney.yuli.models.User

expect class SocialApiFactory {
    fun get(): SocialApi
}

interface SocialApi {
    suspend fun loginAsync(username: String, password: String): Deferred<Result<Unit>>
    suspend fun restoreSessionAsync(): Deferred<Result<Boolean>>
    suspend fun fetchUserProfileAsync(): Deferred<Result<User>>
    suspend fun fetchFollowersAsync(pageDelay: Long): Deferred<Result<List<Profile>>>
    suspend fun fetchFollowingsAsync(pageDelay: Long): Deferred<Result<List<Profile>>>
}
