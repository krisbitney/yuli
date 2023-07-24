package api

import models.Profile
import models.User
import com.github.instagram4j.instagram4j.models.user.User as jvmUser
import com.github.instagram4j.instagram4j.models.user.Profile as jvmProfile

fun jvmUser.toUser(): User = User(
    username = this.username,
    name = this.full_name,
    picUrl = this.profile_pic_url,
    followerCount = this.follower_count,
    followingCount = this.following_count,
    mediaCount = this.media_count
)

fun jvmProfile.toProfile(): Profile = Profile(
    username = this.username,
    name = this.full_name,
    picUrl = this.profile_pic_url
)