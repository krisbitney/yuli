package io.github.krisbitney.yuli.repository

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import platform.BackgroundTasks.BGProcessingTaskRequest
import platform.BackgroundTasks.BGTask
import platform.BackgroundTasks.BGTaskScheduler
import platform.Foundation.NSError
import platform.Foundation.NSUUID
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNUserNotificationCenter

actual object BackgroundTaskLauncher {

    val updateFollowsTaskIdentifier = "io.github.krisbitney.yuli.updateFollows"

    // alert permission = 0x01
    // sound permission = 0x02
    val alertAndSoundPermission = 0x01.and(0x02).toULong()

    fun registerTasks() {
        UNUserNotificationCenter
            .currentNotificationCenter()
            .requestAuthorizationWithOptions(alertAndSoundPermission) { granted, error ->
                // TODO: handle error
            }
        BGTaskScheduler.sharedScheduler.registerForTaskWithIdentifier(
            updateFollowsTaskIdentifier,
            null
        ) {
            CoroutineScope(Dispatchers.Default).launch {
                this@BackgroundTaskLauncher.handleUpdateFollowsTask(it!!)
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    actual fun <C> updateFollowsAndNotify(context: C) {
        val request = BGProcessingTaskRequest(updateFollowsTaskIdentifier)
        request.requiresNetworkConnectivity = true
        memScoped {
            val err = alloc<ObjCObjectVar<NSError?>>()
            BGTaskScheduler.sharedScheduler.submitTaskRequest(request, err.ptr)
            if (err.value != null) {
                throw Exception(err.value!!.localizedDescription)
            }
        }
    }

    private suspend fun handleUpdateFollowsTask(task: BGTask) {
        task.expirationHandler = {
            // TODO: Cleanup code
        }
        val result = BackgroundTasks.launchUpdateFollowsTask(null)
        val message: String = result.getOrElse { it.message ?: "Unknown Error" }

        notifyOnFinish(result.isSuccess, message)

        task.setTaskCompletedWithSuccess(result.isSuccess)
    }

    // TODO: make notification look good
    // TODO: handle isSuccess = false
    private fun notifyOnFinish(isSuccess: Boolean, message: String) {
        val content = UNMutableNotificationContent()
        content.setTitle("Updated Followers!")
        content.setBody(message)

        val request = UNNotificationRequest.requestWithIdentifier(
            NSUUID().UUIDString,
            content,
            null
        )
        UNUserNotificationCenter.currentNotificationCenter().addNotificationRequest(request) { error ->
            // TODO: Handle error
        }
    }
}