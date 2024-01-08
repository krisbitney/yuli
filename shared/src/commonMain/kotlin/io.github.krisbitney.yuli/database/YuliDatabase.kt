package io.github.krisbitney.yuli.database

import io.github.krisbitney.yuli.models.Event
import io.github.krisbitney.yuli.models.FollowType
import io.github.krisbitney.yuli.models.Profile
import io.github.krisbitney.yuli.models.User
import io.github.krisbitney.yuli.models.UserState
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@ExperimentalStdlibApi
class YuliDatabase : AutoCloseable {
    private val configuration = RealmConfiguration.create(
        schema = setOf(User::class, UserState::class, Profile::class, Event::class)
    )
    private val realm = Realm.open(configuration)

    suspend fun countProfilesAsFlow(type: FollowType): Flow<Long> = withContext(Dispatchers.IO) {
        val query = when(type) {
            FollowType.MUTUAL -> "follower == true AND following == true"
            FollowType.NONFOLLOWER -> "follower == false AND following == true"
            FollowType.FAN -> "follower == true AND following == false"
            FollowType.FORMER -> "follower == false AND following == false"
        }
        realm.query<Profile>(query).count().asFlow()
    }

    suspend fun selectProfiles(type: FollowType): List<Profile> = withContext(Dispatchers.IO) {
        val query = when(type) {
            FollowType.MUTUAL -> "follower == true AND following == true"
            FollowType.NONFOLLOWER -> "follower == false AND following == true"
            FollowType.FAN -> "follower == true AND following == false"
            FollowType.FORMER -> "follower == false AND following == false"
        }
        realm.query<Profile>(query).find()
    }

    suspend fun insertOrReplaceProfiles(profiles: Collection<Profile>) = withContext(Dispatchers.IO) {
        realm.write {
            profiles.forEach { copyToRealm(it, UpdatePolicy.ALL) }
        }
    }

    suspend fun selectUser(): User? = withContext(Dispatchers.IO) {
        realm.query<User>().first().find()
    }

    suspend fun insertOrReplaceUser(user: User) = withContext(Dispatchers.IO) {
        realm.write {
            copyToRealm(user, UpdatePolicy.ALL)
        }
    }

    suspend fun selectState(): UserState? = withContext(Dispatchers.IO) {
        realm.query<UserState>().first().find()
    }

    suspend fun insertOrReplaceState(state: UserState) = withContext(Dispatchers.IO) {
        realm.write {
            copyToRealm(state, UpdatePolicy.ALL)
        }
    }

    suspend fun selectEvents(fromUnixTime: Long, toUnixTime: Long): List<Event> = withContext(Dispatchers.IO) {
        realm.query<Event>("timestamp > $0 AND timestamp < $1", fromUnixTime, toUnixTime)
            .sort("timestamp", Sort.DESCENDING)
            .find()
    }

    suspend fun insertEvents(events: Collection<Event>) = withContext(Dispatchers.IO) {
        realm.write {
            events.forEach { copyToRealm(it, UpdatePolicy.ALL) }
        }
    }

    suspend fun deleteEvents(events: Collection<Event>) = withContext(Dispatchers.IO) {
        if (events.isEmpty()) return@withContext
        realm.write {
            val realmList = events.toRealmList()
            while (realmList.isNotEmpty()) {
                delete(realmList.removeLast())
            }
        }
    }

    suspend fun clear() = withContext(Dispatchers.IO) {
        realm.write {
            deleteAll()
        }
    }

    fun daysAgoUnixTimestamp(days: Int): Long {
        return Clock.System.now().minus(days.toDuration(DurationUnit.DAYS)).epochSeconds
    }

    override fun close() = realm.close()
}
