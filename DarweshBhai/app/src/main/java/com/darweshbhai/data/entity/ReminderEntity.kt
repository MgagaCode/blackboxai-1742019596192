package com.darweshbhai.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val time: Long,
    val type: ReminderType = ReminderType.ONE_TIME,
    val repeatInterval: RepeatInterval? = null,
    val customRepeatInterval: Long? = null, // in milliseconds, for custom repeat intervals
    val isEnabled: Boolean = true,
    val lastTriggered: Long? = null,
    val nextTrigger: Long? = null,
    val associatedTaskId: String? = null, // Optional link to a task
    val priority: ReminderPriority = ReminderPriority.NORMAL,
    val tags: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class ReminderType {
    ONE_TIME,
    REPEATING,
    LOCATION_BASED
}

enum class RepeatInterval {
    DAILY,
    WEEKLY,
    MONTHLY,
    CUSTOM;

    fun toMillis(): Long {
        return when (this) {
            DAILY -> 24 * 60 * 60 * 1000L // 24 hours
            WEEKLY -> 7 * 24 * 60 * 60 * 1000L // 7 days
            MONTHLY -> 30 * 24 * 60 * 60 * 1000L // 30 days (approximate)
            CUSTOM -> 0L // Custom interval should be specified separately
        }
    }
}

enum class ReminderPriority {
    LOW,
    NORMAL,
    HIGH,
    URGENT;

    fun toNotificationPriority(): Int {
        return when (this) {
            LOW -> android.app.NotificationManager.IMPORTANCE_LOW
            NORMAL -> android.app.NotificationManager.IMPORTANCE_DEFAULT
            HIGH -> android.app.NotificationManager.IMPORTANCE_HIGH
            URGENT -> android.app.NotificationManager.IMPORTANCE_HIGH
        }
    }
}
