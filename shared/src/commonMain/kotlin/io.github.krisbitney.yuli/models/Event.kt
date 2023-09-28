package io.github.krisbitney.yuli.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Event(
    var profile: Profile?,
    var kindOrdinal: Int,
    var timestamp: Long
) : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()

    constructor() : this(null, 0, 0)
    constructor(profile: Profile?, kind: Kind, timestamp: Long) : this(profile, kind.ordinal, timestamp)

    var kind: Kind
        get() = Kind.values()[kindOrdinal]
        set(value) { kindOrdinal = value.ordinal }

    enum class Kind {
        GAINED_FOLLOWER,
        LOST_FOLLOWER,
        STARTED_FOLLOWING,
        STOPPED_FOLLOWING
    }
    
    fun message(): String  {
        val name = profile?.name ?: "Someone"
        return when (this.kind) {
            Kind.GAINED_FOLLOWER -> "$name followed you"
            Kind.LOST_FOLLOWER -> "$name unfollowed you"
            Kind.STARTED_FOLLOWING -> "You followed $name"
            Kind.STOPPED_FOLLOWING -> "You unfollowed $name"
        }
    }
}
