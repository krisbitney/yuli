package io.github.krisbitney.yuli.repository

import platform.Foundation.NSUUID
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNUserNotificationCenter

actual object BackgroundTaskLauncher {

    // alert permission = 0x01
    // sound permission = 0x02
    private val alertAndSoundPermission = 0x01.and(0x02).toULong()

    fun requestNotificationPermissions() = UNUserNotificationCenter
        .currentNotificationCenter()
        .requestAuthorizationWithOptions(alertAndSoundPermission) { granted, error -> }

    actual fun <AndroidContext> scheduleUpdateFollows(context: AndroidContext){
        throw NotImplementedError("BackgroundTaskLauncher::scheduleUpdateFollows is not implemented on iOS")
    }

    actual suspend fun <AndroidContext> updateFollowsAndNotify(context: AndroidContext) {
        val result = BackgroundTasks.launchUpdateFollowsTask(null)

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