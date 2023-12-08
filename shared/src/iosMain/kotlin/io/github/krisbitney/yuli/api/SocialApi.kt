package io.github.krisbitney.yuli.api

import kotlinx.coroutines.delay
import io.github.krisbitney.yuli.models.Profile
import io.github.krisbitney.yuli.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import cocoapods.yuli_ios.SwiftSocialApi
import kotlinx.cinterop.ExperimentalForeignApi
import cocoapods.yuli_ios.Profile as SwiftProfile
import cocoapods.yuli_ios.User as SwiftUser

actual object SocialApiFactory {
    // context is unused in ios
    actual fun <AndroidContext> get(context: AndroidContext): SocialApi = IosSocialApi()
}

@OptIn(ExperimentalForeignApi::class)
class IosSocialApi : SocialApi {

    override val context = null

    private val api = SwiftSocialApi()

    override suspend fun login(username: String, password: String, onChallenge: () -> String): Result<Unit> = withContext(Dispatchers.IO) {
        var isLoggedIn: Result<Unit> = Result.failure(Exception("'isLoggedIn' failed to initialize"))
        try {
            var isComplete = false
            api.loginWithUsername(username, password, onChallenge) { isSuccess: Boolean, error: String? ->
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

    override suspend fun restoreSession(username: String): Result<Boolean> = withContext(Dispatchers.IO) {
        var isRestored: Result<Boolean> = Result.failure(Exception("'isRestored' failed to initialize"))
        try {
            var isComplete = false
            api.restoreSessionWithCompletion { isSuccess: Boolean, error: String? ->
                isRestored = if (error == null) {
                    Result.success(isSuccess)
                } else {
                    Result.failure(Exception(error))
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

    override suspend fun fetchUser(): Result<User> = withContext(Dispatchers.IO) {
        var user: Result<SwiftUser> = Result.failure(Exception("'user' failed to initialize"))
        try {
            var isComplete = false
            api.fetchUserProfileWithCompletion { iosUser: SwiftUser?, error: String? ->
                user = if (iosUser != null) {
                    Result.success(iosUser)
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
        user.map { it.toUser() }
    }

    override suspend fun fetchFollowers(pageDelay: Long): Result<List<Profile>> = withContext(Dispatchers.IO) {
        var followers: Result<List<Profile>> = Result.failure(Exception("'followers' failed to initialize"))
        try {
            var isComplete = false
            api.fetchFollowersWithPageDelay(pageDelay) { iosFollowers: List<*>?, error: String? ->
                followers = if (iosFollowers != null) {
                    iosFollowers
                        .filterIsInstance<SwiftProfile>()
                        .map { it.toProfile(follower = true) }
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

    override suspend fun fetchFollowings(pageDelay: Long): Result<List<Profile>> = withContext(Dispatchers.IO) {
        var followings: Result<List<Profile>> = Result.failure(Exception("'followings' failed to initialize"))
        try {
            var isComplete = false
            api.fetchFollowersWithPageDelay(pageDelay) { iosFollowings: List<*>?, error: String? ->
                followings = if (iosFollowings != null) {
                    iosFollowings
                        .filterIsInstance<SwiftProfile>()
                        .map { it.toProfile(following = true) }
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
