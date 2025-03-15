package com.darweshbhai.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.darweshbhai.data.entity.ReminderPriority
import com.darweshbhai.ui.DarweshBhaiApp

object NotificationUtils {

    // Notification Channels
    const val CHANNEL_FOCUS_SESSION = "focus_session"
    const val CHANNEL_TASK_REMINDER = "task_reminder"
    const val CHANNEL_APP_LIMIT = "app_limit"
    const val CHANNEL_PRODUCTIVITY = "productivity"

    // Notification IDs
    private const val NOTIFICATION_ID_FOCUS_SESSION = 1000
    private const val NOTIFICATION_ID_TASK_REMINDER = 2000
    private const val NOTIFICATION_ID_APP_LIMIT = 3000
    private const val NOTIFICATION_ID_PRODUCTIVITY = 4000

    // Actions
    const val ACTION_START_BREAK = "com.darweshbhai.action.START_BREAK"
    const val ACTION_SKIP_BREAK = "com.darweshbhai.action.SKIP_BREAK"
    const val ACTION_STOP_SESSION = "com.darweshbhai.action.STOP_SESSION"
    const val ACTION_EXTEND_TIME = "com.darweshbhai.action.EXTEND_TIME"
    const val ACTION_COMPLETE_TASK = "com.darweshbhai.action.COMPLETE_TASK"
    const val ACTION_SNOOZE_REMINDER = "com.darweshbhai.action.SNOOZE_REMINDER"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    CHANNEL_FOCUS_SESSION,
                    "Focus Sessions",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notifications for active focus sessions"
                    setShowBadge(true)
                },
                NotificationChannel(
                    CHANNEL_TASK_REMINDER,
                    "Task Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Reminders for tasks and deadlines"
                    setShowBadge(true)
                },
                NotificationChannel(
                    CHANNEL_APP_LIMIT,
                    "App Usage Limits",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Alerts when app usage limits are reached"
                    setShowBadge(true)
                },
                NotificationChannel(
                    CHANNEL_PRODUCTIVITY,
                    "Productivity Updates",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "General productivity insights and updates"
                    setShowBadge(false)
                }
            )

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannels(channels)
        }
    }

    fun showFocusSessionNotification(
        context: Context,
        title: String,
        message: String,
        progress: Int? = null,
        showActions: Boolean = true
    ) {
        val builder = NotificationCompat.Builder(context, CHANNEL_FOCUS_SESSION)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setAutoCancel(false)

        // Add progress if provided
        progress?.let {
            builder.setProgress(100, it, false)
        }

        // Add actions if requested
        if (showActions) {
            builder.addAction(
                createNotificationAction(
                    context,
                    android.R.drawable.ic_media_pause,
                    "Stop",
                    ACTION_STOP_SESSION
                )
            )
        }

        NotificationManagerCompat.from(context)
            .notify(NOTIFICATION_ID_FOCUS_SESSION, builder.build())
    }

    fun showTaskReminderNotification(
        context: Context,
        taskId: String,
        title: String,
        message: String,
        priority: ReminderPriority
    ) {
        val builder = NotificationCompat.Builder(context, CHANNEL_TASK_REMINDER)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(priority.toNotificationPriority())
            .setAutoCancel(true)
            .addAction(
                createNotificationAction(
                    context,
                    android.R.drawable.ic_menu_done,
                    "Complete",
                    ACTION_COMPLETE_TASK,
                    taskId
                )
            )
            .addAction(
                createNotificationAction(
                    context,
                    android.R.drawable.ic_popup_reminder,
                    "Snooze",
                    ACTION_SNOOZE_REMINDER,
                    taskId
                )
            )

        NotificationManagerCompat.from(context)
            .notify("${NOTIFICATION_ID_TASK_REMINDER}_$taskId".hashCode(), builder.build())
    }

    fun showAppLimitNotification(
        context: Context,
        appName: String,
        packageName: String,
        timeUsed: Long
    ) {
        val builder = NotificationCompat.Builder(context, CHANNEL_APP_LIMIT)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Time Limit Reached")
            .setContentText("You've spent ${DateTimeUtils.formatDuration(timeUsed)} on $appName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(
                createNotificationAction(
                    context,
                    android.R.drawable.ic_menu_add,
                    "Extend Time",
                    ACTION_EXTEND_TIME,
                    packageName
                )
            )

        NotificationManagerCompat.from(context)
            .notify("${NOTIFICATION_ID_APP_LIMIT}_$packageName".hashCode(), builder.build())
    }

    fun showProductivityUpdateNotification(
        context: Context,
        title: String,
        message: String
    ) {
        val builder = NotificationCompat.Builder(context, CHANNEL_PRODUCTIVITY)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context)
            .notify(NOTIFICATION_ID_PRODUCTIVITY, builder.build())
    }

    private fun createNotificationAction(
        context: Context,
        icon: Int,
        title: String,
        action: String,
        extraValue: String? = null
    ): NotificationCompat.Action {
        val intent = Intent(context, DarweshBhaiApp::class.java).apply {
            this.action = action
            extraValue?.let { putExtra("extra_value", it) }
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Action.Builder(icon, title, pendingIntent).build()
    }

    fun cancelNotification(context: Context, notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }

    fun cancelAllNotifications(context: Context) {
        NotificationManagerCompat.from(context).cancelAll()
    }

    private fun ReminderPriority.toNotificationPriority(): Int {
        return when (this) {
            ReminderPriority.LOW -> NotificationCompat.PRIORITY_LOW
            ReminderPriority.NORMAL -> NotificationCompat.PRIORITY_DEFAULT
            ReminderPriority.HIGH -> NotificationCompat.PRIORITY_HIGH
            ReminderPriority.URGENT -> NotificationCompat.PRIORITY_MAX
        }
    }
}
