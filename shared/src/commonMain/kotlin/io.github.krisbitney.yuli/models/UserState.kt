package io.github.krisbitney.yuli.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class UserState(
    @PrimaryKey
    var username: String,
    var isLoggedIn: Boolean,
    var isLocked: Boolean
) : RealmObject {
    constructor() : this("", false, false)

    fun copy(
        username: String = this.username,
        isLoggedIn: Boolean = this.isLoggedIn,
        isLocked: Boolean = this.isLocked
    ): UserState {
        return UserState(username, isLoggedIn, isLocked)
    }
}