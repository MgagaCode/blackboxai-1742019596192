package com.darweshbhai.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.darweshbhai.MainActivity
import com.darweshbhai.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationService : Service() {

    companion object {
        private const val CHANNEL_ID_REMINDERS = "reminders_channel"
        private const val CHANNEL_ID_FOCUS = "focus_channel"
        private const val CHANNEL_ID_USAGE = "usage_channel"
        private const val CHANNEL_ID_MOTIVATION = "motivation_channel"

        private var notificationId = 2000

        fun createNotificationChannels(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager = 
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                // Reminders Channel
                NotificationChannel(
                    CHANNEL_ID_REMINDERS,
                    "Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Task and activity reminders"
                    enableVibration(true)
                    notificationManager.createNotificationChannel(this)
                }

                // Focus Channel
                NotificationChannel(
                    CHANNEL_ID_FOCUS,
                    "Focus Mode",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Focus mode notifications"
                    enableVibration(false)
                    notificationManager.createNotificationChannel(this)
                }

                // Usage Channel
                NotificationChannel(
                    CHANNEL_ID_USAGE,
                    "Usage Alerts",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "App usage and limit notifications"
                    enableVibration(true)
                    notificationManager.createNotificationChannel(this)
                }

                // Motivation Channel
                NotificationChannel(
                    CHANNEL_ID_MOTIVATION,
                    "Motivation",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Motivational messages and tips"
                    enableVibration(false)
                    notificationManager.createNotificationChannel(this)
                }
            }
        }
    }

    @Inject
    lateinit var preferences: SharedPreferences

    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannels(this)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    fun showTaskReminder(taskTitle: String, taskDescription: String? = null) {
        if (!areNotificationsEnabled()) return

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("destination", "tasks")
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID_REMINDERS)
            .setContentTitle("Task Reminder")
            .setContentText(taskTitle)
            .setStyle(NotificationCompat.BigTextStyle().bigText(
                taskDescription?.let { "$taskTitle\n$it" } ?: taskTitle
            ))
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(getNextNotificationId(), notification)
    }

    fun showFocusReminder(message: String, isBreakTime: Boolean = false) {
        if (!areNotificationsEnabled()) return

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("destination", "focus")
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID_FOCUS)
            .setContentTitle(if (isBreakTime) "Break Time!" else "Focus Time!")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(getNextNotificationId(), notification)
    }

    fun showUsageAlert(appName: String, usageTime: String) {
        if (!areNotificationsEnabled()) return

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("destination", "usage")
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID_USAGE)
            .setContentTitle("Usage Alert")
            .setContentText("You've spent $usageTime on $appName today")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(getNextNotificationId(), notification)
    }

    fun showMotivationalMessage(message: String) {
        if (!areNotificationsEnabled()) return

        val notification = NotificationCompat.Builder(this, CHANNEL_ID_MOTIVATION)
            .setContentTitle("Daily Motivation")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(getNextNotificationId(), notification)
    }

    private fun getNextNotificationId(): Int {
        return notificationId++
    }

    private fun areNotificationsEnabled(): Boolean {
        return preferences.getBoolean("notifications_enabled", true)
    }

    // Helper methods for formatting time and generating motivational messages
    companion object {
        fun formatUsageTime(timeInMillis: Long): String {
            val hours = timeInMillis / (1000 * 60 * 60)
            val minutes = (timeInMillis % (1000 * 60 * 60)) / (1000 * 60)
            return when {
                hours > 0 -> "${hours}h ${minutes}m"
                else -> "${minutes}m"
            }
        }

        private val motivationalMessages = listOf(
            "Take a break, you deserve it!",
            "Stay focused on what matters most.",
            "Small progress is still progress!",
            "Your well-being comes first.",
            "Remember to stretch and hydrate!",
            "You're doing great! Keep it up!",
            "Balance is key to productivity.",
            "Every small step counts!"
        )

        fun getRandomMotivationalMessage(): String {
            return motivationalMessages.random()
        }
    }
}
