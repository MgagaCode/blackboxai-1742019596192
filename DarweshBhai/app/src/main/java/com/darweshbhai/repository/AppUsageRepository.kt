package com.darweshbhai.repository

import com.darweshbhai.data.entity.AppUsageEntity
import com.darweshbhai.data.entity.AppCategory
import kotlinx.coroutines.flow.Flow

interface AppUsageRepository {
    fun getAllAppUsage(): Flow<List<AppUsageEntity>>
    suspend fun getAppUsage(packageName: String): AppUsageEntity?
    fun getBlockedApps(): Flow<List<AppUsageEntity>>
    fun getWhitelistedApps(): Flow<List<AppUsageEntity>>
    fun getAppsByCategory(category: AppCategory): Flow<List<AppUsageEntity>>
    fun getAppsWithLimits(): Flow<List<AppUsageEntity>>
    fun getTotalScreenTime(): Flow<Long>
    fun getOverLimitApps(): Flow<List<AppUsageEntity>>
    suspend fun insertAppUsage(appUsage: AppUsageEntity)
    suspend fun insertAppUsages(appUsages: List<AppUsageEntity>)
    suspend fun updateAppUsage(appUsage: AppUsageEntity)
    suspend fun deleteAppUsage(appUsage: AppUsageEntity)
    suspend fun setAppBlocked(packageName: String, blocked: Boolean)
    suspend fun setAppWhitelisted(packageName: String, whitelisted: Boolean)
    suspend fun setAppDailyLimit(packageName: String, limitInMillis: Long)
    suspend fun setAppCategory(packageName: String, category: AppCategory)
    suspend fun updateAppUsageTime(packageName: String, usage: Long)
    suspend fun updateLastUsageTime(packageName: String, timestamp: Long)
    fun getMostUsedApps(limit: Int): Flow<List<AppUsageEntity>>
    fun getRecentlyUsedApps(since: Long): Flow<List<AppUsageEntity>>
    suspend fun resetDailyUsage()
    suspend fun incrementAppUsage(packageName: String, usageTime: Long)
    fun getBlockedAppCount(): Flow<Int>
    fun getCategoryUsageTime(category: AppCategory): Flow<Long>
    fun getTopAppsInCategory(category: AppCategory): Flow<List<AppUsageEntity>>
}

class AppUsageRepositoryImpl(
    private val appUsageDao: com.darweshbhai.data.dao.AppUsageDao
) : AppUsageRepository {
    
    override fun getAllAppUsage(): Flow<List<AppUsageEntity>> = appUsageDao.getAllAppUsage()
    
    override suspend fun getAppUsage(packageName: String): AppUsageEntity? =
        appUsageDao.getAppUsage(packageName)
    
    override fun getBlockedApps(): Flow<List<AppUsageEntity>> = appUsageDao.getBlockedApps()
    
    override fun getWhitelistedApps(): Flow<List<AppUsageEntity>> = appUsageDao.getWhitelistedApps()
    
    override fun getAppsByCategory(category: AppCategory): Flow<List<AppUsageEntity>> =
        appUsageDao.getAppsByCategory(category)
    
    override fun getAppsWithLimits(): Flow<List<AppUsageEntity>> = appUsageDao.getAppsWithLimits()
    
    override fun getTotalScreenTime(): Flow<Long> = appUsageDao.getTotalScreenTime()
    
    override fun getOverLimitApps(): Flow<List<AppUsageEntity>> = appUsageDao.getOverLimitApps()
    
    override suspend fun insertAppUsage(appUsage: AppUsageEntity) =
        appUsageDao.insertAppUsage(appUsage)
    
    override suspend fun insertAppUsages(appUsages: List<AppUsageEntity>) =
        appUsageDao.insertAppUsages(appUsages)
    
    override suspend fun updateAppUsage(appUsage: AppUsageEntity) =
        appUsageDao.updateAppUsage(appUsage)
    
    override suspend fun deleteAppUsage(appUsage: AppUsageEntity) =
        appUsageDao.deleteAppUsage(appUsage)
    
    override suspend fun setAppBlocked(packageName: String, blocked: Boolean) =
        appUsageDao.setAppBlocked(packageName, blocked)
    
    override suspend fun setAppWhitelisted(packageName: String, whitelisted: Boolean) =
        appUsageDao.setAppWhitelisted(packageName, whitelisted)
    
    override suspend fun setAppDailyLimit(packageName: String, limitInMillis: Long) =
        appUsageDao.setAppDailyLimit(packageName, limitInMillis)
    
    override suspend fun setAppCategory(packageName: String, category: AppCategory) =
        appUsageDao.setAppCategory(packageName, category)
    
    override suspend fun updateAppUsageTime(packageName: String, usage: Long) =
        appUsageDao.updateAppUsageTime(packageName, usage)
    
    override suspend fun updateLastUsageTime(packageName: String, timestamp: Long) =
        appUsageDao.updateLastUsageTime(packageName, timestamp)
    
    override fun getMostUsedApps(limit: Int): Flow<List<AppUsageEntity>> =
        appUsageDao.getMostUsedApps(limit)
    
    override fun getRecentlyUsedApps(since: Long): Flow<List<AppUsageEntity>> =
        appUsageDao.getRecentlyUsedApps(since)
    
    override suspend fun resetDailyUsage() = appUsageDao.resetDailyUsage()
    
    override suspend fun incrementAppUsage(packageName: String, usageTime: Long) =
        appUsageDao.incrementAppUsage(packageName, usageTime)
    
    override fun getBlockedAppCount(): Flow<Int> = appUsageDao.getBlockedAppCount()
    
    override fun getCategoryUsageTime(category: AppCategory): Flow<Long> =
        appUsageDao.getCategoryUsageTime(category)
    
    override fun getTopAppsInCategory(category: AppCategory): Flow<List<AppUsageEntity>> =
        appUsageDao.getTopAppsInCategory(category)
}
