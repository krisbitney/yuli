package io.github.krisbitney.yuli.repository

import io.github.krisbitney.yuli.api.SocialApi
import io.github.krisbitney.yuli.api.randomizeDelay
import io.github.krisbitney.yuli.api.requestDelay
import io.github.krisbitney.yuli.api.requestTimeout
import io.github.krisbitney.yuli.database.YuliDatabase
import io.github.krisbitney.yuli.models.Event
import io.github.krisbitney.yuli.models.FollowType
import io.github.krisbitney.yuli.models.Profile
import io.github.krisbitney.yuli.models.User
import io.github.krisbitney.yuli.models.UserState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.datetime.Clock

@OptIn(ExperimentalStdlibApi::class)
class ApiHandler(private val api: SocialApi, private val db: YuliDatabase) {

    val inBackground = LaunchInBackground(api.context)
    inner class LaunchInBackground(private val context: Any?) {
        suspend fun updateFollowsAndNotify() = BackgroundTaskLauncher.updateFollowsAndNotify(context)
    }

    data class UpdateFollowsSummary(
        val gainedFollowers: Int,
        val lostFollowers: Int
    )

    private data class Follows(
        val fans: Set<Profile>,
        val mutuals: Set<Profile>,
        val nonfollowers: Set<Profile>
    )

    suspend fun createSession(
        username: String,
        password: String,
        onChallenge: () -> String
    ): Result<User> = withContext(Dispatchers.IO) {
        val maybeOldUser = db.selectUser()

        val state = when (maybeOldUser?.username) {
            username -> db.selectState()!!
            else -> UserState(username, isLoggedIn = false, isLocked = false, lastUpdate = 0L)
        }

        val isLoggedIn = api.login(username, password, onChallenge)

        if (isLoggedIn.isFailure) {
            val exception = handleLoginException(isLoggedIn.exceptionOrNull()!!, state)
            return@withContext Result.failure(exception)
        }

        checkLogin(state, maybeOldUser?.username != username)
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

        checkLogin(state, false)
    }

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun updateFollows(reportProgress: suspend (message: String) -> Unit = {}): Result<UpdateFollowsSummary> = withContext(Dispatchers.IO) {
        val username = db.selectUser()?.username
        if (username == null) {
            val e = Exception("No user logged in")
            return@withContext Result.failure(e)
        }

        reportProgress("Checking login status...")
        ApiHandler(api, db)
            .restoreSession(username)
            .getOrElse { return@withContext Result.failure(it) }
        randomizeDelay(requestDelay)

        // fetch followers
        reportProgress("Downloading followers...")
        val followers = api.fetchFollowers(requestDelay).getOrElse {
            return@withContext Result.failure(it)
        }
        randomizeDelay(requestDelay)

        // fetch followings
        reportProgress("Downloading followings...")
        val followings = api.fetchFollowings(requestDelay).getOrElse {
            return@withContext Result.failure(it)
        }

        reportProgress("Finishing up...")

        // get previous follows before update
        val previous = Follows(
            fans = db.selectProfiles(FollowType.FAN).toSet(),
            mutuals = db.selectProfiles(FollowType.MUTUAL).toSet(),
            nonfollowers = db.selectProfiles(FollowType.NONFOLLOWER).toSet()
        )

        // organize followers and followings
        val follows = followSetAlgebra(followers, followings)

        // calculate former follows
        val previousAll = previous.fans + previous.mutuals + previous.nonfollowers
        val formerFollows = (previousAll - follows.fans - follows.mutuals - follows.nonfollowers).onEach {
            it.follower = false
            it.following = false
        }

        // store updated follows
        db.insertOrReplaceProfiles(follows.fans)
        db.insertOrReplaceProfiles(follows.mutuals)
        db.insertOrReplaceProfiles(follows.nonfollowers)
        db.insertOrReplaceProfiles(formerFollows)

        // assess change events
        val events = deriveFollowEvents(previous, follows)

        // store events
        db.insertEvents(events)

        // summarize changes
        val gainedFollowers = events.filter { it.kind == Event.Kind.GAINED_FOLLOWER }.size
        val lostFollowers = events.filter { it.kind == Event.Kind.LOST_FOLLOWER }.size
        val summary = UpdateFollowsSummary(
            gainedFollowers = gainedFollowers,
            lostFollowers = lostFollowers
        )

        Result.success(summary)
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

    // checks login; updates database and user state
    private suspend fun checkLogin(initialState: UserState, isNewUserLogin: Boolean): Result<User> = withContext(Dispatchers.IO) {
        val user = withTimeoutOrNull(requestTimeout) { api.fetchUser() }
            ?: return@withContext Result.failure(Exception("Login timed out. Please try again."))

        if (user.isSuccess) {
            if (isNewUserLogin) { db.clear() }
            db.insertOrReplaceState(initialState.copy(isLoggedIn = true, isLocked = false))
            db.insertOrReplaceUser(user.getOrThrow())
            user
        } else {
            val exception = handleLoginException(user.exceptionOrNull()!!, initialState)
            Result.failure(exception)
        }
    }

    // handles login exceptions; updates database and user state
    private suspend fun handleLoginException(e: Throwable, initialState: UserState): Throwable = withContext(Dispatchers.IO) {
        var newState = initialState.copy(isLoggedIn = false, isLocked = false)
        val message = e.message ?: ""
        val newException = if (message.contains("factor")) {
            Exception("This account requires 2-factor-authentication.")
        } else if (message.contains("few minutes")) {
            Exception("Please wait a few minutes and try again.")
        } else if (
            message.contains("password") ||
            message.contains("Authenticator.Error error 2") ||
            message.contains("Authenticator.Error error 5")
            ) {
            Exception("Username or password is incorrect")
        } else if (message.contains("challenge") || message.contains("Authenticator.Error error 6")) {
            newState = newState.copy(isLocked = true)
            Exception("Your account is locked. Open https://i.instagram.com/challenge to verify your account.")
        } else {
            e
        }
        db.insertOrReplaceState(newState)
        newException
    }
}