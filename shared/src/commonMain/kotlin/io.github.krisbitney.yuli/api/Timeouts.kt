package io.github.krisbitney.yuli.api

import kotlin.random.Random
import kotlin.random.nextLong

val requestTimeout = 6000L
val requestDelay = 1000L

// this is currently only used in Android, but it is located here for salience
fun randomizeDelay(delayMs: Long): Long {
    return Random.nextLong(delayMs - 500L until delayMs + 1500L)
}
