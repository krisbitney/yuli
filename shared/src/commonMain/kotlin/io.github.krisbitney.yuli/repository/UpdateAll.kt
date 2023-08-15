package io.github.krisbitney.yuli.repository

import io.github.krisbitney.yuli.api.SocialApiFactory
import io.github.krisbitney.yuli.api.randomizeDelay
import io.github.krisbitney.yuli.api.requestDelay
import io.github.krisbitney.yuli.api.requestTimeout
import io.github.krisbitney.yuli.database.createDatabase
import io.github.krisbitney.yuli.models.Event
import io.github.krisbitney.yuli.models.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.datetime.Clock

// TODO: add background tasks (after testing)
suspend fun <C> updateAll(context: C): Result<Unit> = withContext(Dispatchers.IO) {
    val api = SocialApiFactory.get(context)
    val db = createDatabase(context)

    LoginManager(api, db).restoreSession().onFailure { return@withContext Result.failure(it) }

    // fetch and store user
    val user = withTimeout(requestTimeout) {
        api.fetchUser()
    }.getOrElse { return@withContext Result.failure(it) }
    db.userQueries.replace(user.toDbUser())
    randomizeDelay(requestDelay)

    // fetch followers
    val followers = withTimeout(requestTimeout * user.followerCount) {
        api.fetchFollowers(requestDelay)
    }.getOrElse { return@withContext Result.failure(it) }
    randomizeDelay(requestDelay)

    // fetch followings
    val followings = withTimeout(requestTimeout * user.followingCount) {
        api.fetchFollowings(requestDelay)
    }.getOrElse { return@withContext Result.failure(it) }

    // organize followers and followings
    val follows = followSetAlgebra(followers, followings)

    // store follows
    db.transaction {
        follows.fans.forEach { fan ->
            db.profileQueries.insertOrReplace(
                username = fan.username,
                name = fan.name,
                picUrl = fan.picUrl,
                follower = true,
                following = false
            )
        }
        follows.mutuals.forEach { mutual ->
            db.profileQueries.insertOrReplace(
                username = mutual.username,
                name = mutual.name,
                picUrl = mutual.picUrl,
                follower = true,
                following = true
            )
        }
        follows.nonfollowers.forEach { nonfollower ->
            db.profileQueries.insertOrReplace(
                username = nonfollower.username,
                name = nonfollower.name,
                picUrl = nonfollower.picUrl,
                follower = false,
                following = true
            )
        }
    }

    // assess change events
    val previous = Follows(
        fans = db.profileQueries.selectFans().executeAsList().map { it.toProfile() },
        mutuals = db.profileQueries.selectMutuals().executeAsList().map { it.toProfile() },
        nonfollowers = db.profileQueries.selectNonfollowers().executeAsList()
            .map { it.toProfile() }
    )
    val events = deriveFollowEvents(previous, follows)

    // store events
    db.transaction {
        events.forEach { event ->
            db.eventQueries.insert(
                username = event.profile.username,
                kind = event.kind,
                timestamp = event.timestamp
            )
        }
    }

    Result.success(Unit)
}

private data class Follows(
    val fans: List<Profile>,
    val mutuals: List<Profile>,
    val nonfollowers: List<Profile>,
)

private fun followSetAlgebra(
    followers: List<Profile>,
    followings: List<Profile>
): Follows {
    val followersSet = followers.toSet()
    val followingsSet = followings.toSet()
    val fans = followersSet - followingsSet
    val mutuals = followersSet.intersect(followingsSet)
    val nonfollowers = followingsSet - followersSet
    return Follows(
        fans = fans.toList(),
        mutuals = mutuals.toList(),
        nonfollowers = nonfollowers.toList()
    )
}

private fun deriveFollowEvents(previous: Follows, current: Follows): List<Event> {
    val previousAll = previous.fans.union(previous.mutuals).union(previous.nonfollowers)
    val currentAll = current.fans.union(current.mutuals).union(current.nonfollowers)
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

