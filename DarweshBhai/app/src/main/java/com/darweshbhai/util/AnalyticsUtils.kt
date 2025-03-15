package com.darweshbhai.util

import android.app.usage.UsageStats
import com.darweshbhai.data.entity.AppCategory
import com.darweshbhai.data.entity.FocusSessionEntity
import com.darweshbhai.data.entity.TaskEntity
import kotlin.math.roundToInt

object AnalyticsUtils {
    
    // Focus Score Calculation
    fun calculateFocusScore(session: FocusSessionEntity): Int {
        if (!session.isCompleted) return 0

        val baseScore = when {
            session.wasInterrupted -> 70
            else -> 100
        }

        val interruptionPenalty = session.interruptionCount * 5
        val durationRatio = (session.actualDuration ?: 0).toFloat() / session.duration

        return ((baseScore - interruptionPenalty) * durationRatio).roundToInt().coerceIn(0, 100)
    }

    // Productivity Score Calculation
    fun calculateProductivityScore(
        focusTime: Long,
        completedTasks: Int,
        screenTime: Long,
        distractingAppsTime: Long
    ): Int {
        val focusScore = (focusTime.toFloat() / (8 * 60 * 60 * 1000)) * 40 // 40% weight
        val taskScore = (completedTasks.toFloat() / 10) * 30 // 30% weight
        val screenTimeScore = calculateScreenTimeScore(screenTime, distractingAppsTime) * 30 // 30% weight

        return (focusScore + taskScore + screenTimeScore).roundToInt().coerceIn(0, 100)
    }

    private fun calculateScreenTimeScore(totalTime: Long, distractingTime: Long): Float {
        val productiveTime = totalTime - distractingTime
        return (productiveTime.toFloat() / totalTime).coerceIn(0f, 1f)
    }

    // App Usage Analysis
    fun analyzeAppUsage(usageStats: List<UsageStats>): AppUsageMetrics {
        val totalTime = usageStats.sumOf { it.totalTimeInForeground }
        val sortedByUsage = usageStats.sortedByDescending { it.totalTimeInForeground }
        val mostUsedApps = sortedByUsage.take(5)
        
        return AppUsageMetrics(
            totalScreenTime = totalTime,
            mostUsedApps = mostUsedApps.map { 
                AppUsageInfo(it.packageName, it.totalTimeInForeground) 
            }
        )
    }

    // Task Analysis
    fun analyzeTaskCompletion(tasks: List<TaskEntity>): TaskMetrics {
        val completed = tasks.count { it.isCompleted }
        val total = tasks.size
        val completionRate = if (total > 0) {
            (completed.toFloat() / total) * 100
        } else {
            0f
        }

        val priorityDistribution = tasks.groupBy { it.priority }
            .mapValues { it.value.size }

        return TaskMetrics(
            completionRate = completionRate,
            priorityDistribution = priorityDistribution
        )
    }

    // Focus Session Analysis
    fun analyzeFocusSessions(sessions: List<FocusSessionEntity>): FocusMetrics {
        val completed = sessions.count { it.isCompleted }
        val totalDuration = sessions.sumOf { it.actualDuration ?: 0L }
        val averageScore = sessions
            .filter { it.isCompleted }
            .map { calculateFocusScore(it) }
            .average()

        return FocusMetrics(
            completedSessions = completed,
            totalFocusTime = totalDuration,
            averageFocusScore = averageScore
        )
    }

    // Streak Calculation
    fun calculateStreak(sessions: List<FocusSessionEntity>): Int {
        if (sessions.isEmpty()) return 0

        val today = DateTimeUtils.getStartOfDay()
        var currentStreak = 0
        var currentDay = today

        while (true) {
            val sessionsForDay = sessions.filter { session ->
                val sessionDay = DateTimeUtils.getStartOfDay(session.startTime)
                sessionDay == currentDay
            }

            if (sessionsForDay.isEmpty() || !hasProductiveDay(sessionsForDay)) {
                break
            }

            currentStreak++
            currentDay -= 24 * 60 * 60 * 1000 // Subtract one day
        }

        return currentStreak
    }

    private fun hasProductiveDay(sessions: List<FocusSessionEntity>): Boolean {
        val totalFocusTime = sessions.sumOf { it.actualDuration ?: 0L }
        return totalFocusTime >= 25 * 60 * 1000 // At least one pomodoro session
    }

    // Category Analysis
    fun analyzeAppCategories(
        usageStats: Map<String, Long>,
        categoryMap: Map<String, AppCategory>
    ): Map<AppCategory, Long> {
        return usageStats.entries.groupBy { 
            categoryMap[it.key] ?: AppCategory.OTHER 
        }.mapValues { entry ->
            entry.value.sumOf { it.value }
        }
    }

    // Data Classes for Analytics Results
    data class AppUsageMetrics(
        val totalScreenTime: Long,
        val mostUsedApps: List<AppUsageInfo>
    )

    data class AppUsageInfo(
        val packageName: String,
        val usageTime: Long
    )

    data class TaskMetrics(
        val completionRate: Float,
        val priorityDistribution: Map<TaskPriority, Int>
    )

    data class FocusMetrics(
        val completedSessions: Int,
        val totalFocusTime: Long,
        val averageFocusScore: Double
    )

    // Utility Functions
    fun formatMetric(value: Float): String {
        return "%.1f".format(value)
    }

    fun formatPercentage(value: Float): String {
        return "${formatMetric(value)}%"
    }

    fun formatDuration(millis: Long): String {
        return DateTimeUtils.formatDurationLong(millis)
    }
}
