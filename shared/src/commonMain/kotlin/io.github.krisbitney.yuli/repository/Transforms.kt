package io.github.krisbitney.yuli.repository

import io.github.krisbitney.yuli.models.Profile
import io.github.krisbitney.yuli.models.User
import io.github.krisbitney.yuli.database.models.User as DbUser
import io.github.krisbitney.yuli.database.models.Profile as DbProfile

fun User.toDbUser(isLoggedIn: Boolean): DbUser = DbUser(
    username = this.username,
    name = this.name,
    picUrl = this.picUrl,
    followerCount = this.followerCount,
    followingCount = this.followingCount
)

fun DbProfile.toProfile(): Profile = Profile(this.username, this.name, this.picUrl)