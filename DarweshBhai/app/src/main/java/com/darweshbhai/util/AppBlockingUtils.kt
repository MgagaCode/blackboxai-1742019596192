package com.darweshbhai.util

import android.app.ActivityManager
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Process
import com.darweshbhai.data.entity.AppUsageEntity
import com.darweshbhai.data.entity.FocusSessionEntity
import java.util.concurrent.TimeUnit

object AppBlockingUtils {

    private const val EMERGENCY_OVERRIDE_DURATION = 5L // minutes
    private const val GRACE_PERIOD = 30L // seconds

    fun shouldBlockApp(
        app: AppUsageEntity,
        currentTime: Long = System.currentTimeMillis()
    ): BlockingReason? {
        return when {
            app.isBlocked -> BlockingReason.MANUALLY_BLOCKED
            app.isWhitelisted -> null
            app.dailyLimit > 0 && app.totalUsageToday >= app.dailyLimit -> BlockingReason.DAILY_LIMIT
            else -> null
        }
    }

    fun shouldBlockDuringFocusSession(
        app: AppUsageEntity,
        session: FocusSessionEntity?
    ): Boolean {
        if (session == null || !session.isCompleted) return false
        if (app.isWhitelisted) return false
        
        return when (app.category) {
            com.darweshbhai.data.entity.AppCategory.SOCIAL_MEDIA,
            com.darweshbhai.data.entity.AppCategory.ENTERTAINMENT,
            com.darweshbhai.data.entity.AppCategory.GAMING -> true
            else -> false
        }
    }

    fun getHomeIntent(): Intent {
        return Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    fun getLockScreenIntent(): Intent {
        return Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        }
    }

    fun isAppInForeground(context: Context, packageName: String): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val tasks = activityManager.getRunningTasks(1)
        return tasks.firstOrNull()?.topActivity?.packageName == packageName
    }

    fun canOverrideBlock(
        app: AppUsageEntity,
        overrideCount: Int,
        overrideLimit: Int,
        lastOverrideTime: Long
    ): Boolean {
        if (!app.isBlocked && app.dailyLimit <= 0) return true
        if (overrideCount >= overrideLimit) return false
        
        val timeSinceLastOverride = System.currentTimeMillis() - lastOverrideTime
        return timeSinceLastOverride >= TimeUnit.HOURS.toMillis(1)
    }

    fun getGracePeriodEnd(blockStartTime: Long): Long {
        return blockStartTime + TimeUnit.SECONDS.toMillis(GRACE_PERIOD)
    }

    fun getEmergencyOverrideEnd(overrideStartTime: Long): Long {
        return overrideStartTime + TimeUnit.MINUTES.toMillis(EMERGENCY_OVERRIDE_DURATION)
    }

    fun isSystemPackage(context: Context, packageName: String): Boolean {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(packageName, 0)
            (packageInfo.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun isLauncherPackage(context: Context, packageName: String): Boolean {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        val resolveInfo = context.packageManager.resolveActivity(intent, 0)
        return resolveInfo?.activityInfo?.packageName == packageName
    }

    fun getBlockedAppNotification(
        context: Context,
        app: AppUsageEntity,
        reason: BlockingReason
    ): BlockedAppNotification {
        val title = when (reason) {
            BlockingReason.MANUALLY_BLOCKED -> "App Blocked"
            BlockingReason.DAILY_LIMIT -> "Daily Limit Reached"
            BlockingReason.FOCUS_SESSION -> "Focus Session Active"
        }

        val message = when (reason) {
            BlockingReason.MANUALLY_BLOCKED ->
                "${app.appName} is currently blocked"
            BlockingReason.DAILY_LIMIT ->
                "You've reached your daily limit of ${DateTimeUtils.formatDuration(app.dailyLimit)} for ${app.appName}"
            BlockingReason.FOCUS_SESSION ->
                "Stay focused! ${app.appName} is blocked during focus sessions"
        }

        return BlockedAppNotification(title, message)
    }

    sealed class BlockingReason {
        object MANUALLY_BLOCKED : BlockingReason()
        object DAILY_LIMIT : BlockingReason()
        object FOCUS_SESSION : BlockingReason()
    }

    data class BlockedAppNotification(
        val title: String,
        val message: String
    )

    fun getBlockingStats(apps: List<AppUsageEntity>): BlockingStats {
        val blockedApps = apps.count { it.isBlocked }
        val appsWithLimits = apps.count { it.dailyLimit > 0 }
        val overLimitApps = apps.count { app ->
            app.dailyLimit > 0 && app.totalUsageToday >= app.dailyLimit
        }

        return BlockingStats(
            totalBlockedApps = blockedApps,
            appsWithLimits = appsWithLimits,
            appsOverLimit = overLimitApps
        )
    }

    data class BlockingStats(
        val totalBlockedApps: Int,
        val appsWithLimits: Int,
        val appsOverLimit: Int
    )

    fun calculateRecommendedLimit(
        averageUsage: Long,
        category: com.darweshbhai.data.entity.AppCategory
    ): Long {
        val reductionFactor = when (category) {
            com.darweshbhai.data.entity.AppCategory.SOCIAL_MEDIA -> 0.5
            com.darweshbhai.data.entity.AppCategory.ENTERTAINMENT -> 0.6
            com.darweshbhai.data.entity.AppCategory.GAMING -> 0.4
            com.darweshbhai.data.entity.AppCategory.PRODUCTIVITY -> 0.8
            else -> 0.7
        }

        return (averageUsage * reductionFactor).toLong()
    }

    fun suggestBlockingSchedule(
        usagePattern: UsageStatsUtils.DailyUsagePattern
    ): List<TimeRange> {
        val blockingPeriods = mutableListOf<TimeRange>()

        // Add late night hours
        blockingPeriods.add(TimeRange(23, 6)) // 11 PM to 6 AM

        // Add peak productivity hours based on quiet hours
        usagePattern.quietHours
            .filter { it in 9..17 } // Consider only working hours
            .forEach { hour ->
                blockingPeriods.add(TimeRange(hour, hour + 1))
            }

        return blockingPeriods.mergeContinuous()
    }

    data class TimeRange(
        val startHour: Int,
        val endHour: Int
    )

    private fun List<TimeRange>.mergeContinuous(): List<TimeRange> {
        if (isEmpty()) return emptyList()

        val sorted = sortedBy { it.startHour }
        val merged = mutableListOf<TimeRange>()
        var current = sorted.first()

        for (i in 1 until sorted.size) {
            val next = sorted[i]
            if (current.endHour == next.startHour) {
                current = TimeRange(current.startHour, next.endHour)
            } else {
                merged.add(current)
                current = next
            }
        }
        merged.add(current)

        return merged
    }
}
