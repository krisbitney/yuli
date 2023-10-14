package io.github.krisbitney.yuli.repository

import io.github.krisbitney.yuli.api.SocialApiFactory
import io.github.krisbitney.yuli.database.YuliDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

expect object BackgroundTaskLauncher {
    fun <C> updateFollowsAndNotify(context: C)
}

object BackgroundTasks {
    @OptIn(ExperimentalStdlibApi::class)
    suspend fun <C> launchUpdateFollowsTask(context: C): Result<String> = withContext(Dispatchers.IO) {
        YuliDatabase().use { db ->
            val api = SocialApiFactory.get(context)
            val username = db.selectUser()?.username
            if (username == null) {
                val e = Exception("No user logged in")
                return@use Result.failure(e)
            }
            // TODO: use message from api
            val updateResult = ApiHandler(api, db).updateFollows(username).getOrElse {
                return@use Result.failure(it)
            }
            return@use Result.success("Follows Updated!")
        }
    }
}