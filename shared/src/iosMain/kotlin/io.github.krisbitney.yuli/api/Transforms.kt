package io.github.krisbitney.yuli.api

import io.github.krisbitney.yuli.models.Profile
import io.github.krisbitney.yuli.models.User
import cocoapods.yuli_ios.Profile as SwiftProfile
import cocoapods.yuli_ios.User as SwiftUser

fun SwiftUser.toUser(): User = User(
    username = this.username(),
    name = this.name() ?: "",
    picUrl = this.picUrl() ?: "",
    followerCount = this.followerCount(),
    followingCount = this.followingCount()
)

fun SwiftProfile.toProfile(follower: Boolean = false, following: Boolean = false): Profile = Profile(
    username = this.username(),
    name = this.name() ?: "",
    picUrl = this.picUrl() ?: "",
    follower = follower,
    following = following
)


