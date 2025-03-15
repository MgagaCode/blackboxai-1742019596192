package com.darweshbhai.data.dao

import androidx.room.*
import com.darweshbhai.data.entity.AppUsageEntity
import com.darweshbhai.data.entity.AppCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface AppUsageDao {
    @Query("SELECT * FROM app_usage ORDER BY totalUsageToday DESC")
    fun getAllAppUsage(): Flow<List<AppUsageEntity>>

    @Query("SELECT * FROM app_usage WHERE packageName = :packageName")
    suspend fun getAppUsage(packageName: String): AppUsageEntity?

    @Query("SELECT * FROM app_usage WHERE isBlocked = 1")
    fun getBlockedApps(): Flow<List<AppUsageEntity>>

    @Query("SELECT * FROM app_usage WHERE isWhitelisted = 1")
    fun getWhitelistedApps(): Flow<List<AppUsageEntity>>

    @Query("SELECT * FROM app_usage WHERE category = :category")
    fun getAppsByCategory(category: AppCategory): Flow<List<AppUsageEntity>>

    @Query("SELECT * FROM app_usage WHERE dailyLimit > 0")
    fun getAppsWithLimits(): Flow<List<AppUsageEntity>>

    @Query("SELECT SUM(totalUsageToday) FROM app_usage")
    fun getTotalScreenTime(): Flow<Long>

    @Query("SELECT * FROM app_usage WHERE totalUsageToday > dailyLimit AND dailyLimit > 0")
    fun getOverLimitApps(): Flow<List<AppUsageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppUsage(appUsage: AppUsageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppUsages(appUsages: List<AppUsageEntity>)

    @Update
    suspend fun updateAppUsage(appUsage: AppUsageEntity)

    @Delete
    suspend fun deleteAppUsage(appUsage: AppUsageEntity)

    @Query("UPDATE app_usage SET isBlocked = :blocked WHERE packageName = :packageName")
    suspend fun setAppBlocked(packageName: String, blocked: Boolean)

    @Query("UPDATE app_usage SET isWhitelisted = :whitelisted WHERE packageName = :packageName")
    suspend fun setAppWhitelisted(packageName: String, whitelisted: Boolean)

    @Query("UPDATE app_usage SET dailyLimit = :limitInMillis WHERE packageName = :packageName")
    suspend fun setAppDailyLimit(packageName: String, limitInMillis: Long)

    @Query("UPDATE app_usage SET category = :category WHERE packageName = :packageName")
    suspend fun setAppCategory(packageName: String, category: AppCategory)

    @Query("UPDATE app_usage SET totalUsageToday = :usage WHERE packageName = :packageName")
    suspend fun updateAppUsageTime(packageName: String, usage: Long)

    @Query("UPDATE app_usage SET lastUsageTime = :timestamp WHERE packageName = :packageName")
    suspend fun updateLastUsageTime(packageName: String, timestamp: Long)

    @Query("SELECT * FROM app_usage " +
           "WHERE totalUsageToday > 0 " +
           "ORDER BY totalUsageToday DESC " +
           "LIMIT :limit")
    fun getMostUsedApps(limit: Int): Flow<List<AppUsageEntity>>

    @Query("SELECT * FROM app_usage " +
           "WHERE lastUsageTime > :since " +
           "ORDER BY lastUsageTime DESC")
    fun getRecentlyUsedApps(since: Long): Flow<List<AppUsageEntity>>

    @Query("UPDATE app_usage SET totalUsageToday = 0")
    suspend fun resetDailyUsage()

    @Transaction
    suspend fun incrementAppUsage(packageName: String, usageTime: Long) {
        val app = getAppUsage(packageName)
        if (app != null) {
            updateAppUsage(app.copy(
                totalUsageToday = app.totalUsageToday + usageTime,
                lastUsageTime = System.currentTimeMillis()
            ))
        }
    }

    @Query("SELECT COUNT(*) FROM app_usage WHERE isBlocked = 1")
    fun getBlockedAppCount(): Flow<Int>

    @Query("SELECT SUM(totalUsageToday) FROM app_usage WHERE category = :category")
    fun getCategoryUsageTime(category: AppCategory): Flow<Long>

    @Query("SELECT * FROM app_usage " +
           "WHERE category = :category " +
           "ORDER BY totalUsageToday DESC " +
           "LIMIT 5")
    fun getTopAppsInCategory(category: AppCategory): Flow<List<AppUsageEntity>>
}
