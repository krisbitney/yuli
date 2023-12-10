package io.github.krisbitney.yuli.repository

import io.github.krisbitney.yuli.api.SocialApiFactory
import io.github.krisbitney.yuli.database.YuliDatabase
import io.github.krisbitney.yuli.models.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

const val UPDATE_FOLLOWS_INTERVAL_SECONDS: Long = 60 * 60 * 6 // 6 hours

expect object BackgroundTaskLauncher {
    suspend fun <AndroidContext> updateFollowsAndNotify(context: AndroidContext)
    fun <AndroidContext> scheduleUpdateFollows(context: AndroidContext)
}

object BackgroundTasks {

    // TODO: need to inform the user if this fails
    @OptIn(ExperimentalStdlibApi::class)
    suspend fun <AndroidContext> launchUpdateFollowsTask(context: AndroidContext, reportProgress: suspend (message: String) -> Unit = {}): Result<ApiHandler.UpdateFollowsSummary> = withContext(Dispatchers.IO) {
        YuliDatabase().use { db ->
            val api = SocialApiFactory.get(context)
            val result = ApiHandler(api, db).updateFollows(reportProgress)
            if (result.isSuccess) {
                // clear old events to keep app storage down
                val oldEvents = db.selectEvents(
                    Instant.DISTANT_PAST.epochSeconds,
                    db.daysAgoUnixTimestamp(Event.DAYS_TO_KEEP_EVENTS)
                )
                db.deleteEvents(oldEvents)
            }
            // record update time
            db.selectState()?.let {
                db.insertOrReplaceState(it.copy(lastUpdate = Clock.System.now().epochSeconds))
            }
            result
        }
    }

    fun createUpdateFollowsNotificationMessage(summary: ApiHandler.UpdateFollowsSummary): String? {
        return if (summary.gainedFollowers == 0 && summary.lostFollowers == 0) {
            null
        } else if (summary.gainedFollowers == 0) {
            "${summary.lostFollowers} unfollowed you since the last update."
        } else if (summary.lostFollowers == 0) {
            "You've gained ${summary.gainedFollowers} followers!"
        } else {
            "Since the last update, ${summary.gainedFollowers} people followed you and ${summary.lostFollowers} unfollowed you."
        }
    }
}