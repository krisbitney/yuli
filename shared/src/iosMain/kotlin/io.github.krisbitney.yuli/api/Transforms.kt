package io.github.krisbitney.yuli.api

import io.github.krisbitney.yuli.models.Profile
import io.github.krisbitney.yuli.models.User

fun yuli_ios.User.toUser(): User = User(
    username = this.username(),
    name = this.name() ?: "",
    picUrl = this.picUrl() ?: "",
    followerCount = this.followerCount(),
    followingCount = this.followingCount()
)

fun yuli_ios.Profile.toProfile(follower: Boolean = false, following: Boolean = false): Profile = Profile(
    username = this.username(),
    name = this.name() ?: "",
    picUrl = this.picUrl() ?: "",
    follower = follower,
    following = following
)


