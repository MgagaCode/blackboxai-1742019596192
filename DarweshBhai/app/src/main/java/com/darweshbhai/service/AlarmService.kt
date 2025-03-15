package com.darweshbhai.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import com.darweshbhai.receiver.AlarmReceiver
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class AlarmService : Service() {

    @Inject
    lateinit var preferences: SharedPreferences

    private lateinit var alarmManager: AlarmManager

    override fun onCreate() {
        super.onCreate()
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    override fun onBind(intent: Intent?): IBinder? = null

    fun scheduleAlarm(
        alarmTime: Long,
        title: String,
        description: String = "",
        isRepeating: Boolean = false,
        repeatInterval: Long = 0,
        alarmId: Int = generateAlarmId()
    ) {
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_ALARM_ID, alarmId)
            putExtra(EXTRA_ALARM_TITLE, title)
            putExtra(EXTRA_ALARM_DESCRIPTION, description)
            putExtra(EXTRA_IS_REPEATING, isRepeating)
            putExtra(EXTRA_REPEAT_INTERVAL, repeatInterval)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            alarmId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && 
            !alarmManager.canScheduleExactAlarms() -> {
                // Handle case where exact alarms are not permitted
                // You might want to show a notification or prompt user to enable permission
                return
            }
            isRepeating -> {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    alarmTime,
                    repeatInterval,
                    pendingIntent
                )
            }
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        alarmTime,
                        pendingIntent
                    )
                } else {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        alarmTime,
                        pendingIntent
                    )
                }
            }
        }

        // Save alarm details
        saveAlarm(Alarm(
            id = alarmId,
            time = alarmTime,
            title = title,
            description = description,
            isRepeating = isRepeating,
            repeatInterval = repeatInterval
        ))
    }

    fun cancelAlarm(alarmId: Int) {
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            alarmId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        removeAlarm(alarmId)
    }

    fun scheduleReminder(
        reminderTime: Long,
        title: String,
        description: String = "",
        reminderId: Int = generateAlarmId()
    ) {
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_ALARM_ID, reminderId)
            putExtra(EXTRA_ALARM_TITLE, title)
            putExtra(EXTRA_ALARM_DESCRIPTION, description)
            putExtra(EXTRA_IS_REMINDER, true)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            reminderId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminderTime,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                reminderTime,
                pendingIntent
            )
        }

        // Save reminder details
        saveReminder(Reminder(
            id = reminderId,
            time = reminderTime,
            title = title,
            description = description
        ))
    }

    fun cancelReminder(reminderId: Int) {
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            reminderId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        removeReminder(reminderId)
    }

    private fun generateAlarmId(): Int {
        return SystemClock.uptimeMillis().toInt()
    }

    private fun saveAlarm(alarm: Alarm) {
        val alarms = getAlarms().toMutableList()
        alarms.add(alarm)
        preferences.edit().putString("alarms", alarm.toJson()).apply()
    }

    private fun removeAlarm(alarmId: Int) {
        val alarms = getAlarms().toMutableList()
        alarms.removeAll { it.id == alarmId }
        preferences.edit().putString("alarms", alarms.toJson()).apply()
    }

    private fun saveReminder(reminder: Reminder) {
        val reminders = getReminders().toMutableList()
        reminders.add(reminder)
        preferences.edit().putString("reminders", reminders.toJson()).apply()
    }

    private fun removeReminder(reminderId: Int) {
        val reminders = getReminders().toMutableList()
        reminders.removeAll { it.id == reminderId }
        preferences.edit().putString("reminders", reminders.toJson()).apply()
    }

    fun getAlarms(): List<Alarm> {
        val alarmsJson = preferences.getString("alarms", "[]") ?: "[]"
        return Alarm.fromJson(alarmsJson)
    }

    fun getReminders(): List<Reminder> {
        val remindersJson = preferences.getString("reminders", "[]") ?: "[]"
        return Reminder.fromJson(remindersJson)
    }

    data class Alarm(
        val id: Int,
        val time: Long,
        val title: String,
        val description: String,
        val isRepeating: Boolean,
        val repeatInterval: Long
    )

    data class Reminder(
        val id: Int,
        val time: Long,
        val title: String,
        val description: String
    )

    companion object {
        const val EXTRA_ALARM_ID = "alarm_id"
        const val EXTRA_ALARM_TITLE = "alarm_title"
        const val EXTRA_ALARM_DESCRIPTION = "alarm_description"
        const val EXTRA_IS_REPEATING = "is_repeating"
        const val EXTRA_REPEAT_INTERVAL = "repeat_interval"
        const val EXTRA_IS_REMINDER = "is_reminder"

        // Repeat intervals
        const val REPEAT_DAILY = AlarmManager.INTERVAL_DAY
        const val REPEAT_WEEKLY = AlarmManager.INTERVAL_DAY * 7
        const val REPEAT_MONTHLY = AlarmManager.INTERVAL_DAY * 30
    }
}
