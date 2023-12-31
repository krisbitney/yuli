package io.github.krisbitney.yuli.models

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.serializers.RealmListKSerializer
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
class User(
    @PrimaryKey
    var username: String,
    var name: String,
    @Serializable(RealmListKSerializer::class)
    var pic: RealmList<Byte>
) : RealmObject {
    constructor() : this("", "", realmListOf())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as User

        if (username != other.username) return false
        if (name != other.name) return false
        if (pic != other.pic) return false

        return true
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + pic.hashCode()
        return result
    }

    override fun toString(): String {
        return "User(username='$username', name='$name', pic='$pic')"
    }
}