package com.example.praktam_2417051056.notification

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.praktam_2417051056.model.Event
import com.example.praktam_2417051056.model.Task
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    fun scheduleEventReminders(context: Context, event: Event) {
        cancelEventReminders(context, event.id)

        val startDateTime = parseEventDateTime(event.date, event.startTime) ?: return
        val now           = LocalDateTime.now()

        scheduleIfFuture(
            context       = context,
            triggerAt     = startDateTime.minusMinutes(60),
            now           = now,
            notifId       = event.id * 10 + 1,
            tag           = "event_${event.id}",
            title         = "⏰ Pengingat — 60 Menit Lagi",
            message       = "\"${event.title}\" dimulai pukul ${event.startTime} (${event.category})"
        )

        scheduleIfFuture(
            context       = context,
            triggerAt     = startDateTime.minusMinutes(30),
            now           = now,
            notifId       = event.id * 10 + 2,
            tag           = "event_${event.id}",
            title         = "⏰ Pengingat — 30 Menit Lagi",
            message       = "\"${event.title}\" dimulai pukul ${event.startTime} (${event.category})"
        )
    }

    fun cancelEventReminders(context: Context, eventId: Int) {
        WorkManager.getInstance(context).cancelAllWorkByTag("event_$eventId")
    }

    fun scheduleTaskReminders(context: Context, task: Task) {
        cancelTaskReminders(context, task.id)

        if (task.deadline == "-" || task.isDone) return

        val deadlineDateTime = parseTaskDateTime(task.deadline) ?: return
        val now              = LocalDateTime.now()

        scheduleIfFuture(
            context   = context,
            triggerAt = deadlineDateTime.minusMinutes(60),
            now       = now,
            notifId   = task.id * 10 + 3,
            tag       = "task_${task.id}",
            title     = "📋 Deadline — 60 Menit Lagi",
            message   = "\"${task.title}\" deadline pukul ${task.deadline.substringAfter(" ")} (${task.category})"
        )

        scheduleIfFuture(
            context   = context,
            triggerAt = deadlineDateTime.minusMinutes(30),
            now       = now,
            notifId   = task.id * 10 + 4,
            tag       = "task_${task.id}",
            title     = "📋 Deadline — 30 Menit Lagi",
            message   = "\"${task.title}\" deadline pukul ${task.deadline.substringAfter(" ")} (${task.category})"
        )
    }

    fun cancelTaskReminders(context: Context, taskId: Int) {
        WorkManager.getInstance(context).cancelAllWorkByTag("task_$taskId")
    }


    fun scheduleAllEvents(context: Context, events: List<Event>) {
        events.forEach { scheduleEventReminders(context, it) }
    }

    fun scheduleAllTasks(context: Context, tasks: List<Task>) {
        tasks.forEach { scheduleTaskReminders(context, it) }
    }

    private fun scheduleIfFuture(
        context:   Context,
        triggerAt: LocalDateTime,
        now:       LocalDateTime,
        notifId:   Int,
        tag:       String,
        title:     String,
        message:   String
    ) {
        if (!triggerAt.isAfter(now)) return

        val delayMs = triggerAt
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli() - System.currentTimeMillis()

        if (delayMs <= 0) return

        val data = Data.Builder()
            .putInt(ReminderWorker.KEY_NOTIF_ID, notifId)
            .putString(ReminderWorker.KEY_TITLE,   title)
            .putString(ReminderWorker.KEY_MESSAGE, message)
            .build()

        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag(tag)
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }

    private fun parseEventDateTime(date: String, time: String): LocalDateTime? {
        return try {
            LocalDateTime.parse("$date $time", formatter)
        } catch (e: Exception) { null }
    }

    private fun parseTaskDateTime(deadline: String): LocalDateTime? {
        return try {
            LocalDateTime.parse(deadline, formatter)
        } catch (e: Exception) { null }
    }
}