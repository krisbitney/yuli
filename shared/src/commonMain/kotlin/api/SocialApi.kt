package api

import kotlinx.coroutines.Deferred
import models.Profile
import models.User

expect object SocialApiFactory {
    fun <T> get(context: T): SocialApi
}

interface SocialApi {
    suspend fun loginAsync(username: String, password: String): Deferred<Result<Unit>>
    suspend fun restoreSessionAsync(): Deferred<Boolean>
    suspend fun fetchUserProfileAsync(): Deferred<Result<User>>
    suspend fun fetchFollowersAsync(): Deferred<Result<List<Profile>>>
    suspend fun fetchFollowingsAsync(): Deferred<Result<List<Profile>>>
}
