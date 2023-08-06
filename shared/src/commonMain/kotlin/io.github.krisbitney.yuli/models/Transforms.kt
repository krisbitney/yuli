package io.github.krisbitney.yuli.models

import io.github.krisbitney.yuli.database.models.User as DbUser
import io.github.krisbitney.yuli.database.models.Profile as DbProfile

fun User.toDbUser(isLoggedIn: Boolean): DbUser = DbUser(
    username = this.username,
    name = this.name,
    picUrl = this.picUrl,
    followerCount = this.followerCount,
    followingCount = this.followingCount,
    isLoggedIn = isLoggedIn
)

fun DbProfile.toProfile(): Profile = Profile(this.username, this.name, this.picUrl)