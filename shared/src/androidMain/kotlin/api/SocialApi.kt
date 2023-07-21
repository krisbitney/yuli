package api

import android.content.Context
import com.github.instagram4j.instagram4j.IGClient
import com.github.instagram4j.instagram4j.actions.users.UserAction
import com.github.instagram4j.instagram4j.exceptions.IGLoginException
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import models.Profile
import models.User
import java.io.File

actual object SocialApiFactory {
    actual fun <T> get(context: T): SocialApi = AndroidSocialApi(context as Context)
}

class AndroidSocialApi(private val context: Context) : SocialApi {

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

    override suspend fun restoreSessionAsync(): Deferred<Boolean> = coroutineScope {
        async {
            if (client.exists() && cookie.exists()) {
                insta = IGClient.deserialize(client, cookie)
                return@async true
            }
            false
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
                User(
                    username = it.username,
                    name = it.full_name,
                    picUrl = it.profile_pic_url,
                    followerCount = it.follower_count,
                    followingCount = it.following_count,
                    mediaCount = it.media_count
                )
            }.let {
                Result.success(it)
            }
        }
    }

    override suspend fun fetchFollowersAsync(): Deferred<Result<List<Profile>>> = fetchFollowsAsync(FollowType.Followers)

    override suspend fun fetchFollowingsAsync(): Deferred<Result<List<Profile>>> = fetchFollowsAsync(FollowType.Followings)

    private enum class FollowType {
        Followers,
        Followings
    }

    private suspend fun fetchFollowsAsync(target: FollowType): Deferred<Result<List<Profile>>> = coroutineScope {
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
                requestDelay()
                it.users
            }.map {
                Profile(it.username, it.full_name, it.profile_pic_url)
            }.let {
                Result.success(it)
            }
        }
    }
}