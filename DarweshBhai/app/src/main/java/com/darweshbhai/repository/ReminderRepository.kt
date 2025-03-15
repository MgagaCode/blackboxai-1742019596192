package com.darweshbhai.repository

import com.darweshbhai.data.entity.ReminderEntity
import com.darweshbhai.data.entity.ReminderType
import com.darweshbhai.data.entity.ReminderPriority
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    fun getAllReminders(): Flow<List<ReminderEntity>>
    suspend fun getReminderById(reminderId: String): ReminderEntity?
    fun getActiveReminders(): Flow<List<ReminderEntity>>
    fun getRemindersInTimeRange(startTime: Long, endTime: Long): Flow<List<ReminderEntity>>
    fun getRemindersByType(reminderType: ReminderType): Flow<List<ReminderEntity>>
    fun getRemindersByPriority(priority: ReminderPriority): Flow<List<ReminderEntity>>
    suspend fun insertReminder(reminder: ReminderEntity)
    suspend fun insertReminders(reminders: List<ReminderEntity>)
    suspend fun updateReminder(reminder: ReminderEntity)
    suspend fun deleteReminder(reminder: ReminderEntity)
    suspend fun deleteReminders(reminderIds: List<String>)
    suspend fun setReminderEnabled(reminderId: String, enabled: Boolean)
    fun getRemindersForTask(taskId: String): Flow<List<ReminderEntity>>
    fun getOverdueReminders(): Flow<List<ReminderEntity>>
    fun getUpcomingReminders(): Flow<List<ReminderEntity>>
    fun getActiveReminderCount(): Flow<Int>
    fun getActiveRepeatingReminders(): Flow<List<ReminderEntity>>
    suspend fun updateNextTrigger(reminderId: String, nextTriggerTime: Long)
    fun getActiveLocationReminders(): Flow<List<ReminderEntity>>
    fun getRemindersByTag(tag: String): Flow<List<ReminderEntity>>
    fun getTodayRemindersByPriority(
        dayStart: Long,
        dayEnd: Long,
        priority: ReminderPriority
    ): Flow<List<ReminderEntity>>
    fun getRecentlyTriggeredReminders(limit: Int): Flow<List<ReminderEntity>>
    suspend fun toggleReminderEnabled(reminderId: String)
    suspend fun updateReminderPriority(reminderId: String, priority: ReminderPriority)
    suspend fun updateReminderTags(reminderId: String, tags: List<String>)
    fun getAllTags(): Flow<List<String>>
}

class ReminderRepositoryImpl(
    private val reminderDao: com.darweshbhai.data.dao.ReminderDao
) : ReminderRepository {
    
    override fun getAllReminders(): Flow<List<ReminderEntity>> = reminderDao.getAllReminders()
    
    override suspend fun getReminderById(reminderId: String): ReminderEntity? =
        reminderDao.getReminderById(reminderId)
    
    override fun getActiveReminders(): Flow<List<ReminderEntity>> = reminderDao.getActiveReminders()
    
    override fun getRemindersInTimeRange(
        startTime: Long,
        endTime: Long
    ): Flow<List<ReminderEntity>> =
        reminderDao.getRemindersInTimeRange(startTime, endTime)
    
    override fun getRemindersByType(reminderType: ReminderType): Flow<List<ReminderEntity>> =
        reminderDao.getRemindersByType(reminderType)
    
    override fun getRemindersByPriority(priority: ReminderPriority): Flow<List<ReminderEntity>> =
        reminderDao.getRemindersByPriority(priority)
    
    override suspend fun insertReminder(reminder: ReminderEntity) =
        reminderDao.insertReminder(reminder)
    
    override suspend fun insertReminders(reminders: List<ReminderEntity>) =
        reminderDao.insertReminders(reminders)
    
    override suspend fun updateReminder(reminder: ReminderEntity) =
        reminderDao.updateReminder(reminder)
    
    override suspend fun deleteReminder(reminder: ReminderEntity) =
        reminderDao.deleteReminder(reminder)
    
    override suspend fun deleteReminders(reminderIds: List<String>) =
        reminderDao.deleteReminders(reminderIds)
    
    override suspend fun setReminderEnabled(reminderId: String, enabled: Boolean) =
        reminderDao.setReminderEnabled(reminderId, enabled)
    
    override fun getRemindersForTask(taskId: String): Flow<List<ReminderEntity>> =
        reminderDao.getRemindersForTask(taskId)
    
    override fun getOverdueReminders(): Flow<List<ReminderEntity>> =
        reminderDao.getOverdueReminders()
    
    override fun getUpcomingReminders(): Flow<List<ReminderEntity>> =
        reminderDao.getUpcomingReminders()
    
    override fun getActiveReminderCount(): Flow<Int> = reminderDao.getActiveReminderCount()
    
    override fun getActiveRepeatingReminders(): Flow<List<ReminderEntity>> =
        reminderDao.getActiveRepeatingReminders()
    
    override suspend fun updateNextTrigger(reminderId: String, nextTriggerTime: Long) =
        reminderDao.updateNextTrigger(reminderId, nextTriggerTime)
    
    override fun getActiveLocationReminders(): Flow<List<ReminderEntity>> =
        reminderDao.getActiveLocationReminders()
    
    override fun getRemindersByTag(tag: String): Flow<List<ReminderEntity>> =
        reminderDao.getRemindersByTag(tag)
    
    override fun getTodayRemindersByPriority(
        dayStart: Long,
        dayEnd: Long,
        priority: ReminderPriority
    ): Flow<List<ReminderEntity>> =
        reminderDao.getTodayRemindersByPriority(dayStart, dayEnd, priority)
    
    override fun getRecentlyTriggeredReminders(limit: Int): Flow<List<ReminderEntity>> =
        reminderDao.getRecentlyTriggeredReminders(limit)
    
    override suspend fun toggleReminderEnabled(reminderId: String) =
        reminderDao.toggleReminderEnabled(reminderId)
    
    override suspend fun updateReminderPriority(reminderId: String, priority: ReminderPriority) =
        reminderDao.updateReminderPriority(reminderId, priority)
    
    override suspend fun updateReminderTags(reminderId: String, tags: List<String>) =
        reminderDao.updateReminderTags(reminderId, tags)
    
    override fun getAllTags(): Flow<List<String>> = reminderDao.getAllTags()
}
