package com.darweshbhai.repository

import com.darweshbhai.data.entity.*
import kotlinx.coroutines.flow.Flow
import android.content.SharedPreferences

interface UserPreferencesRepository {
    fun getUserPreferences(): Flow<UserPreferencesEntity?>
    suspend fun insertUserPreferences(preferences: UserPreferencesEntity)
    suspend fun updateUserPreferences(preferences: UserPreferencesEntity)
    suspend fun setDarkMode(isDarkMode: Boolean)
    suspend fun setAccentColor(color: String)
    suspend fun setNotificationsEnabled(enabled: Boolean)
    suspend fun setQuietHours(enabled: Boolean, startHour: Int, endHour: Int)
    suspend fun updateFocusSettings(
        pomodoroMinutes: Int,
        shortBreakMinutes: Int,
        longBreakMinutes: Int,
        interval: Int
    )
    suspend fun setAutoStartPreferences(autoStartBreaks: Boolean, autoStartPomodoros: Boolean)
    suspend fun setStrictMode(enabled: Boolean)
    suspend fun updateBlockingPreferences(
        blockNotifications: Boolean,
        allowOverride: Boolean,
        overrideLimit: Int
    )
    suspend fun setLanguage(languageCode: String)
    suspend fun setTimeFormat(format: TimeFormat)
    suspend fun setDateFormat(format: DateFormat)
    suspend fun updateDashboardPreferences(
        showScreenTime: Boolean,
        showFocusScore: Boolean,
        showTips: Boolean,
        showReport: Boolean
    )
    suspend fun updateTaskPreferences(
        priority: TaskPriority,
        showCompleted: Boolean,
        sortOption: TaskSortOption
    )
    suspend fun setBackupPreferences(enabled: Boolean, frequency: BackupFrequency)
    suspend fun updateLastBackupTime(timestamp: Long)
    suspend fun markFirstLaunchComplete()
    suspend fun updateLastVersion(version: String)
    suspend fun resetAllPreferences()
    suspend fun resetToDefaults()
    fun isDarkMode(): Flow<Boolean>
    fun areNotificationsEnabled(): Flow<Boolean>
    fun isStrictModeEnabled(): Flow<Boolean>
    fun getCurrentLanguage(): Flow<String>
    suspend fun isFirstLaunch(): Boolean
    fun areQuietHoursEnabled(): Flow<Boolean>
    fun getQuietHours(): Flow<UserPreferencesEntity?>
}

class UserPreferencesRepositoryImpl(
    private val userPreferencesDao: com.darweshbhai.data.dao.UserPreferencesDao,
    private val sharedPreferences: SharedPreferences
) : UserPreferencesRepository {
    
    override fun getUserPreferences(): Flow<UserPreferencesEntity?> =
        userPreferencesDao.getUserPreferences()
    
    override suspend fun insertUserPreferences(preferences: UserPreferencesEntity) =
        userPreferencesDao.insertUserPreferences(preferences)
    
    override suspend fun updateUserPreferences(preferences: UserPreferencesEntity) =
        userPreferencesDao.updateUserPreferences(preferences)
    
    override suspend fun setDarkMode(isDarkMode: Boolean) =
        userPreferencesDao.setDarkMode(isDarkMode)
    
    override suspend fun setAccentColor(color: String) =
        userPreferencesDao.setAccentColor(color)
    
    override suspend fun setNotificationsEnabled(enabled: Boolean) =
        userPreferencesDao.setNotificationsEnabled(enabled)
    
    override suspend fun setQuietHours(enabled: Boolean, startHour: Int, endHour: Int) =
        userPreferencesDao.setQuietHours(enabled, startHour, endHour)
    
    override suspend fun updateFocusSettings(
        pomodoroMinutes: Int,
        shortBreakMinutes: Int,
        longBreakMinutes: Int,
        interval: Int
    ) = userPreferencesDao.updateFocusSettings(
        pomodoroMinutes,
        shortBreakMinutes,
        longBreakMinutes,
        interval
    )
    
    override suspend fun setAutoStartPreferences(
        autoStartBreaks: Boolean,
        autoStartPomodoros: Boolean
    ) = userPreferencesDao.setAutoStartPreferences(autoStartBreaks, autoStartPomodoros)
    
    override suspend fun setStrictMode(enabled: Boolean) =
        userPreferencesDao.setStrictMode(enabled)
    
    override suspend fun updateBlockingPreferences(
        blockNotifications: Boolean,
        allowOverride: Boolean,
        overrideLimit: Int
    ) = userPreferencesDao.updateBlockingPreferences(
        blockNotifications,
        allowOverride,
        overrideLimit
    )
    
    override suspend fun setLanguage(languageCode: String) =
        userPreferencesDao.setLanguage(languageCode)
    
    override suspend fun setTimeFormat(format: TimeFormat) =
        userPreferencesDao.setTimeFormat(format)
    
    override suspend fun setDateFormat(format: DateFormat) =
        userPreferencesDao.setDateFormat(format)
    
    override suspend fun updateDashboardPreferences(
        showScreenTime: Boolean,
        showFocusScore: Boolean,
        showTips: Boolean,
        showReport: Boolean
    ) = userPreferencesDao.updateDashboardPreferences(
        showScreenTime,
        showFocusScore,
        showTips,
        showReport
    )
    
    override suspend fun updateTaskPreferences(
        priority: TaskPriority,
        showCompleted: Boolean,
        sortOption: TaskSortOption
    ) = userPreferencesDao.updateTaskPreferences(priority, showCompleted, sortOption)
    
    override suspend fun setBackupPreferences(enabled: Boolean, frequency: BackupFrequency) =
        userPreferencesDao.setBackupPreferences(enabled, frequency)
    
    override suspend fun updateLastBackupTime(timestamp: Long) =
        userPreferencesDao.updateLastBackupTime(timestamp)
    
    override suspend fun markFirstLaunchComplete() =
        userPreferencesDao.markFirstLaunchComplete()
    
    override suspend fun updateLastVersion(version: String) =
        userPreferencesDao.updateLastVersion(version)
    
    override suspend fun resetAllPreferences() =
        userPreferencesDao.resetAllPreferences()
    
    override suspend fun resetToDefaults() =
        userPreferencesDao.resetToDefaults()
    
    override fun isDarkMode(): Flow<Boolean> =
        userPreferencesDao.isDarkMode()
    
    override fun areNotificationsEnabled(): Flow<Boolean> =
        userPreferencesDao.areNotificationsEnabled()
    
    override fun isStrictModeEnabled(): Flow<Boolean> =
        userPreferencesDao.isStrictModeEnabled()
    
    override fun getCurrentLanguage(): Flow<String> =
        userPreferencesDao.getCurrentLanguage()
    
    override suspend fun isFirstLaunch(): Boolean =
        userPreferencesDao.isFirstLaunch()
    
    override fun areQuietHoursEnabled(): Flow<Boolean> =
        userPreferencesDao.areQuietHoursEnabled()
    
    override fun getQuietHours(): Flow<UserPreferencesEntity?> =
        userPreferencesDao.getQuietHours()
}
