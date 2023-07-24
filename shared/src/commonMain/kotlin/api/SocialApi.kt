package api

import kotlinx.coroutines.Deferred
import models.Profile
import models.User

expect object SocialApiFactory {
    fun get(): SocialApi
}

interface SocialApi {
    suspend fun loginAsync(username: String, password: String): Deferred<Result<Unit>>
    suspend fun restoreSessionAsync(): Deferred<Result<Boolean>>
    suspend fun fetchUserProfileAsync(): Deferred<Result<User>>
    suspend fun fetchFollowersAsync(pageDelay: Long): Deferred<Result<List<Profile>>>
    suspend fun fetchFollowingsAsync(pageDelay: Long): Deferred<Result<List<Profile>>>
}
