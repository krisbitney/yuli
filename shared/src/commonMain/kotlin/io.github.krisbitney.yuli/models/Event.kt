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
        FAN_TO_MUTUAL,
        FAN_TO_NONE,
        NONFOLLOWER_TO_MUTUAL,
        NONFOLLOWER_TO_NONE,
        MUTUAL_TO_NONFOLLOWER,
        MUTUAL_TO_FAN,
        NONE_TO_NONFOLLOWER,
        NONE_TO_FAN,
        NONFOLLOWER_TO_FAN,
        FAN_TO_NONFOLLOWER,
        NONE_TO_MUTUAL,
        MUTUAL_TO_NONE
    }
    
    fun message(): String  {
        val name = profile?.name ?: "Someone"
        return when (this.kind) {
            Kind.FAN_TO_MUTUAL -> "You followed $name back"
            Kind.FAN_TO_NONE -> "$name unfollowed you"
            Kind.NONFOLLOWER_TO_MUTUAL -> "$name followed you back"
            Kind.NONFOLLOWER_TO_NONE -> "You unfollowed $name"
            Kind.MUTUAL_TO_NONFOLLOWER -> "$name unfollowed you"
            Kind.MUTUAL_TO_FAN -> "You unfollowed $name"
            Kind.NONE_TO_NONFOLLOWER -> "You followed $name"
            Kind.NONE_TO_FAN -> "$name followed you"
            Kind.NONFOLLOWER_TO_FAN -> "You unfollowed $name and they followed you"
            Kind.FAN_TO_NONFOLLOWER -> "You followed $name and they unfollowed you"
            Kind.NONE_TO_MUTUAL -> "You and $name began following each other"
            Kind.MUTUAL_TO_NONE -> "You and $name unfollowed each other"
        }
    }
}
