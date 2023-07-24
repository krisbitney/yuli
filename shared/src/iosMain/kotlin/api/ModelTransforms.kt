package api

import models.Profile
import models.User

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


