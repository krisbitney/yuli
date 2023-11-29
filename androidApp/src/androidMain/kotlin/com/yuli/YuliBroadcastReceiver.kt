package com.yuli

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.github.krisbitney.yuli.repository.BackgroundTaskLauncher

class YuliBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            BackgroundTaskLauncher.scheduleUpdateFollows(context)
        }
    }
}