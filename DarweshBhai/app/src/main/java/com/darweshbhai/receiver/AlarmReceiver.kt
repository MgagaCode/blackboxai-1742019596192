package com.darweshbhai.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.darweshbhai.service.AlarmService
import com.darweshbhai.service.NotificationService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationService: NotificationService

    @Inject
    lateinit var alarmService: AlarmService

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getIntExtra(AlarmService.EXTRA_ALARM_ID, -1)
        val title = intent.getStringExtra(AlarmService.EXTRA_ALARM_TITLE) ?: "Reminder"
        val description = intent.getStringExtra(AlarmService.EXTRA_ALARM_DESCRIPTION) ?: ""
        val isRepeating = intent.getBooleanExtra(AlarmService.EXTRA_IS_REPEATING, false)
        val repeatInterval = intent.getLongExtra(AlarmService.EXTRA_REPEAT_INTERVAL, 0)
        val isReminder = intent.getBooleanExtra(AlarmService.EXTRA_IS_REMINDER, false)

        when {
            // Handle device boot
            intent.action == Intent.ACTION_BOOT_COMPLETED -> {
                handleDeviceBoot(context)
            }
            // Handle reminder
            isReminder -> {
                notificationService.showTaskReminder(title, description)
                // Remove one-time reminder from storage
                alarmService.cancelReminder(alarmId)
            }
            // Handle alarm
            else -> {
                notificationService.showTaskReminder(title, description)
                
                // If it's a repeating alarm, schedule the next occurrence
                if (isRepeating && repeatInterval > 0) {
                    scheduleNextAlarm(alarmId, title, description, repeatInterval)
                } else {
                    // Remove one-time alarm from storage
                    alarmService.cancelAlarm(alarmId)
                }
            }
        }
    }

    private fun handleDeviceBoot(context: Context) {
        // Reschedule all saved alarms and reminders after device boot
        val alarms = alarmService.getAlarms()
        val reminders = alarmService.getReminders()

        // Reschedule alarms
        alarms.forEach { alarm ->
            if (alarm.time > System.currentTimeMillis()) {
                alarmService.scheduleAlarm(
                    alarmTime = alarm.time,
                    title = alarm.title,
                    description = alarm.description,
                    isRepeating = alarm.isRepeating,
                    repeatInterval = alarm.repeatInterval,
                    alarmId = alarm.id
                )
            }
        }

        // Reschedule reminders
        reminders.forEach { reminder ->
            if (reminder.time > System.currentTimeMillis()) {
                alarmService.scheduleReminder(
                    reminderTime = reminder.time,
                    title = reminder.title,
                    description = reminder.description,
                    reminderId = reminder.id
                )
            }
        }
    }

    private fun scheduleNextAlarm(
        alarmId: Int,
        title: String,
        description: String,
        repeatInterval: Long
    ) {
        val nextAlarmTime = System.currentTimeMillis() + repeatInterval
        
        alarmService.scheduleAlarm(
            alarmTime = nextAlarmTime,
            title = title,
            description = description,
            isRepeating = true,
            repeatInterval = repeatInterval,
            alarmId = alarmId
        )
    }

    companion object {
        fun getPendingIntent(
            context: Context,
            alarmId: Int,
            title: String,
            description: String = "",
            isRepeating: Boolean = false,
            repeatInterval: Long = 0,
            isReminder: Boolean = false
        ): android.app.PendingIntent {
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra(AlarmService.EXTRA_ALARM_ID, alarmId)
                putExtra(AlarmService.EXTRA_ALARM_TITLE, title)
                putExtra(AlarmService.EXTRA_ALARM_DESCRIPTION, description)
                putExtra(AlarmService.EXTRA_IS_REPEATING, isRepeating)
                putExtra(AlarmService.EXTRA_REPEAT_INTERVAL, repeatInterval)
                putExtra(AlarmService.EXTRA_IS_REMINDER, isReminder)
            }

            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or 
                android.app.PendingIntent.FLAG_IMMUTABLE
            } else {
                android.app.PendingIntent.FLAG_UPDATE_CURRENT
            }

            return android.app.PendingIntent.getBroadcast(
                context,
                alarmId,
                intent,
                flags
            )
        }
    }
}
