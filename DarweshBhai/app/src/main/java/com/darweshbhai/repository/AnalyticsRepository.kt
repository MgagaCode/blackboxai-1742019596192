package com.darweshbhai.repository

import com.darweshbhai.data.dao.AppUsageDao
import com.darweshbhai.data.dao.FocusSessionDao
import com.darweshbhai.data.dao.TaskDao
import com.darweshbhai.data.entity.AppCategory
import com.darweshbhai.data.entity.SessionType
import kotlinx.coroutines.flow.*
import java.util.*

interface AnalyticsRepository {
    // Screen Time Analytics
    fun getTotalScreenTime(): Flow<Long>
    fun getScreenTimeByCategory(category: AppCategory): Flow<Long>
    fun getMostUsedApps(limit: Int): Flow<List<com.darweshbhai.data.entity.AppUsageEntity>>
    fun getAppUsageTrend(startTime: Long, endTime: Long): Flow<Map<Long, Long>>

    // Focus Session Analytics
    fun getTotalFocusTime(): Flow<Long>
    fun getFocusTimeByType(sessionType: SessionType): Flow<Long>
    fun getFocusSessionStats(): Flow<FocusSessionStats>
    fun getFocusStreak(): Flow<Int>
    fun getProductivityScore(): Flow<Float>

    // Task Analytics
    fun getTaskCompletionRate(): Flow<Float>
    fun getTasksByPriorityDistribution(): Flow<Map<com.darweshbhai.data.entity.TaskPriority, Int>>
    fun getTaskCompletionTrend(startTime: Long, endTime: Long): Flow<Map<Long, Int>>
    fun getAverageTaskCompletionTime(): Flow<Long>

    // Combined Analytics
    fun getDailyProductivityReport(date: Long): Flow<DailyProductivityReport>
    fun getWeeklyProductivityReport(startDate: Long): Flow<WeeklyProductivityReport>
    fun getMonthlyProductivityReport(startDate: Long): Flow<MonthlyProductivityReport>
}

class AnalyticsRepositoryImpl(
    private val appUsageDao: AppUsageDao,
    private val focusSessionDao: FocusSessionDao,
    private val taskDao: TaskDao
) : AnalyticsRepository {

    override fun getTotalScreenTime(): Flow<Long> = appUsageDao.getTotalScreenTime()

    override fun getScreenTimeByCategory(category: AppCategory): Flow<Long> =
        appUsageDao.getCategoryUsageTime(category)

    override fun getMostUsedApps(limit: Int): Flow<List<com.darweshbhai.data.entity.AppUsageEntity>> =
        appUsageDao.getMostUsedApps(limit)

    override fun getAppUsageTrend(startTime: Long, endTime: Long): Flow<Map<Long, Long>> = flow {
        // Implementation to aggregate app usage data by time periods
    }

    override fun getTotalFocusTime(): Flow<Long> = focusSessionDao.getTotalFocusTime()

    override fun getFocusTimeByType(sessionType: SessionType): Flow<Long> = flow {
        // Implementation to calculate focus time for specific session type
    }

    override fun getFocusSessionStats(): Flow<FocusSessionStats> = flow {
        val completedSessions = focusSessionDao.getTotalCompletedSessions()
        val averageScore = focusSessionDao.getAverageFocusScore()
        val totalInterruptions = focusSessionDao.getTotalInterruptions()

        completedSessions.combine(averageScore) { sessions, score ->
            sessions to score
        }.combine(totalInterruptions) { (sessions, score), interruptions ->
            FocusSessionStats(
                totalSessions = sessions,
                averageScore = score,
                totalInterruptions = interruptions
            )
        }
    }

    override fun getFocusStreak(): Flow<Int> {
        val now = System.currentTimeMillis()
        val thirtyDaysAgo = now - (30 * 24 * 60 * 60 * 1000)
        return focusSessionDao.getConsecutiveFocusDays(thirtyDaysAgo, now)
    }

    override fun getProductivityScore(): Flow<Float> = flow {
        // Implementation to calculate overall productivity score
    }

    override fun getTaskCompletionRate(): Flow<Float> = flow {
        val totalTasks = taskDao.getActiveTaskCount()
        val completedTasks = taskDao.getCompletedTaskCount()
        
        totalTasks.combine(completedTasks) { total, completed ->
            if (total > 0) (completed.toFloat() / total) * 100 else 0f
        }
    }

    override fun getTasksByPriorityDistribution(): Flow<Map<com.darweshbhai.data.entity.TaskPriority, Int>> = flow {
        // Implementation to calculate task distribution by priority
    }

    override fun getTaskCompletionTrend(startTime: Long, endTime: Long): Flow<Map<Long, Int>> = flow {
        // Implementation to track task completion over time
    }

    override fun getAverageTaskCompletionTime(): Flow<Long> = flow {
        // Implementation to calculate average task completion time
    }

    override fun getDailyProductivityReport(date: Long): Flow<DailyProductivityReport> = flow {
        val dayStart = Calendar.getInstance().apply {
            timeInMillis = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.timeInMillis

        val dayEnd = dayStart + 24 * 60 * 60 * 1000

        val focusTime = focusSessionDao.getFocusTimeForDay(dayStart, dayEnd)
        val screenTime = appUsageDao.getTotalScreenTime()
        val completedTasks = taskDao.getCompletedTaskCount()

        combine(
            focusTime,
            screenTime,
            completedTasks
        ) { focus, screen, tasks ->
            DailyProductivityReport(
                date = date,
                totalFocusTime = focus,
                totalScreenTime = screen,
                completedTasks = tasks
            )
        }
    }

    override fun getWeeklyProductivityReport(startDate: Long): Flow<WeeklyProductivityReport> = flow {
        // Implementation to generate weekly productivity report
    }

    override fun getMonthlyProductivityReport(startDate: Long): Flow<MonthlyProductivityReport> = flow {
        // Implementation to generate monthly productivity report
    }
}

data class FocusSessionStats(
    val totalSessions: Int,
    val averageScore: Float,
    val totalInterruptions: Int
)

data class DailyProductivityReport(
    val date: Long,
    val totalFocusTime: Long,
    val totalScreenTime: Long,
    val completedTasks: Int
)

data class WeeklyProductivityReport(
    val startDate: Long,
    val endDate: Long,
    val dailyReports: List<DailyProductivityReport>,
    val weeklyFocusTime: Long,
    val weeklyScreenTime: Long,
    val weeklyCompletedTasks: Int,
    val productivityTrend: Float
)

data class MonthlyProductivityReport(
    val month: Int,
    val year: Int,
    val weeklyReports: List<WeeklyProductivityReport>,
    val monthlyFocusTime: Long,
    val monthlyScreenTime: Long,
    val monthlyCompletedTasks: Int,
    val productivityScore: Float,
    val mostProductiveWeek: Int,
    val mostProductiveDay: Long
)
