package com.darweshbhai.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val startTime: Long,
    val endTime: Long? = null,
    val duration: Long, // planned duration in milliseconds
    val actualDuration: Long? = null, // actual duration if completed
    val type: SessionType,
    val isCompleted: Boolean = false,
    val wasInterrupted: Boolean = false,
    val interruptionCount: Int = 0,
    val focusScore: Int? = null, // 0-100 score based on completion and interruptions
    val notes: String = "",
    val associatedTasks: List<String> = emptyList(), // List of task IDs worked on
    val createdAt: Long = System.currentTimeMillis()
)

enum class SessionType {
    POMODORO,
    SHORT_BREAK,
    LONG_BREAK;

    companion object {
        fun getDefaultDuration(type: SessionType): Long {
            return when (type) {
                POMODORO -> 25L * 60 * 1000 // 25 minutes
                SHORT_BREAK -> 5L * 60 * 1000 // 5 minutes
                LONG_BREAK -> 15L * 60 * 1000 // 15 minutes
            }
        }
    }
}
