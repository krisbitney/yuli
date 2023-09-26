package io.github.krisbitney.yuli.api

import io.github.krisbitney.yuli.models.Profile
import io.github.krisbitney.yuli.models.User
import io.realm.kotlin.ext.toRealmList
import com.github.instagram4j.instagram4j.models.user.User as jvmUser
import com.github.instagram4j.instagram4j.models.user.Profile as jvmProfile

suspend fun jvmUser.toUser(): User = User(
    username = this.username,
    name = this.full_name,
    pic = downloadImage(this.profile_pic_url).getOrNull()?.toList()?.toRealmList()
)

fun jvmProfile.toProfile(follower: Boolean = false, following: Boolean = false): Profile = Profile(
    username = this.username,
    name = this.full_name,
    follower = follower,
    following = following
)