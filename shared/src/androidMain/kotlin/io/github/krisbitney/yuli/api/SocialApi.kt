package io.github.krisbitney.yuli.api

import com.github.instagram4j.instagram4j.IGClient
import com.github.instagram4j.instagram4j.actions.users.UserAction
import com.github.instagram4j.instagram4j.exceptions.IGLoginException
import kotlinx.coroutines.delay
import io.github.krisbitney.yuli.models.Profile
import io.github.krisbitney.yuli.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

actual object SocialApiFactory {
    actual fun get(androidSecureStorageDir: String?): SocialApi = AndroidSocialApi(androidSecureStorageDir!!)
}

class AndroidSocialApi(storageDir: String) : SocialApi {

    // TODO: use more secure method of storing credentials -- encryption?
    private val cacheDir = File(storageDir, "cache").also {
        if (!it.exists()) it.mkdirs()
    }
    private val client = File(cacheDir, "ClientObject.ser")
    private val cookie = File(cacheDir, "LoginSession.ser")
    private var insta: IGClient? = null
    private var username: String? = null

    override suspend fun login(username: String, password: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            if (client.exists()) client.delete()
            if (cookie.exists()) cookie.delete()
            try {
                val ig = IGClient.builder()
                    .username(username)
                    .password(password)
                    .login()
                ig.serialize(client, cookie)
                insta = ig
                this@AndroidSocialApi.username = username
            } catch (e: IGLoginException) {
                // TODO: Do I need this?
                val revised: Exception = if (e.loginResponse.two_factor_info != null) {
                    Exception("This account requires 2-factor-authentication.")
                } else {
                    e
                }
                return@withContext Result.failure(revised)
            }
        }
    }

    override suspend fun restoreSession(): Result<Boolean> = withContext(Dispatchers.IO) {
        if (client.exists() && cookie.exists()) {
            try {
                insta = IGClient.deserialize(client, cookie)
                this@AndroidSocialApi.username = username
            } catch (e: Exception) {
                return@withContext Result.failure(e)
            }
            return@withContext Result.success(true)
        }
        Result.success(false)
    }

    override suspend fun fetchUser(): Result<User> = withContext(Dispatchers.IO) {
        val ig = insta ?: return@withContext Result.failure(Exception("User is not logged in"))
        runCatching {
            ig.actions.account().currentUser().get().user
        }.getOrElse { e ->
            return@withContext Result.failure(e)
        }.let {
            Result.success(it.toUser())
        }
    }

    override suspend fun fetchFollowers(pageDelay: Long): Result<List<Profile>> =
        fetchFollows(pageDelay, FollowType.Follower)

    override suspend fun fetchFollowings(pageDelay: Long): Result<List<Profile>> =
        fetchFollows(pageDelay, FollowType.Following)

    private suspend fun fetchFollows(pageDelay: Long, target: FollowType): Result<List<Profile>> = withContext(Dispatchers.IO) {
        val ig = insta ?: return@withContext Result.failure(Exception("User is not logged in"))
        runCatching {
            val action: UserAction = ig.actions().users().findByUsername(username).get()
            when (target) {
                FollowType.Follower -> action.followersFeed()
                FollowType.Following -> action.followingFeed()
            }
        }.getOrElse { e ->
            return@withContext Result.failure(e)
        }.flatMap {
            delay(randomizeDelay(pageDelay))
            it.users
        }.map {
            when (target) {
                FollowType.Follower -> it.toProfile(follower = true)
                FollowType.Following -> it.toProfile(following = true)
            }
        }.let {
            Result.success(it)
        }
    }
}