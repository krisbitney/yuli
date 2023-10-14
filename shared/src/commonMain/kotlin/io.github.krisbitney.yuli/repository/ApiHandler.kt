package io.github.krisbitney.yuli.repository

import io.github.krisbitney.yuli.api.SocialApi
import io.github.krisbitney.yuli.api.randomizeDelay
import io.github.krisbitney.yuli.api.requestDelay
import io.github.krisbitney.yuli.api.requestTimeout
import io.github.krisbitney.yuli.database.YuliDatabase
import io.github.krisbitney.yuli.models.Event
import io.github.krisbitney.yuli.models.Profile
import io.github.krisbitney.yuli.models.User
import io.github.krisbitney.yuli.models.UserState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.datetime.Clock

// TODO: withTimeouts will throw instead of returning Result.failure

@OptIn(ExperimentalStdlibApi::class)
class ApiHandler(private val api: SocialApi, private val db: YuliDatabase) {

    private data class Follows(
        val fans: Set<Profile>,
        val mutuals: Set<Profile>,
        val nonfollowers: Set<Profile>
    )

    suspend fun createSession(
        username: String,
        password: String
    ): Result<User> = withContext(Dispatchers.IO) {
        val maybeOldUser = db.selectUser()

        val initialState = when (maybeOldUser?.username) {
            username -> db.selectState()!!
            else -> UserState(username, isLoggedIn = false, isLocked = false, 0)
        }

        val isLoggedIn = withTimeout(requestTimeout) {
            api.login(username, password)
        }
        if (isLoggedIn.isFailure) {
            val e = isLoggedIn.exceptionOrNull()!!
            var newState = initialState.copy(isLoggedIn = false, isLocked = false)
            val exception = if (e.message!!.contains("factor")) {
                Exception("This account requires 2-factor-authentication.")
            } else if (e.message!!.contains("few minutes")) {
                Exception("Please wait a few minutes and try again.")
            } else if (e.message!!.contains("password")) {
                Exception("Username or password is incorrect")
            } else if (e.message!!.contains("challenge")) {
                newState = newState.copy(isLocked = true)
                Exception("Your account is locked. Open https://i.instagram.com/challenge to verify your account.")
            }  else {
                e
            }
            db.insertOrReplaceState(newState)
            return@withContext Result.failure(exception)
        }

        // fetch user to check login success
        val user = withTimeout(requestTimeout) { api.fetchUser() }

        // update database
        if (user.isSuccess) {
            // TODO: implement with endSession?
            if (maybeOldUser?.username != username) { db.clear() }
            db.insertOrReplaceState(initialState.copy(isLoggedIn = true, isLocked = false))
            db.insertOrReplaceUser(user.getOrThrow())
        }

        user
    }

    suspend fun restoreSession(username: String): Result<User> = withContext(Dispatchers.IO) {
        val state = db.selectState()
            ?: return@withContext Result.failure(Exception("User has never logged in"))

        if (!state.isLoggedIn) {
            return@withContext Result.failure(Exception("User is not logged in"))
        }

        if (state.isLocked) {
            return@withContext Result.failure(Exception("Your account is locked. Open https://i.instagram.com/challenge to verify your account."))
        }

        val isRestored = api.restoreSession(username).getOrElse {
            return@withContext Result.failure(it)
        }

        if (!isRestored) {
            db.insertOrReplaceState(state.copy(isLoggedIn = false))
            return@withContext Result.failure(Exception("Session could not be restored. User must log in again."))
        }

        val user = withTimeout(requestTimeout) { api.fetchUser() }

        if (user.isSuccess) {
            db.insertOrReplaceState(state.copy(isLoggedIn = true, isLocked = false))
            db.insertOrReplaceUser(user.getOrThrow())
        }

        user
    }

    suspend fun endSession() {
        TODO("Not yet implemented")
    }

    // TODO: return something to say in notification
    @OptIn(ExperimentalStdlibApi::class)
    suspend fun updateFollows(username: String): Result<Unit> = withContext(Dispatchers.IO) {
        ApiHandler(api, db)
            .restoreSession(username)
            .getOrElse { return@withContext Result.failure(it) }
        randomizeDelay(requestDelay)

        // fetch followers
        val followers = api.fetchFollowers(requestDelay).getOrElse {
            return@withContext Result.failure(it)
        }
        randomizeDelay(requestDelay)

        // fetch followings
        val followings = api.fetchFollowings(requestDelay).getOrElse {
            return@withContext Result.failure(it)
        }

        // get previous follows before update
        val previous = Follows(
            fans = db.selectFans().toSet(),
            mutuals = db.selectMutuals().toSet(),
            nonfollowers = db.selectNonfollowers().toSet()
        )

        // organize followers and followings
        val follows = followSetAlgebra(followers, followings)

        // calculate former connections
        val previousAll = previous.fans + previous.mutuals + previous.nonfollowers
        val formerConnections = (previousAll - follows.fans - follows.mutuals - follows.nonfollowers).onEach {
            it.follower = false
            it.following = false
        }

        // store updated follows
        db.insertOrReplaceProfile(follows.fans)
        db.insertOrReplaceProfile(follows.mutuals)
        db.insertOrReplaceProfile(follows.nonfollowers)
        db.insertOrReplaceProfile(formerConnections)

        // assess change events
        val events = deriveFollowEvents(previous, follows)

        // store events
        db.insertEvent(events)

        Result.success(Unit)
    }

    private fun followSetAlgebra(
        followers: List<Profile>,
        followings: List<Profile>
    ): Follows {
        val followersSet = followers.toSet()
        val followingsSet = followings.toSet()
        val fans = followersSet - followingsSet
        val mutuals = followersSet.intersect(followingsSet).onEach {
            it.follower = true
            it.following = true
        }
        val nonfollowers = followingsSet - followersSet
        return Follows(
            fans = fans,
            mutuals = mutuals,
            nonfollowers = nonfollowers
        )
    }

    private fun deriveFollowEvents(previous: Follows, current: Follows): List<Event> {
        val time = Clock.System.now().epochSeconds

        val currentFollowers = current.fans + current.mutuals
        val previousFollowers = previous.fans + previous.mutuals
        val gainedFollowers = currentFollowers - previousFollowers
        val lostFollowers = previousFollowers - currentFollowers

        val currentFollowings = current.mutuals + current.nonfollowers
        val previousFollowings = previous.mutuals + previous.nonfollowers
        val startedFollowing = currentFollowings - previousFollowings
        val stoppedFollowing = previousFollowings - currentFollowings

        val events: MutableList<Event> = mutableListOf()

        events.addAll(gainedFollowers.map { it.toEvent(Event.Kind.GAINED_FOLLOWER, time) })
        events.addAll(lostFollowers.map { it.toEvent(Event.Kind.LOST_FOLLOWER, time) })
        events.addAll(startedFollowing.map { it.toEvent(Event.Kind.STARTED_FOLLOWING, time) })
        events.addAll(stoppedFollowing.map { it.toEvent(Event.Kind.STOPPED_FOLLOWING, time) })

        return events
    }

    private fun Profile.toEvent(kind: Event.Kind, time: Long) = Event(
        profile = this,
        kind = kind,
        timestamp = time
    )
}