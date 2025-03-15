package com.darweshbhai.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_usage")
data class AppUsageEntity(
    @PrimaryKey
    val packageName: String,
    val appName: String,
    val dailyLimit: Long = 0, // in milliseconds
    val isBlocked: Boolean = false,
    val lastUsageTime: Long = 0,
    val totalUsageToday: Long = 0,
    val updatedAt: Long = System.currentTimeMillis(),
    val isWhitelisted: Boolean = false,
    val category: AppCategory = AppCategory.OTHER
)

enum class AppCategory {
    SOCIAL_MEDIA,
    PRODUCTIVITY,
    ENTERTAINMENT,
    GAMING,
    COMMUNICATION,
    EDUCATION,
    OTHER
}
