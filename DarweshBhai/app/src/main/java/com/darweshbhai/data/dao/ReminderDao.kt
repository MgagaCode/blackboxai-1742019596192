package com.darweshbhai.data.dao

import androidx.room.*
import com.darweshbhai.data.entity.ReminderEntity
import com.darweshbhai.data.entity.ReminderType
import com.darweshbhai.data.entity.ReminderPriority
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders ORDER BY time ASC")
    fun getAllReminders(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE id = :reminderId")
    suspend fun getReminderById(reminderId: String): ReminderEntity?

    @Query("SELECT * FROM reminders WHERE isEnabled = 1 ORDER BY time ASC")
    fun getActiveReminders(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders " +
           "WHERE time >= :startTime AND time <= :endTime " +
           "ORDER BY time ASC")
    fun getRemindersInTimeRange(startTime: Long, endTime: Long): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE type = :reminderType ORDER BY time ASC")
    fun getRemindersByType(reminderType: ReminderType): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE priority = :priority ORDER BY time ASC")
    fun getRemindersByPriority(priority: ReminderPriority): Flow<List<ReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminders(reminders: List<ReminderEntity>)

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    @Query("DELETE FROM reminders WHERE id IN (:reminderIds)")
    suspend fun deleteReminders(reminderIds: List<String>)

    @Query("UPDATE reminders SET isEnabled = :enabled WHERE id = :reminderId")
    suspend fun setReminderEnabled(reminderId: String, enabled: Boolean)

    @Query("SELECT * FROM reminders WHERE associatedTaskId = :taskId")
    fun getRemindersForTask(taskId: String): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE time < :now AND isEnabled = 1")
    fun getOverdueReminders(now: Long = System.currentTimeMillis()): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE nextTrigger IS NOT NULL AND nextTrigger > :now")
    fun getUpcomingReminders(now: Long = System.currentTimeMillis()): Flow<List<ReminderEntity>>

    @Query("SELECT COUNT(*) FROM reminders WHERE isEnabled = 1")
    fun getActiveReminderCount(): Flow<Int>

    // Repeating reminders
    @Query("SELECT * FROM reminders WHERE type = 'REPEATING' AND isEnabled = 1")
    fun getActiveRepeatingReminders(): Flow<List<ReminderEntity>>

    @Transaction
    suspend fun updateNextTrigger(reminderId: String, nextTriggerTime: Long) {
        val reminder = getReminderById(reminderId)
        reminder?.let {
            updateReminder(it.copy(
                lastTriggered = System.currentTimeMillis(),
                nextTrigger = nextTriggerTime,
                updatedAt = System.currentTimeMillis()
            ))
        }
    }

    // Location-based reminders
    @Query("SELECT * FROM reminders WHERE type = 'LOCATION_BASED' AND isEnabled = 1")
    fun getActiveLocationReminders(): Flow<List<ReminderEntity>>

    // Tag-based queries
    @Query("SELECT * FROM reminders WHERE :tag IN (tags)")
    fun getRemindersByTag(tag: String): Flow<List<ReminderEntity>>

    // Priority-based reminders for today
    @Query("SELECT * FROM reminders " +
           "WHERE isEnabled = 1 " +
           "AND time BETWEEN :dayStart AND :dayEnd " +
           "AND priority = :priority " +
           "ORDER BY time ASC")
    fun getTodayRemindersByPriority(
        dayStart: Long,
        dayEnd: Long,
        priority: ReminderPriority
    ): Flow<List<ReminderEntity>>

    // Recently triggered reminders
    @Query("SELECT * FROM reminders " +
           "WHERE lastTriggered IS NOT NULL " +
           "ORDER BY lastTriggered DESC LIMIT :limit")
    fun getRecentlyTriggeredReminders(limit: Int): Flow<List<ReminderEntity>>

    @Transaction
    suspend fun toggleReminderEnabled(reminderId: String) {
        val reminder = getReminderById(reminderId)
        reminder?.let {
            updateReminder(it.copy(
                isEnabled = !it.isEnabled,
                updatedAt = System.currentTimeMillis()
            ))
        }
    }

    @Query("UPDATE reminders SET priority = :priority WHERE id = :reminderId")
    suspend fun updateReminderPriority(reminderId: String, priority: ReminderPriority)

    @Query("UPDATE reminders SET tags = :tags WHERE id = :reminderId")
    suspend fun updateReminderTags(reminderId: String, tags: List<String>)

    @Query("SELECT DISTINCT tags FROM reminders WHERE tags IS NOT NULL")
    fun getAllTags(): Flow<List<String>>
}
