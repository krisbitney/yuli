package io.github.krisbitney.yuli.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class User(
    @PrimaryKey
    var username: String,
    var name: String,
    var picUrl: String,
    var followerCount: Long,
    var followingCount: Long,
) : RealmObject {
    constructor() : this("", "", "", 0, 0)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as User

        if (username != other.username) return false
        if (name != other.name) return false
        if (picUrl != other.picUrl) return false
        if (followerCount != other.followerCount) return false
        if (followingCount != other.followingCount) return false

        return true
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + picUrl.hashCode()
        result = 31 * result + followerCount.hashCode()
        result = 31 * result + followingCount.hashCode()
        return result
    }

    override fun toString(): String {
        return "User(username='$username', name='$name', picUrl='$picUrl', followerCount=$followerCount, followingCount=$followingCount)"
    }


}