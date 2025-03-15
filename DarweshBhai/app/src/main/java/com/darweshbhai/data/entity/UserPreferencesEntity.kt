package com.darweshbhai.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserPreferencesEntity(
    @PrimaryKey
    val id: Int = 1, // Single row for user preferences
    
    // Theme preferences
    val isDarkMode: Boolean = false,
    val accentColor: String = "#2196F3", // Default blue
    
    // Notification preferences
    val isNotificationsEnabled: Boolean = true,
    val notificationSound: Boolean = true,
    val notificationVibration: Boolean = true,
    val quietHoursEnabled: Boolean = false,
    val quietHoursStart: Int = 22, // 24-hour format
    val quietHoursEnd: Int = 7, // 24-hour format
    
    // Focus mode preferences
    val pomodoroLength: Int = 25, // in minutes
    val shortBreakLength: Int = 5, // in minutes
    val longBreakLength: Int = 15, // in minutes
    val longBreakInterval: Int = 4, // number of pomodoros before long break
    val autoStartBreaks: Boolean = false,
    val autoStartPomodoros: Boolean = false,
    
    // App blocking preferences
    val isStrictModeEnabled: Boolean = false,
    val blockNotifications: Boolean = false,
    val allowEmergencyOverride: Boolean = true,
    val emergencyOverrideLimit: Int = 3, // times per day
    
    // Language and localization
    val language: String = "en",
    val timeFormat: TimeFormat = TimeFormat.HOURS_24,
    val dateFormat: DateFormat = DateFormat.ISO,
    
    // Dashboard preferences
    val showScreenTime: Boolean = true,
    val showFocusScore: Boolean = true,
    val showProductivityTips: Boolean = true,
    val showWeeklyReport: Boolean = true,
    
    // Task preferences
    val defaultTaskPriority: TaskPriority = TaskPriority.MEDIUM,
    val showCompletedTasks: Boolean = true,
    val sortTasksBy: TaskSortOption = TaskSortOption.DUE_DATE,
    
    // Backup and sync
    val autoBackupEnabled: Boolean = true,
    val backupFrequency: BackupFrequency = BackupFrequency.WEEKLY,
    val lastBackupTime: Long? = null,
    
    // System
    val firstLaunch: Boolean = true,
    val lastUpdateVersion: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)

enum class TimeFormat {
    HOURS_12,
    HOURS_24
}

enum class DateFormat {
    ISO, // YYYY-MM-DD
    US,  // MM/DD/YYYY
    EU   // DD/MM/YYYY
}

enum class TaskSortOption {
    CREATION_DATE,
    DUE_DATE,
    PRIORITY,
    ALPHABETICAL
}

enum class BackupFrequency {
    DAILY,
    WEEKLY,
    MONTHLY,
    NEVER;

    fun toMillis(): Long {
        return when (this) {
            DAILY -> 24 * 60 * 60 * 1000L
            WEEKLY -> 7 * 24 * 60 * 60 * 1000L
            MONTHLY -> 30 * 24 * 60 * 60 * 1000L
            NEVER -> Long.MAX_VALUE
        }
    }
}
