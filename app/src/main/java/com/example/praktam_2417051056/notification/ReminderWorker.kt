package com.example.praktam_2417051056.notification

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(
    private val context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    companion object {
        const val KEY_NOTIF_ID = "notif_id"
        const val KEY_TITLE    = "title"
        const val KEY_MESSAGE  = "message"
    }

    override fun doWork(): Result {
        val notifId = inputData.getInt(KEY_NOTIF_ID, 0)
        val title   = inputData.getString(KEY_TITLE)   ?: return Result.failure()
        val message = inputData.getString(KEY_MESSAGE) ?: return Result.failure()

        NotificationHelper.showNotification(context, notifId, title, message)
        return Result.success()
    }
}