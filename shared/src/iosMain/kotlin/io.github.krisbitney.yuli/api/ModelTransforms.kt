package io.github.krisbitney.yuli.api

import io.github.krisbitney.yuli.models.Profile
import io.github.krisbitney.yuli.models.User

fun yuli_ios.User.toUser(): User = User(
    username = this.username ?: "",
    name = this.name ?: "",
    picUrl = this.picUrl ?: "",
    followerCount = this.followerCount.toInt(),
    followingCount = this.followingCount.toInt(),
    mediaCount = this.mediaCount.toInt()
)

fun yuli_ios.Profile.toProfile(): Profile = Profile(
    username = this.username ?: "",
    name = this.name ?: "",
    picUrl = this.picUrl ?: ""
)


