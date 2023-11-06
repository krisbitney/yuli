package io.github.krisbitney.yuli.api

import io.github.krisbitney.yuli.models.Profile
import io.github.krisbitney.yuli.models.User
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.ext.toRealmList
import kotlinx.cinterop.ExperimentalForeignApi
import cocoapods.yuli_ios.Profile as SwiftProfile
import cocoapods.yuli_ios.User as SwiftUser

@OptIn(ExperimentalForeignApi::class)
suspend fun SwiftUser.toUser(): User = User(
    username = this.username(),
    name = this.name() ?: "",
    pic = this.picUrl()?.let {
        downloadImage(it).getOrNull()?.toList()?.toRealmList()
    } ?: realmListOf()
)

@OptIn(ExperimentalForeignApi::class)
fun SwiftProfile.toProfile(follower: Boolean = false, following: Boolean = false): Profile = Profile(
    username = this.username(),
    name = this.name() ?: "",
    follower = follower,
    following = following
)


