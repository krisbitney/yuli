package io.github.krisbitney.yuli.repository

import io.github.krisbitney.yuli.api.SocialApi
import io.github.krisbitney.yuli.api.randomizeDelay
import io.github.krisbitney.yuli.api.requestDelay
import io.github.krisbitney.yuli.api.requestTimeout
import io.github.krisbitney.yuli.database.SocialDatabase
import io.github.krisbitney.yuli.models.Event
import io.github.krisbitney.yuli.models.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.datetime.Clock

// TODO: add background tasks (after testing)
@OptIn(ExperimentalStdlibApi::class)
suspend fun updateAll(
    username: String,
    api: SocialApi,
    db: SocialDatabase
): Result<Unit> = withContext(Dispatchers.IO) {
    val user = LoginManager(api, db)
        .restoreSession(username)
        .getOrElse { return@withContext Result.failure(it) }

    // store user
    db.insertOrReplaceUser(user)
    randomizeDelay(requestDelay)

    // TODO: refactor timeout calculations into separate function
    // fetch followers
    val lastFollowerCount = db.countFollowers()
    val lastFollowerTimeout = if (lastFollowerCount == 0L) {
        Long.MAX_VALUE
    } else {
        (lastFollowerCount * 1.1 * requestTimeout).toLong()
    }
    val followers = withTimeout(lastFollowerTimeout) {
        api.fetchFollowers(requestDelay)
    }.getOrElse { return@withContext Result.failure(it) }
    randomizeDelay(requestDelay)

    // fetch followings
    val lastFollowingCount = db.countFollowing()
    val lastFollowingTimeout = if (lastFollowingCount == 0L) {
        Long.MAX_VALUE
    } else {
        (lastFollowingCount * 1.1 * requestTimeout).toLong()
    }
    val followings = withTimeout(lastFollowingTimeout) {
        api.fetchFollowings(requestDelay)
    }.getOrElse { return@withContext Result.failure(it) }

    // get previous follows before update
    val previous = Follows(
        fans = db.selectFans().toSet(),
        mutuals = db.selectMutuals().toSet(),
        nonfollowers = db.selectNonfollowers().toSet()
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
    db.insertOrReplaceProfile(follows.fans)
    db.insertOrReplaceProfile(follows.mutuals)
    db.insertOrReplaceProfile(follows.nonfollowers)
    db.insertOrReplaceProfile(formerFollows)

    // assess change events
    val events = deriveFollowEvents(previous, follows)

    // store events
    db.insertEvent(events)

    Result.success(Unit)
}

private data class Follows(
    val fans: Set<Profile>,
    val mutuals: Set<Profile>,
    val nonfollowers: Set<Profile>
)

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
    val previousAll = previous.fans + previous.mutuals + previous.nonfollowers
    val currentAll = current.fans + current.mutuals + current.nonfollowers
    val time = Clock.System.now().epochSeconds

    return previousAll.union(currentAll).mapNotNull {
        when {
            it in previous.fans && it in current.mutuals -> Event(
                it,
                Event.Kind.FAN_TO_MUTUAL,
                time
            )

            it in previous.fans && it !in currentAll -> Event(it, Event.Kind.FAN_TO_NONE, time)
            it in previous.nonfollowers && it in current.mutuals -> Event(
                it,
                Event.Kind.NONFOLLOWER_TO_MUTUAL,
                time
            )

            it in previous.nonfollowers && it !in currentAll -> Event(
                it,
                Event.Kind.NONFOLLOWER_TO_NONE,
                time
            )

            it in previous.mutuals && it in current.nonfollowers -> Event(
                it,
                Event.Kind.MUTUAL_TO_NONFOLLOWER,
                time
            )

            it in previous.mutuals && it in current.fans -> Event(
                it,
                Event.Kind.MUTUAL_TO_FAN,
                time
            )

            it in current.nonfollowers && it !in previousAll -> Event(
                it,
                Event.Kind.NONE_TO_NONFOLLOWER,
                time
            )

            it in current.fans && it !in previousAll -> Event(it, Event.Kind.NONE_TO_FAN, time)
            it in previous.nonfollowers && it in current.fans -> Event(
                it,
                Event.Kind.NONFOLLOWER_TO_FAN,
                time
            )

            it in previous.fans && it in current.nonfollowers -> Event(
                it,
                Event.Kind.FAN_TO_NONFOLLOWER,
                time
            )

            it in current.mutuals && it !in previousAll -> Event(
                it,
                Event.Kind.NONE_TO_MUTUAL,
                time
            )

            it in previous.mutuals && it !in currentAll -> Event(
                it,
                Event.Kind.MUTUAL_TO_NONE,
                time
            )

            else -> null
        }
    }
}

