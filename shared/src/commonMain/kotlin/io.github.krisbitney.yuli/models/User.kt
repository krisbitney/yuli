package io.github.krisbitney.yuli.models

data class User(
    val username: String,
    val name: String,
    val picUrl: String,
    val followerCount: Int,
    val followingCount: Int,
    val mediaCount: Int
)