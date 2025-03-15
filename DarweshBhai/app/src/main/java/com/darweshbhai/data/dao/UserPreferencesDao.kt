package com.darweshbhai.data.dao

import androidx.room.*
import com.darweshbhai.data.entity.UserPreferencesEntity
import com.darweshbhai.data.entity.TimeFormat
import com.darweshbhai.data.entity.DateFormat
import com.darweshbhai.data.entity.TaskSortOption
import com.darweshbhai.data.entity.BackupFrequency
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPreferencesDao {
    @Query("SELECT * FROM user_preferences LIMIT 1")
    fun getUserPreferences(): Flow<UserPreferencesEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserPreferences(preferences: UserPreferencesEntity)

    @Update
    suspend fun updateUserPreferences(preferences: UserPreferencesEntity)

    // Theme preferences
    @Query("UPDATE user_preferences SET isDarkMode = :isDarkMode")
    suspend fun setDarkMode(isDarkMode: Boolean)

    @Query("UPDATE user_preferences SET accentColor = :color")
    suspend fun setAccentColor(color: String)

    // Notification preferences
    @Query("UPDATE user_preferences SET isNotificationsEnabled = :enabled")
    suspend fun setNotificationsEnabled(enabled: Boolean)

    @Query("UPDATE user_preferences SET " +
           "quietHoursEnabled = :enabled, " +
           "quietHoursStart = :startHour, " +
           "quietHoursEnd = :endHour")
    suspend fun setQuietHours(enabled: Boolean, startHour: Int, endHour: Int)

    // Focus mode preferences
    @Query("UPDATE user_preferences SET " +
           "pomodoroLength = :pomodoroMinutes, " +
           "shortBreakLength = :shortBreakMinutes, " +
           "longBreakLength = :longBreakMinutes, " +
           "longBreakInterval = :interval")
    suspend fun updateFocusSettings(
        pomodoroMinutes: Int,
        shortBreakMinutes: Int,
        longBreakMinutes: Int,
        interval: Int
    )

    @Query("UPDATE user_preferences SET " +
           "autoStartBreaks = :autoStartBreaks, " +
           "autoStartPomodoros = :autoStartPomodoros")
    suspend fun setAutoStartPreferences(autoStartBreaks: Boolean, autoStartPomodoros: Boolean)

    // App blocking preferences
    @Query("UPDATE user_preferences SET isStrictModeEnabled = :enabled")
    suspend fun setStrictMode(enabled: Boolean)

    @Query("UPDATE user_preferences SET " +
           "blockNotifications = :blockNotifications, " +
           "allowEmergencyOverride = :allowOverride, " +
           "emergencyOverrideLimit = :overrideLimit")
    suspend fun updateBlockingPreferences(
        blockNotifications: Boolean,
        allowOverride: Boolean,
        overrideLimit: Int
    )

    // Language and localization
    @Query("UPDATE user_preferences SET language = :languageCode")
    suspend fun setLanguage(languageCode: String)

    @Query("UPDATE user_preferences SET timeFormat = :format")
    suspend fun setTimeFormat(format: TimeFormat)

    @Query("UPDATE user_preferences SET dateFormat = :format")
    suspend fun setDateFormat(format: DateFormat)

    // Dashboard preferences
    @Query("UPDATE user_preferences SET " +
           "showScreenTime = :showScreenTime, " +
           "showFocusScore = :showFocusScore, " +
           "showProductivityTips = :showTips, " +
           "showWeeklyReport = :showReport")
    suspend fun updateDashboardPreferences(
        showScreenTime: Boolean,
        showFocusScore: Boolean,
        showTips: Boolean,
        showReport: Boolean
    )

    // Task preferences
    @Query("UPDATE user_preferences SET " +
           "defaultTaskPriority = :priority, " +
           "showCompletedTasks = :showCompleted, " +
           "sortTasksBy = :sortOption")
    suspend fun updateTaskPreferences(
        priority: TaskPriority,
        showCompleted: Boolean,
        sortOption: TaskSortOption
    )

    // Backup preferences
    @Query("UPDATE user_preferences SET " +
           "autoBackupEnabled = :enabled, " +
           "backupFrequency = :frequency")
    suspend fun setBackupPreferences(enabled: Boolean, frequency: BackupFrequency)

    @Query("UPDATE user_preferences SET lastBackupTime = :timestamp")
    suspend fun updateLastBackupTime(timestamp: Long)

    // First launch and updates
    @Query("UPDATE user_preferences SET firstLaunch = 0")
    suspend fun markFirstLaunchComplete()

    @Query("UPDATE user_preferences SET lastUpdateVersion = :version")
    suspend fun updateLastVersion(version: String)

    // Reset preferences
    @Query("DELETE FROM user_preferences")
    suspend fun resetAllPreferences()

    @Transaction
    suspend fun resetToDefaults() {
        resetAllPreferences()
        insertUserPreferences(UserPreferencesEntity())
    }

    // Utility queries
    @Query("SELECT isDarkMode FROM user_preferences LIMIT 1")
    fun isDarkMode(): Flow<Boolean>

    @Query("SELECT isNotificationsEnabled FROM user_preferences LIMIT 1")
    fun areNotificationsEnabled(): Flow<Boolean>

    @Query("SELECT isStrictModeEnabled FROM user_preferences LIMIT 1")
    fun isStrictModeEnabled(): Flow<Boolean>

    @Query("SELECT language FROM user_preferences LIMIT 1")
    fun getCurrentLanguage(): Flow<String>

    @Query("SELECT firstLaunch FROM user_preferences LIMIT 1")
    suspend fun isFirstLaunch(): Boolean

    @Query("SELECT quietHoursEnabled FROM user_preferences LIMIT 1")
    fun areQuietHoursEnabled(): Flow<Boolean>

    @Query("SELECT * FROM user_preferences WHERE quietHoursEnabled = 1 LIMIT 1")
    fun getQuietHours(): Flow<UserPreferencesEntity?>
}
