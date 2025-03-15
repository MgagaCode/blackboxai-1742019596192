package com.darweshbhai.data.dao

import androidx.room.*
import com.darweshbhai.data.entity.FocusSessionEntity
import com.darweshbhai.data.entity.SessionType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface FocusSessionDao {
    @Query("SELECT * FROM focus_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<FocusSessionEntity>>

    @Query("SELECT * FROM focus_sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: String): FocusSessionEntity?

    @Query("SELECT * FROM focus_sessions WHERE isCompleted = 1 ORDER BY endTime DESC")
    fun getCompletedSessions(): Flow<List<FocusSessionEntity>>

    @Query("SELECT * FROM focus_sessions WHERE type = :sessionType ORDER BY startTime DESC")
    fun getSessionsByType(sessionType: SessionType): Flow<List<FocusSessionEntity>>

    @Query("SELECT * FROM focus_sessions " +
           "WHERE startTime >= :startTime AND startTime < :endTime " +
           "ORDER BY startTime DESC")
    fun getSessionsInTimeRange(startTime: Long, endTime: Long): Flow<List<FocusSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: FocusSessionEntity)

    @Update
    suspend fun updateSession(session: FocusSessionEntity)

    @Delete
    suspend fun deleteSession(session: FocusSessionEntity)

    @Query("DELETE FROM focus_sessions WHERE id IN (:sessionIds)")
    suspend fun deleteSessions(sessionIds: List<String>)

    // Statistics queries
    @Query("SELECT COUNT(*) FROM focus_sessions WHERE isCompleted = 1")
    fun getTotalCompletedSessions(): Flow<Int>

    @Query("SELECT COUNT(*) FROM focus_sessions WHERE type = :sessionType AND isCompleted = 1")
    fun getCompletedSessionsByType(sessionType: SessionType): Flow<Int>

    @Query("SELECT SUM(actualDuration) FROM focus_sessions WHERE isCompleted = 1")
    fun getTotalFocusTime(): Flow<Long>

    @Query("SELECT AVG(CAST(focusScore as FLOAT)) FROM focus_sessions WHERE isCompleted = 1")
    fun getAverageFocusScore(): Flow<Float>

    @Query("SELECT SUM(interruptionCount) FROM focus_sessions WHERE isCompleted = 1")
    fun getTotalInterruptions(): Flow<Int>

    // Daily statistics
    @Query("SELECT * FROM focus_sessions " +
           "WHERE startTime >= :dayStart AND startTime < :dayEnd " +
           "ORDER BY startTime DESC")
    fun getSessionsForDay(dayStart: Long, dayEnd: Long): Flow<List<FocusSessionEntity>>

    @Query("SELECT SUM(actualDuration) FROM focus_sessions " +
           "WHERE isCompleted = 1 AND startTime >= :dayStart AND startTime < :dayEnd")
    fun getFocusTimeForDay(dayStart: Long, dayEnd: Long): Flow<Long>

    // Weekly statistics
    @Query("SELECT COUNT(*) FROM focus_sessions " +
           "WHERE isCompleted = 1 AND startTime >= :weekStart AND startTime < :weekEnd")
    fun getCompletedSessionsForWeek(weekStart: Long, weekEnd: Long): Flow<Int>

    // Monthly statistics
    @Query("SELECT AVG(CAST(focusScore as FLOAT)) FROM focus_sessions " +
           "WHERE isCompleted = 1 AND startTime >= :monthStart AND startTime < :monthEnd")
    fun getAverageFocusScoreForMonth(monthStart: Long, monthEnd: Long): Flow<Float>

    // Streak calculation
    @Query("SELECT COUNT(DISTINCT date(startTime/1000, 'unixepoch')) " +
           "FROM focus_sessions " +
           "WHERE isCompleted = 1 AND startTime >= :streakStart AND startTime <= :streakEnd")
    fun getConsecutiveFocusDays(streakStart: Long, streakEnd: Long): Flow<Int>

    // Session completion rate
    @Query("SELECT (SELECT COUNT(*) FROM focus_sessions WHERE isCompleted = 1) * 100.0 / " +
           "(SELECT COUNT(*) FROM focus_sessions) WHERE (SELECT COUNT(*) FROM focus_sessions) > 0")
    fun getSessionCompletionRate(): Flow<Float>

    // Most productive time analysis
    @Query("SELECT strftime('%H', startTime/1000, 'unixepoch') as hour, " +
           "COUNT(*) as session_count " +
           "FROM focus_sessions " +
           "WHERE isCompleted = 1 " +
           "GROUP BY hour " +
           "ORDER BY session_count DESC " +
           "LIMIT 1")
    fun getMostProductiveHour(): Flow<String>

    // Recent sessions with tasks
    @Query("SELECT * FROM focus_sessions " +
           "WHERE associatedTasks IS NOT NULL AND associatedTasks != '[]' " +
           "ORDER BY startTime DESC LIMIT :limit")
    fun getRecentSessionsWithTasks(limit: Int): Flow<List<FocusSessionEntity>>

    @Transaction
    suspend fun completeSession(
        sessionId: String,
        endTime: Long,
        actualDuration: Long,
        wasInterrupted: Boolean = false,
        interruptionCount: Int = 0,
        focusScore: Int? = null
    ) {
        val session = getSessionById(sessionId)
        session?.let {
            updateSession(it.copy(
                endTime = endTime,
                actualDuration = actualDuration,
                isCompleted = true,
                wasInterrupted = wasInterrupted,
                interruptionCount = interruptionCount,
                focusScore = focusScore
            ))
        }
    }
}
