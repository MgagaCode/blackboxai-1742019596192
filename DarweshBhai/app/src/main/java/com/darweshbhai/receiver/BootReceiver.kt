package com.darweshbhai.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.darweshbhai.service.AlarmService
import com.darweshbhai.service.NotificationService
import com.darweshbhai.service.UsageMonitoringService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alarmService: AlarmService

    @Inject
    lateinit var notificationService: NotificationService

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Restart services
            restartServices(context)
            
            // Reschedule alarms and reminders
            rescheduleAlarms()
            
            // Show welcome back notification
            showWelcomeBackNotification()
        }
    }

    private fun restartServices(context: Context) {
        // Start Usage Monitoring Service
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(Intent(context, UsageMonitoringService::class.java))
        } else {
            context.startService(Intent(context, UsageMonitoringService::class.java))
        }
    }

    private fun rescheduleAlarms() {
        // Get all saved alarms and reminders
        val alarms = alarmService.getAlarms()
        val reminders = alarmService.getReminders()

        // Reschedule valid alarms (future alarms)
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

        // Reschedule valid reminders (future reminders)
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

    private fun showWelcomeBackNotification() {
        notificationService.showMotivationalMessage(
            "Welcome back! Your digital well-being assistant is ready to help you stay focused."
        )
    }

    companion object {
        fun isBootReceiverEnabled(context: Context): Boolean {
            val pm = context.packageManager
            val receiver = ComponentName(context, BootReceiver::class.java)
            return pm.getComponentEnabledSetting(receiver) != 
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        }

        fun setBootReceiverEnabled(context: Context, enabled: Boolean) {
            val pm = context.packageManager
            val receiver = ComponentName(context, BootReceiver::class.java)
            pm.setComponentEnabledSetting(
                receiver,
                if (enabled) 
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED 
                else 
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
        }
    }
}
