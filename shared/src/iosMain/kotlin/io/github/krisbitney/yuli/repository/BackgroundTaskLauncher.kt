package io.github.krisbitney.yuli.repository

import co.touchlab.kermit.Logger
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import platform.BackgroundTasks.BGProcessingTaskRequest
import platform.BackgroundTasks.BGTask
import platform.BackgroundTasks.BGTaskScheduler
import platform.Foundation.NSDate
import platform.Foundation.NSError
import platform.Foundation.NSUUID
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNUserNotificationCenter

actual object BackgroundTaskLauncher {

    private const val updateFollowsTaskIdentifier = "io.github.krisbitney.yuli.updateFollows"
    private const val scheduleUpdateFollowsTaskIdentifier = "io.github.krisbitney.yuli.scheduleUpdateFollows"

    // alert permission = 0x01
    // sound permission = 0x02
    private val alertAndSoundPermission = 0x01.and(0x02).toULong()

    fun requestNotificationPermissions() = UNUserNotificationCenter
        .currentNotificationCenter()
        .requestAuthorizationWithOptions(alertAndSoundPermission) { granted, error ->
            if (error != null) {
                Logger.e(error.localizedDescription, null, "BackgroundTaskLauncher::requestNotificationPermissions")
            }
        }

    fun registerTasks() {
        BGTaskScheduler.sharedScheduler.registerForTaskWithIdentifier(
            updateFollowsTaskIdentifier,
            null
        ) {
            handleUpdateFollowsTask(it!!)
        }
        BGTaskScheduler.sharedScheduler.registerForTaskWithIdentifier(
            scheduleUpdateFollowsTaskIdentifier,
            null
        ) {
            handleUpdateFollowsTask(it!!, true)
        }
    }

    actual fun <AndroidContext> scheduleUpdateFollows(context: AndroidContext){
        val scheduled = Clock.System.now().epochSeconds + UPDATE_FOLLOWS_INTERVAL_SECONDS
        this.updateFollows(scheduleUpdateFollowsTaskIdentifier, NSDate(scheduled.toDouble()))
    }

    actual fun <AndroidContext> updateFollowsAndNotify(context: AndroidContext) {
        this.updateFollows(updateFollowsTaskIdentifier)
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    private fun updateFollows(taskIdentifier: String, scheduled: NSDate? = null) {
        val request = BGProcessingTaskRequest(taskIdentifier)
        request.requiresNetworkConnectivity = true
        scheduled?.let { request.earliestBeginDate = it }
        memScoped {
            val err = alloc<ObjCObjectVar<NSError?>>()
            BGTaskScheduler.sharedScheduler.submitTaskRequest(request, err.ptr)
            if (err.value != null) {
                Logger.e(err.value!!.localizedDescription, null, "BackgroundTaskLauncher::updateFollows")
            }
        }
    }

    // TODO: is it possible to update progress intermittently?
    private fun handleUpdateFollowsTask(task: BGTask, reschedule: Boolean = false) {
        task.expirationHandler = {
            // TODO: Cleanup code
        }
        val result = runBlocking { BackgroundTasks.launchUpdateFollowsTask(null) }

        UNUserNotificationCenter.currentNotificationCenter()
            .getNotificationSettingsWithCompletionHandler { settings ->
                if (settings?.authorizationStatus == 2L || settings?.authorizationStatus == 3L) {
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
                }
            }

        task.setTaskCompletedWithSuccess(result.isSuccess)
        if (reschedule) {
            scheduleUpdateFollows(null)
        }
    }

    // TODO: make notification look good
    private fun notifyOnFinish(isSuccess: Boolean, message: String) {
        val content = UNMutableNotificationContent()
        content.setBody(message)
        if (isSuccess) {
            content.setTitle("Updated Followers!")
        } else {
            content.setTitle("Update Failed")
        }

        val request = UNNotificationRequest.requestWithIdentifier(
            NSUUID().UUIDString,
            content,
            null
        )
        UNUserNotificationCenter.currentNotificationCenter().addNotificationRequest(request, null)
    }
}