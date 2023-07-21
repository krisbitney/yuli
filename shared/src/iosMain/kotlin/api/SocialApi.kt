package api

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import models.Profile
import models.User

actual object SocialApiFactory {
    actual fun <T> get(context: T): SocialApi = SwiftSocialApi()
}

class SwiftSocialApi : SocialApi {
    override suspend fun loginAsync(username: String, password: String): Deferred<Result<Unit>> = coroutineScope {
        async {
            Result.failure(NotImplementedError("Not implemented"))
        }
    }

    override suspend fun restoreSessionAsync(): Deferred<Boolean> = coroutineScope {
        async {
            false
        }
    }

    override suspend fun fetchUserProfileAsync(): Deferred<Result<User>> = coroutineScope {
        async {
            Result.failure(NotImplementedError("Not implemented"))
        }
    }

    override suspend fun fetchFollowersAsync(): Deferred<Result<List<Profile>>> = coroutineScope {
        async {
            Result.failure(NotImplementedError("Not implemented"))
        }
    }

    override suspend fun fetchFollowingsAsync(): Deferred<Result<List<Profile>>> = coroutineScope {
        async {
            Result.failure(NotImplementedError("Not implemented"))
        }
    }
}
