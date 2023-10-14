package io.github.krisbitney.yuli.database

import io.github.krisbitney.yuli.models.Event
import io.github.krisbitney.yuli.models.Profile
import io.github.krisbitney.yuli.models.User
import io.github.krisbitney.yuli.models.UserState
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.delete
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

// TODO: handle realm open/close on each use to avoid memory leaks, or possibly use singleton and close on app exit
@ExperimentalStdlibApi
class YuliDatabase : AutoCloseable {
    private val configuration = RealmConfiguration.create(
        schema = setOf(User::class, UserState::class, Profile::class, Event::class)
    )
    private val realm = Realm.open(configuration)

    fun countMutuals(): Flow<Long> {
        return realm.query<Profile>("follower > 0 AND following > 0").count().asFlow()
    }

    fun countNonfollowers(): Flow<Long> {
        return realm.query<Profile>("follower = 0 AND following > 0").count().asFlow()
    }

    fun countFans(): Flow<Long> {
        return realm.query<Profile>("follower > 0 AND following = 0").count().asFlow()
    }

    fun countFormerConnections(): Flow<Long> {
        return realm.query<Profile>("follower = 0 AND following = 0").count().asFlow()
    }

    // TODO: should these use 1 and 0 or true and false?
    fun selectMutuals(): List<Profile> {
        return realm.query<Profile>("follower > 0 AND following > 0").find()
    }

    fun selectNonfollowers(): List<Profile> {
        return realm.query<Profile>("follower = 0 AND following > 0").find()
    }

    fun selectFans(): List<Profile> {
        return realm.query<Profile>("follower > 0 AND following = 0").find()
    }
    
    fun selectFormer(): List<Profile> {
        return realm.query<Profile>("follower = 0 AND following = 0").find()
    }

    suspend fun insertOrReplaceProfile(profile: Profile) = withContext(Dispatchers.IO) {
        realm.write {
            copyToRealm(profile, UpdatePolicy.ALL)
        }
    }

    suspend fun insertOrReplaceProfile(profiles: Collection<Profile>) = withContext(Dispatchers.IO) {
        realm.write {
            profiles.forEach { copyToRealm(it, UpdatePolicy.ALL) }
        }
    }

    fun selectUser(): User? {
        return realm.query<User>().first().find()
    }

    suspend fun insertOrReplaceUser(user: User) = withContext(Dispatchers.IO) {
        realm.write {
            copyToRealm(user, UpdatePolicy.ALL)
        }
    }

    fun selectState(): UserState? {
        return realm.query<UserState>().first().find()
    }

    suspend fun insertOrReplaceState(state: UserState) = withContext(Dispatchers.IO) {
        realm.write {
            copyToRealm(state, UpdatePolicy.ALL)
        }
    }

    fun selectEvents(fromUnixTime: Long, toUnixTime: Long): List<Event> {
        return realm.query<Event>("timestamp > $0 AND timestamp < $1", fromUnixTime, toUnixTime)
            .sort("timestamp", Sort.DESCENDING)
            .find()
    }

    suspend fun insertEvent(event: Event) = withContext(Dispatchers.IO) {
        realm.write {
            copyToRealm(event)
        }
    }

    suspend fun insertEvent(events: Collection<Event>) = withContext(Dispatchers.IO) {
        realm.write {
            events.forEach { copyToRealm(it) }
        }
    }

    fun getAllEvents(): List<Event> = selectEvents(
        Instant.DISTANT_PAST.epochSeconds,
        Instant.DISTANT_FUTURE.epochSeconds
    )

    fun getTodayEvents(): List<Event> = selectEvents(
        beginningOfTodayUnixTimestamp(),
        Instant.DISTANT_FUTURE.epochSeconds
    )

    suspend fun clear() = withContext(Dispatchers.IO) {
        realm.write {
            delete<Event>()
            delete<Profile>()
            delete<User>()
            delete<UserState>()
        }
    }

    // TODO: move this to events state file
    private fun beginningOfTodayUnixTimestamp(): Long {
        val now = Clock.System.now()
        val localToday = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
        val startOfDay = LocalDateTime(localToday, LocalTime(0, 0))
        return startOfDay.toInstant(TimeZone.currentSystemDefault()).epochSeconds
    }

    override fun close() = realm.close()
}
