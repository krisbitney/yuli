package io.github.krisbitney.yuli.state.history.integration

import io.github.krisbitney.yuli.database.YuliDatabase
import io.github.krisbitney.yuli.models.Event
import io.github.krisbitney.yuli.state.history.store.YuliHistoryStoreProvider
import kotlinx.datetime.Instant

@OptIn(ExperimentalStdlibApi::class)
class YuliHistoryStoreDatabase(private val database: YuliDatabase) : YuliHistoryStoreProvider.Database {
    override suspend fun selectEvents(timePeriod: Event.TimePeriod): List<Event> {
        return when (timePeriod) {
            Event.TimePeriod.ONE_DAY -> database.selectEvents(
                database.daysAgoUnixTimestamp(1),
                Instant.DISTANT_FUTURE.epochSeconds
            )
            Event.TimePeriod.SEVEN_DAYS -> database.selectEvents(
                database.daysAgoUnixTimestamp(7),
                Instant.DISTANT_FUTURE.epochSeconds
            )
            Event.TimePeriod.ALL -> database.selectEvents(
                Instant.DISTANT_PAST.epochSeconds,
                Instant.DISTANT_FUTURE.epochSeconds
            )
        }
    }
}