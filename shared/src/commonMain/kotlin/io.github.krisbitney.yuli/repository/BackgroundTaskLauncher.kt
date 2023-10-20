package io.github.krisbitney.yuli.repository

import io.github.krisbitney.yuli.api.SocialApiFactory
import io.github.krisbitney.yuli.database.YuliDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

expect object BackgroundTaskLauncher {
    fun <AndroidContext> updateFollowsAndNotify(context: AndroidContext)
}

object BackgroundTasks {
    @OptIn(ExperimentalStdlibApi::class)
    suspend fun <AndroidContext> launchUpdateFollowsTask(context: AndroidContext, reportProgress: suspend (message: String) -> Unit = {}): Result<ApiHandler.UpdateFollowsSummary> = withContext(Dispatchers.IO) {
        YuliDatabase().use { db ->
            val api = SocialApiFactory.get(context)
            val username = db.selectUser()?.username
            if (username == null) {
                val e = Exception("No user logged in")
                return@use Result.failure(e)
            }
            ApiHandler(api, db).updateFollows(username, reportProgress)
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