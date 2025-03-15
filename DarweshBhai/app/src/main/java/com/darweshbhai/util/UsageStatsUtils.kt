package com.darweshbhai.util

import android.app.usage.UsageEvents
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import com.darweshbhai.data.entity.AppCategory
import com.darweshbhai.data.entity.AppUsageEntity
import java.util.concurrent.TimeUnit

object UsageStatsUtils {

    private val SOCIAL_MEDIA_PACKAGES = setOf(
        "com.facebook.katana",
        "com.instagram.android",
        "com.twitter.android",
        "com.snapchat.android",
        "com.whatsapp",
        "com.linkedin.android",
        "com.pinterest"
    )

    private val PRODUCTIVITY_PACKAGES = setOf(
        "com.google.android.apps.docs",
        "com.microsoft.office.word",
        "com.microsoft.office.excel",
        "com.microsoft.office.powerpoint",
        "com.evernote",
        "com.todoist",
        "com.notion"
    )

    private val ENTERTAINMENT_PACKAGES = setOf(
        "com.netflix.mediaclient",
        "com.spotify.music",
        "com.google.android.youtube",
        "com.amazon.avod.thirdpartyclient",
        "com.disney.disneyplus"
    )

    private val GAMING_PACKAGES = setOf(
        "com.supercell.clashofclans",
        "com.king.candycrushsaga",
        "com.mojang.minecraftpe",
        "com.pubg.mobile",
        "com.activision.callofduty.shooter"
    )

    fun getAppUsageStats(
        context: Context,
        startTime: Long,
        endTime: Long
    ): List<AppUsageEntity> {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val packageManager = context.packageManager

        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )

        return stats.mapNotNull { stat ->
            try {
                val appInfo = packageManager.getApplicationInfo(stat.packageName, 0)
                val appName = packageManager.getApplicationLabel(appInfo).toString()

                AppUsageEntity(
                    packageName = stat.packageName,
                    appName = appName,
                    totalUsageToday = stat.totalTimeInForeground,
                    lastUsageTime = stat.lastTimeUsed,
                    category = determineAppCategory(stat.packageName)
                )
            } catch (e: PackageManager.NameNotFoundException) {
                null
            }
        }.filter {
            it.totalUsageToday > 0
        }
    }

    fun getDetailedUsageStats(
        context: Context,
        startTime: Long,
        endTime: Long
    ): Map<String, AppUsageDetails> {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val events = usageStatsManager.queryEvents(startTime, endTime)
        val usageEvent = UsageEvents.Event()
        val appUsageMap = mutableMapOf<String, AppUsageDetails>()

        var lastEventTime = startTime
        var currentForegroundApp: String? = null

        while (events.hasNextEvent()) {
            events.getNextEvent(usageEvent)

            when (usageEvent.eventType) {
                UsageEvents.Event.MOVE_TO_FOREGROUND -> {
                    // Calculate time for previous app
                    currentForegroundApp?.let { pkg ->
                        val details = appUsageMap.getOrPut(pkg) { AppUsageDetails() }
                        details.totalTimeInForeground += usageEvent.timeStamp - lastEventTime
                        details.lastTimeUsed = usageEvent.timeStamp
                    }
                    currentForegroundApp = usageEvent.packageName
                    lastEventTime = usageEvent.timeStamp

                    // Increment launch count
                    val details = appUsageMap.getOrPut(usageEvent.packageName) { AppUsageDetails() }
                    details.launchCount++
                }

                UsageEvents.Event.MOVE_TO_BACKGROUND -> {
                    if (currentForegroundApp == usageEvent.packageName) {
                        val details = appUsageMap.getOrPut(usageEvent.packageName) { AppUsageDetails() }
                        details.totalTimeInForeground += usageEvent.timeStamp - lastEventTime
                        details.lastTimeUsed = usageEvent.timeStamp
                        currentForegroundApp = null
                    }
                }
            }
        }

        // Handle case where app is still in foreground
        currentForegroundApp?.let { pkg ->
            val details = appUsageMap.getOrPut(pkg) { AppUsageDetails() }
            details.totalTimeInForeground += endTime - lastEventTime
            details.lastTimeUsed = endTime
        }

        return appUsageMap
    }

    fun determineAppCategory(packageName: String): AppCategory {
        return when {
            SOCIAL_MEDIA_PACKAGES.contains(packageName) -> AppCategory.SOCIAL_MEDIA
            PRODUCTIVITY_PACKAGES.contains(packageName) -> AppCategory.PRODUCTIVITY
            ENTERTAINMENT_PACKAGES.contains(packageName) -> AppCategory.ENTERTAINMENT
            GAMING_PACKAGES.contains(packageName) -> AppCategory.GAMING
            else -> AppCategory.OTHER
        }
    }

    fun isSystemApp(context: Context, packageName: String): Boolean {
        return try {
            val appInfo = context.packageManager.getApplicationInfo(packageName, 0)
            (appInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun getAppName(context: Context, packageName: String): String {
        return try {
            val appInfo = context.packageManager.getApplicationInfo(packageName, 0)
            context.packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            packageName
        }
    }

    fun calculateDailyAverageUsage(stats: List<UsageStats>): Long {
        if (stats.isEmpty()) return 0

        val totalUsage = stats.sumOf { it.totalTimeInForeground }
        val days = TimeUnit.MILLISECONDS.toDays(
            stats.maxOf { it.lastTimeUsed } - stats.minOf { it.firstTimeStamp }
        ).coerceAtLeast(1)

        return totalUsage / days
    }

    data class AppUsageDetails(
        var totalTimeInForeground: Long = 0L,
        var lastTimeUsed: Long = 0L,
        var launchCount: Int = 0
    )

    data class DailyUsagePattern(
        val hourlyUsage: Map<Int, Long>, // Hour (0-23) to usage time in ms
        val peakUsageHour: Int,
        val quietHours: List<Int>
    )

    fun analyzeDailyPattern(
        usageStats: Map<String, AppUsageDetails>,
        startTime: Long,
        endTime: Long
    ): DailyUsagePattern {
        val hourlyUsage = mutableMapOf<Int, Long>()
        
        // Initialize hours
        for (hour in 0..23) {
            hourlyUsage[hour] = 0L
        }

        // Aggregate usage by hour
        usageStats.values.forEach { details ->
            val hour = DateTimeUtils.getHourOfDay(details.lastTimeUsed)
            hourlyUsage[hour] = (hourlyUsage[hour] ?: 0L) + details.totalTimeInForeground
        }

        val peakHour = hourlyUsage.maxByOrNull { it.value }?.key ?: 0
        val quietHours = hourlyUsage.filter { it.value < 10 * 60 * 1000 } // Less than 10 minutes
            .map { it.key }
            .sorted()

        return DailyUsagePattern(
            hourlyUsage = hourlyUsage,
            peakUsageHour = peakHour,
            quietHours = quietHours
        )
    }
}
