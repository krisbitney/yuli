package io.github.krisbitney.yuli.api

import kotlin.random.Random
import kotlin.random.nextLong

val requestTimeout = 6000L
val pageDelay = 4400L

// this is only used in Android, but it is located here for salience
fun randomizePageDelay(pageDelay: Long): Long {
    return Random.nextLong(pageDelay - 1500L until pageDelay + 1500L)
}