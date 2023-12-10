package io.github.krisbitney.yuli.repository

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

actual object BackgroundTaskLauncher {

    actual suspend fun <AndroidContext> updateFollowsAndNotify(context: AndroidContext) {
        val workRequest = OneTimeWorkRequestBuilder<UpdateFollowsWorker>().build()
        WorkManager.getInstance(context as Context).enqueue(workRequest)
    }

    actual fun <AndroidContext> scheduleUpdateFollows(context: AndroidContext) {
        val workRequest = PeriodicWorkRequestBuilder<UpdateFollowsWorker>(UPDATE_FOLLOWS_INTERVAL_SECONDS, TimeUnit.SECONDS)
            .setInitialDelay(UPDATE_FOLLOWS_INTERVAL_SECONDS, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(context as Context).enqueueUniquePeriodicWork(
            "YuliUpdateFollowsWork",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}

class UpdateFollowsWorker(
    appContext: Context,
    workerParams: WorkerParameters
): CoroutineWorker(appContext, workerParams) {

    private val notificationChannelID = "yuli_follows"
    private val notificationManager =
        appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result {
        createNotificationChannel()
        setForeground(createForegroundInfo("Checking for updates..."))
        val result = BackgroundTasks.launchUpdateFollowsTask(applicationContext) {
            setForeground(createForegroundInfo(it))
        }

        if (result.isSuccess) {
            val updateFollowsSummary = result.getOrThrow()
            val notificationMessage = BackgroundTasks.createUpdateFollowsNotificationMessage(updateFollowsSummary)
            if (notificationMessage != null) {
                notifyOnFinish(true, notificationMessage)
            }
        } else {
            val notificationMessage = result.exceptionOrNull()?.message ?: "Unknown error"
            notifyOnFinish(false, notificationMessage)
        }

        return if (result.isSuccess) Result.success() else Result.failure()
    }

    private fun createForegroundInfo(progress: String): ForegroundInfo {
        // This can be used to cancel the worker
        val intent = WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)

        val notification = NotificationCompat.Builder(applicationContext, notificationChannelID)
            .setContentTitle("Updating Followers")
            .setTicker("Checking for updates...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentText(progress)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_delete, "Cancel", intent)
            .build()

        return ForegroundInfo(0, notification)
    }

    // TODO: make notification look good
    @SuppressLint("MissingPermission")
    private fun notifyOnFinish(isSuccess: Boolean, message: String) {
        val notification = NotificationCompat.Builder(applicationContext, notificationChannelID)
            .setTicker(message)
            .setContentText(message)
        if (isSuccess) {
            notification
                .setContentTitle("Updated Followers!")
                .setSmallIcon(android.R.drawable.btn_star_big_on)
        } else {
            notification
                .setContentTitle("Update Failed")
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
        }

        NotificationManagerCompat.from(applicationContext).notify(1, notification.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannelID,
                "yuli_follows",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
}