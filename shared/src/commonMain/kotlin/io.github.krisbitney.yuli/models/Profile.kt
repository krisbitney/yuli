package io.github.krisbitney.yuli.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

// TODO: follower and following should be based on an enum with 4 possible values
class Profile(
    @PrimaryKey
    var username: String,
    var name: String,
    var picUrl: String,
    var follower: Boolean,
    var following: Boolean,
) : RealmObject {
    constructor() : this("", "", "", false, false)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Profile

        if (username != other.username) return false
        if (name != other.name) return false
        if (picUrl != other.picUrl) return false

        return true
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + picUrl.hashCode()
        return result
    }

    fun copy(
        username: String = this.username,
        name: String = this.name,
        picUrl: String = this.picUrl,
        follower: Boolean = this.follower,
        following: Boolean = this.following,
    ): Profile {
        return Profile(username, name, picUrl, follower, following)
    }

    override fun toString(): String {
        return "Profile(username='$username', name='$name', picUrl='$picUrl', follower=$follower, following=$following)"
    }
}