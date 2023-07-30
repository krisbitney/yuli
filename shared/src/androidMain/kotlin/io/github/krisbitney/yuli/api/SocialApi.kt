package io.github.krisbitney.yuli.api

import android.content.Context
import com.github.instagram4j.instagram4j.IGClient
import com.github.instagram4j.instagram4j.actions.users.UserAction
import com.github.instagram4j.instagram4j.exceptions.IGLoginException
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import io.github.krisbitney.yuli.models.Profile
import io.github.krisbitney.yuli.models.User
import java.io.File

actual class SocialApiFactory(private val context: Context) {

    actual fun get(): SocialApi = AndroidSocialApi(context)
}

class AndroidSocialApi(context: Context) : SocialApi {

    private val cacheDir = File(context.filesDir, "cache")
    private val client = File(cacheDir, "ClientObject.ser")
    private val cookie = File(cacheDir, "LoginSession.ser")
    private var insta: IGClient? = null
    private var username: String? = null

    override suspend fun loginAsync(username: String, password: String): Deferred<Result<Unit>> = coroutineScope {
        async {
            runCatching {
                if (client.exists()) client.delete()
                if (cookie.exists()) cookie.delete()
                try {
                    val ig = IGClient.builder()
                        .username(username)
                        .password(password)
                        .simulatedLogin()
                    ig.serialize(client, cookie)
                    insta = ig
                } catch (e: IGLoginException) {
                    val revised: Exception = if (e.loginResponse.two_factor_info != null) {
                        Exception("This account requires 2-factor-authentication.")
                    } else if (e.message!!.contains("few minutes")) {
                        Exception("Wait a few minutes and try again.")
                    } else if (e.message!!.contains("password")) {
                        Exception("Username or password is incorrect")
                    } else if (e.message!!.contains("challenge")) {
                        Exception("You account is locked. Open https://i.instagram.com/challenge to verify your account.")
                    }  else {
                        e
                    }
                    return@async Result.failure(revised)
                }
            }
        }
    }

    override suspend fun restoreSessionAsync(): Deferred<Result<Boolean>> = coroutineScope {
        async {
            if (client.exists() && cookie.exists()) {
                insta = IGClient.deserialize(client, cookie)
                return@async Result.success(true)
            }
            Result.success(false)
        }
    }

    override suspend fun fetchUserProfileAsync(): Deferred<Result<User>>  = coroutineScope {
        async {
            val ig = insta ?: return@async Result.failure(Exception("User is not logged in"))
            runCatching {
                ig.actions.account().currentUser().get().user
            }.getOrElse { e ->
                return@async Result.failure(e)
            }.let {
                Result.success(it.toUser())
            }
        }
    }

    override suspend fun fetchFollowersAsync(pageDelay: Long): Deferred<Result<List<Profile>>> = fetchFollowsAsync(pageDelay,
        FollowType.Followers
    )

    override suspend fun fetchFollowingsAsync(pageDelay: Long): Deferred<Result<List<Profile>>> = fetchFollowsAsync(pageDelay,
        FollowType.Followings
    )

    private enum class FollowType {
        Followers,
        Followings
    }

    private suspend fun fetchFollowsAsync(pageDelay: Long, target: FollowType): Deferred<Result<List<Profile>>> = coroutineScope {
        async {
            val ig = insta ?: return@async Result.failure(Exception("User is not logged in"))
            runCatching {
                val action: UserAction = ig.actions().users().findByUsername(username).get()
                when (target) {
                    FollowType.Followers -> action.followersFeed()
                    FollowType.Followings -> action.followingFeed()
                }
            }.getOrElse { e ->
                return@async Result.failure(e)
            }.flatMap {
                delay(randomizePageDelay(pageDelay))
                it.users
            }.map {
                it.toProfile()
            }.let {
                Result.success(it)
            }
        }
    }
}