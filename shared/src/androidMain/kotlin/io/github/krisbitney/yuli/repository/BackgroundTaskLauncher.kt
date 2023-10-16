package io.github.krisbitney.yuli.repository

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters

internal actual object BackgroundTaskLauncher {
    actual fun <AndroidContext> updateFollowsAndNotify(context: AndroidContext) {
        val workRequest = OneTimeWorkRequestBuilder<UpdateFollowsWorker>().build()
        WorkManager.getInstance(context as Context).enqueue(workRequest)
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
        setForeground(createForegroundInfo("Downloading Follows"))
        val result = BackgroundTasks.launchUpdateFollowsTask(applicationContext)
        val message: String = result.getOrElse { it.message ?: "Unknown Error" }

        notifyOnFinish(result.isSuccess, message)

        return if (result.isSuccess) Result.success() else Result.failure()
    }

    // TODO: update progress in updateFollows
    private fun createForegroundInfo(progress: String): ForegroundInfo {
        // This PendingIntent can be used to cancel the worker
        val intent = WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)

        val notification = NotificationCompat.Builder(applicationContext, notificationChannelID)
            .setContentTitle("Updating Follows")
            .setTicker("Updating Follows")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentText(progress)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_delete, "Cancel", intent)
            .build()

        return ForegroundInfo(0, notification)
    }

    // TODO: make notification look good
    // TODO: handle isSuccess = false
    @SuppressLint("MissingPermission")
    private fun notifyOnFinish(isSuccess: Boolean, message: String) {
        val notification = NotificationCompat.Builder(applicationContext, notificationChannelID)
            .setContentTitle("Updated Followers!")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.btn_star_big_on)
            .build()
        NotificationManagerCompat.from(applicationContext).notify(1, notification)
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