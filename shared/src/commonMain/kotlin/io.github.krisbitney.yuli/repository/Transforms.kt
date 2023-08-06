package io.github.krisbitney.yuli.repository

import io.github.krisbitney.yuli.models.User
import io.github.krisbitney.yuli.models.Profile
import io.github.krisbitney.yuli.models.Event
import io.github.krisbitney.yuli.database.models.User as DbUser
import io.github.krisbitney.yuli.database.models.Profile as DbProfile
import io.github.krisbitney.yuli.database.models.SelectEvents as DbEvent

fun User.toDbUser(): DbUser = DbUser(
    username = this.username,
    name = this.name,
    picUrl = this.picUrl,
    followerCount = this.followerCount,
    followingCount = this.followingCount
)

fun DbUser.toUser(): User = User(
    username = this.username,
    name = this.name,
    picUrl = this.picUrl,
    followerCount = this.followerCount,
    followingCount = this.followingCount
)

fun DbProfile.toProfile(): Profile = Profile(this.username, this.name, this.picUrl)

fun DbEvent.toEvent(): Event = Event(
    profile = Profile(this.username, this.name!!, this.picUrl!!),
    kind = this.kind,
    timestamp = this.timestamp
)
