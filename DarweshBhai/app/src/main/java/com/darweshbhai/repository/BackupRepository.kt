package com.darweshbhai.repository

import android.util.Log
import com.darweshbhai.data.AppDatabase
import com.darweshbhai.data.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

interface BackupRepository {
    suspend fun createBackup(file: File): Result<Unit>
    suspend fun restoreBackup(file: File): Result<Unit>
    suspend fun getLastBackupTime(): Long?
    suspend fun shouldPerformBackup(): Boolean
    suspend fun deleteOldBackups(maxBackups: Int = 5)
    suspend fun getBackupFiles(): List<File>
    suspend fun validateBackup(file: File): Boolean
}

class BackupRepositoryImpl(
    private val database: AppDatabase,
    private val userPreferencesRepository: UserPreferencesRepository
) : BackupRepository {

    companion object {
        private const val TAG = "BackupRepository"
        private const val BACKUP_VERSION = 1
        private const val BACKUP_DIR = "backups"
    }

    override suspend fun createBackup(file: File): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val backup = JSONObject().apply {
                put("version", BACKUP_VERSION)
                put("timestamp", System.currentTimeMillis())
                put("deviceId", getDeviceId())
                
                // Tasks
                put("tasks", JSONObject().apply {
                    put("data", database.taskDao().getAllTasks().first().map { it.toJson() })
                })

                // App Usage
                put("app_usage", JSONObject().apply {
                    put("data", database.appUsageDao().getAllAppUsage().first().map { it.toJson() })
                })

                // Focus Sessions
                put("focus_sessions", JSONObject().apply {
                    put("data", database.focusSessionDao().getAllSessions().first().map { it.toJson() })
                })

                // Reminders
                put("reminders", JSONObject().apply {
                    put("data", database.reminderDao().getAllReminders().first().map { it.toJson() })
                })

                // User Preferences
                put("preferences", database.userPreferencesDao().getUserPreferences().first()?.toJson() 
                    ?: JSONObject())
            }

            // Ensure backup directory exists
            file.parentFile?.mkdirs()
            
            // Write backup file
            file.writeText(backup.toString(2))
            
            // Update last backup time
            userPreferencesRepository.updateLastBackupTime(System.currentTimeMillis())
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating backup", e)
            Result.failure(e)
        }
    }

    override suspend fun restoreBackup(file: File): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (!validateBackup(file)) {
                return@withContext Result.failure(IllegalArgumentException("Invalid backup file"))
            }

            val backup = JSONObject(file.readText())

            // Begin transaction
            database.runInTransaction {
                // Clear existing data
                database.clearAllTables()

                // Restore all entities
                restoreTasks(backup.getJSONObject("tasks"))
                restoreAppUsage(backup.getJSONObject("app_usage"))
                restoreFocusSessions(backup.getJSONObject("focus_sessions"))
                restoreReminders(backup.getJSONObject("reminders"))
                restorePreferences(backup.getJSONObject("preferences"))
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error restoring backup", e)
            Result.failure(e)
        }
    }

    override suspend fun getLastBackupTime(): Long? {
        return userPreferencesRepository.getUserPreferences().first()?.lastBackupTime
    }

    override suspend fun shouldPerformBackup(): Boolean {
        val preferences = userPreferencesRepository.getUserPreferences().first() ?: return false
        if (!preferences.autoBackupEnabled) return false
        
        val lastBackup = preferences.lastBackupTime ?: return true
        val frequency = preferences.backupFrequency.toMillis()
        return System.currentTimeMillis() - lastBackup >= frequency
    }

    override suspend fun deleteOldBackups(maxBackups: Int) {
        getBackupFiles()
            .sortedByDescending { it.lastModified() }
            .drop(maxBackups)
            .forEach { it.delete() }
    }

    override suspend fun getBackupFiles(): List<File> {
        val backupDir = getBackupDirectory()
        return backupDir.listFiles { file -> 
            file.isFile && file.name.endsWith(".json") 
        }?.toList() ?: emptyList()
    }

    override suspend fun validateBackup(file: File): Boolean {
        return try {
            val backup = JSONObject(file.readText())
            val version = backup.getInt("version")
            version <= BACKUP_VERSION &&
                backup.has("tasks") &&
                backup.has("app_usage") &&
                backup.has("focus_sessions") &&
                backup.has("reminders") &&
                backup.has("preferences")
        } catch (e: Exception) {
            false
        }
    }

    private fun getBackupDirectory(): File {
        return File(database.openHelper.writableDatabase.path)
            .parentFile
            ?.resolve(BACKUP_DIR)
            ?.apply { mkdirs() }
            ?: throw IllegalStateException("Could not create backup directory")
    }

    private fun getDeviceId(): String {
        return UUID.randomUUID().toString()
    }

    private suspend fun restoreTasks(tasksObj: JSONObject) {
        tasksObj.getJSONArray("data").let { array ->
            val tasks = (0 until array.length()).map { i ->
                TaskEntity.fromJson(array.getJSONObject(i))
            }
            database.taskDao().insertTasks(tasks)
        }
    }

    private suspend fun restoreAppUsage(usageObj: JSONObject) {
        usageObj.getJSONArray("data").let { array ->
            val usage = (0 until array.length()).map { i ->
                AppUsageEntity.fromJson(array.getJSONObject(i))
            }
            database.appUsageDao().insertAppUsages(usage)
        }
    }

    private suspend fun restoreFocusSessions(sessionsObj: JSONObject) {
        sessionsObj.getJSONArray("data").let { array ->
            val sessions = (0 until array.length()).map { i ->
                FocusSessionEntity.fromJson(array.getJSONObject(i))
            }
            sessions.forEach { database.focusSessionDao().insertSession(it) }
        }
    }

    private suspend fun restoreReminders(remindersObj: JSONObject) {
        remindersObj.getJSONArray("data").let { array ->
            val reminders = (0 until array.length()).map { i ->
                ReminderEntity.fromJson(array.getJSONObject(i))
            }
            database.reminderDao().insertReminders(reminders)
        }
    }

    private suspend fun restorePreferences(preferencesObj: JSONObject) {
        if (preferencesObj.length() > 0) {
            val preferences = UserPreferencesEntity.fromJson(preferencesObj)
            database.userPreferencesDao().insertUserPreferences(preferences)
        }
    }
}
