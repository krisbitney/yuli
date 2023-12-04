package io.github.krisbitney.yuli.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Event(
    var username: String,
    var name: String,
    var kindOrdinal: Int,
    var timestamp: Long
) : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()

    constructor() : this("", "", 0, 0)
    constructor(
        profile: Profile,
        kind: Kind,
        timestamp: Long
    ) : this(profile.username, profile.name, kind.ordinal, timestamp)

    var kind: Kind
        get() = Kind.entries[kindOrdinal]
        set(value) { kindOrdinal = value.ordinal }

    enum class Kind {
        GAINED_FOLLOWER,
        LOST_FOLLOWER,
        STARTED_FOLLOWING,
        STOPPED_FOLLOWING
    }

    enum class TimePeriod {
        ONE_DAY,
        SEVEN_DAYS,
        ALL;

        override fun toString(): String {
            return when (this) {
                ONE_DAY -> "1"
                SEVEN_DAYS -> "7"
                ALL -> "$DAYS_TO_KEEP_EVENTS"
            }
        }
    }

    companion object {
        const val DAYS_TO_KEEP_EVENTS: Int = 30
    }
}
