package com.darweshbhai.service

import android.accessibilityservice.AccessibilityService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import androidx.core.app.NotificationCompat
import com.darweshbhai.MainActivity
import com.darweshbhai.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

@AndroidEntryPoint
class UsageMonitoringService : AccessibilityService() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private var usageStatsManager: UsageStatsManager? = null
    private var notificationManager: NotificationManager? = null
    private val appLimits = HashMap<String, Long>() // packageName to milliseconds
    private val NOTIFICATION_CHANNEL_ID = "usage_monitoring_channel"
    private val NOTIFICATION_ID = 1001

    @Inject
    lateinit var preferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
        startMonitoring()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        // Load saved app limits from preferences
        loadAppLimits()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Usage Monitoring",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Monitors app usage and enforces limits"
            }
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun startMonitoring() {
        serviceScope.launch {
            while (isActive) {
                checkAppUsage()
                delay(1000) // Check every second
            }
        }
    }

    private fun checkAppUsage() {
        val currentTime = System.currentTimeMillis()
        val startTime = currentTime - (24 * 60 * 60 * 1000) // Last 24 hours

        val usageStats = usageStatsManager?.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            currentTime
        )

        usageStats?.forEach { stats ->
            val packageName = stats.packageName
            val timeInForeground = stats.totalTimeInForeground

            if (appLimits.containsKey(packageName) && 
                timeInForeground >= appLimits[packageName] ?: 0L) {
                showLimitReachedNotification(packageName)
                if (isStrictModeEnabled()) {
                    // In strict mode, force close the app
                    performGlobalAction(GLOBAL_ACTION_HOME)
                }
            }
        }
    }

    private fun showLimitReachedNotification(packageName: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val appName = try {
            packageManager.getApplicationLabel(
                packageManager.getApplicationInfo(packageName, 0)
            ).toString()
        } catch (e: Exception) {
            packageName
        }

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.app_limit_reached))
            .setContentText("You've reached your daily limit for $appName")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager?.notify(NOTIFICATION_ID, notification)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let {
            when (it.eventType) {
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                    val packageName = it.packageName?.toString()
                    if (packageName != null && appLimits.containsKey(packageName)) {
                        checkAppUsage()
                    }
                }
            }
        }
    }

    override fun onInterrupt() {
        // Service interrupted
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private fun loadAppLimits() {
        // Load app limits from SharedPreferences
        appLimits.clear()
        val limits = preferences.getStringSet("app_limits", emptySet()) ?: emptySet()
        limits.forEach { limitString ->
            val parts = limitString.split(":")
            if (parts.size == 2) {
                appLimits[parts[0]] = parts[1].toLongOrNull() ?: 0L
            }
        }
    }

    fun updateAppLimit(packageName: String, limitInMillis: Long) {
        appLimits[packageName] = limitInMillis
        saveAppLimits()
    }

    private fun saveAppLimits() {
        // Save app limits to SharedPreferences
        val limits = appLimits.map { "${it.key}:${it.value}" }.toSet()
        preferences.edit().putStringSet("app_limits", limits).apply()
    }

    private fun isStrictModeEnabled(): Boolean {
        return preferences.getBoolean("strict_mode", false)
    }

    companion object {
        fun getAppUsageStats(context: Context, startTime: Long, endTime: Long): List<UsageStats> {
            val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            return usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                startTime,
                endTime
            ).sortedByDescending { it.totalTimeInForeground }
        }

        fun getTotalScreenTime(context: Context): Long {
            val endTime = System.currentTimeMillis()
            val startTime = endTime - (24 * 60 * 60 * 1000) // Last 24 hours
            return getAppUsageStats(context, startTime, endTime)
                .sumOf { it.totalTimeInForeground }
        }
    }
}
