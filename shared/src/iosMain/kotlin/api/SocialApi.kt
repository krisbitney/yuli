package api

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import models.Profile
import models.User

actual object SocialApiFactory {
    actual fun get(): SocialApi = SwiftSocialApi()
}

class SwiftSocialApi : SocialApi {

    private val api = yuli_ios.SwiftSocialApi()

    override suspend fun loginAsync(username: String, password: String): Deferred<Result<Unit>> = coroutineScope {
        async {
            var isLoggedIn: Result<Unit> = Result.failure(Exception("'isLoggedIn' failed to initialize"))
            try {
                var isComplete = false
                api.login(username, password) { isSuccess: Boolean, error: String? ->
                    isLoggedIn = if (isSuccess) {
                        Result.success(Unit)
                    } else if (error != null) {
                        Result.failure(Exception(error))
                    } else {
                        Result.failure(Exception("login failed without error message"))
                    }
                    isComplete = true
                }
                while (!isComplete) {
                    delay(100L)
                }
            } catch (e: Exception) {
                isLoggedIn = Result.failure(e)
            }
            isLoggedIn
        }
    }

    override suspend fun restoreSessionAsync(): Deferred<Result<Boolean>> = coroutineScope {
        async {
            var isRestored: Result<Boolean> = Result.failure(Exception("'isRestored' failed to initialize"))
            try {
                var isComplete = false
                api.restoreSession { isSuccess: Boolean, error: String? ->
                    isRestored = if (isSuccess) {
                        Result.success(true)
                    } else if (error != null) {
                        Result.failure(Exception(error))
                    } else {
                        Result.failure(Exception("restoreSession failed without error message"))
                    }
                    isComplete = true
                }
                while (!isComplete) {
                    delay(100L)
                }
            } catch (e: Exception) {
                isRestored = Result.failure(e)
            }
            isRestored
        }
    }

    override suspend fun fetchUserProfileAsync(): Deferred<Result<User>> = coroutineScope {
        async {
            var user: Result<User> = Result.failure(Exception("'user' failed to initialize"))
            try {
                var isComplete = false
                api.fetchUserProfile { iosUser: yuli_ios.User?, error: String? ->
                    user = if (iosUser != null) {
                        Result.success(iosUser.toUser())
                    } else if (error != null) {
                        Result.failure(Exception(error))
                    } else {
                        Result.failure(Exception("fetchUserProfile failed without error message"))
                    }
                    isComplete = true
                }
                while (!isComplete) {
                    delay(100L)
                }
            } catch (e: Exception) {
                user = Result.failure(e)
            }
            user
        }
    }

    override suspend fun fetchFollowersAsync(pageDelay: Long): Deferred<Result<List<Profile>>> = coroutineScope {
        async {
            var followers: Result<List<Profile>> = Result.failure(Exception("'followers' failed to initialize"))
            try {
                var isComplete = false
                api.fetchFollowers(pageDelay) { iosFollowers: List<*>?, error: String? ->
                    followers = if (iosFollowers != null) {
                        iosFollowers
                            .filterIsInstance<yuli_ios.Profile>()
                            .map { it.toProfile() }
                            .let { Result.success(it) }
                    } else if (error != null) {
                        Result.failure(Exception(error))
                    } else {
                        Result.failure(Exception("fetchFollowers failed without error message"))
                    }
                    isComplete = true
                }
                while (!isComplete) {
                    delay(pageDelay)
                }
            } catch (e: Exception) {
                followers = Result.failure(e)
            }
            followers
        }
    }

    override suspend fun fetchFollowingsAsync(pageDelay: Long): Deferred<Result<List<Profile>>> = coroutineScope {
        async {
            var followings: Result<List<Profile>> = Result.failure(Exception("'followings' failed to initialize"))
            try {
                var isComplete = false
                api.fetchFollowers(pageDelay) { iosFollowings: List<*>?, error: String? ->
                    followings = if (iosFollowings != null) {
                        iosFollowings
                            .filterIsInstance<yuli_ios.Profile>()
                            .map { it.toProfile() }
                            .let { Result.success(it) }
                    } else if (error != null) {
                        Result.failure(Exception(error))
                    } else {
                        Result.failure(Exception("fetchFollowings failed without error message"))
                    }
                    isComplete = true
                }
                while (!isComplete) {
                    delay(pageDelay)
                }
            } catch (e: Exception) {
                followings = Result.failure(e)
            }
            followings
        }
    }
}
