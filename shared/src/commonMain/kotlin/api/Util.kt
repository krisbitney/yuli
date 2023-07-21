package api

import kotlinx.coroutines.delay
import kotlin.random.Random
import kotlin.random.nextLong

val requestDelayRange = 3000L until 7000L

suspend fun requestDelay() = delay(Random.nextLong(requestDelayRange))