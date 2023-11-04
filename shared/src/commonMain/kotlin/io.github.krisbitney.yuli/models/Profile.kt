package io.github.krisbitney.yuli.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Profile(
    @PrimaryKey
    var username: String,
    var name: String,
    var follower: Boolean,
    var following: Boolean,
) : RealmObject {
    constructor() : this("", "", false, false)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Profile

        if (username != other.username) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

    fun copy(
        username: String = this.username,
        name: String = this.name,
        follower: Boolean = this.follower,
        following: Boolean = this.following,
    ): Profile {
        return Profile(username, name, follower, following)
    }

    override fun toString(): String {
        return "Profile(username='$username', name='$name', follower=$follower, following=$following)"
    }

    enum class SortBy {
        USERNAME,
        NAME
    }
}