package com.darweshbhai.repository

import com.darweshbhai.data.entity.FocusSessionEntity
import com.darweshbhai.data.entity.SessionType
import kotlinx.coroutines.flow.Flow

interface FocusSessionRepository {
    fun getAllSessions(): Flow<List<FocusSessionEntity>>
    suspend fun getSessionById(sessionId: String): FocusSessionEntity?
    fun getCompletedSessions(): Flow<List<FocusSessionEntity>>
    fun getSessionsByType(sessionType: SessionType): Flow<List<FocusSessionEntity>>
    fun getSessionsInTimeRange(startTime: Long, endTime: Long): Flow<List<FocusSessionEntity>>
    suspend fun insertSession(session: FocusSessionEntity)
    suspend fun updateSession(session: FocusSessionEntity)
    suspend fun deleteSession(session: FocusSessionEntity)
    suspend fun deleteSessions(sessionIds: List<String>)
    fun getTotalCompletedSessions(): Flow<Int>
    fun getCompletedSessionsByType(sessionType: SessionType): Flow<Int>
    fun getTotalFocusTime(): Flow<Long>
    fun getAverageFocusScore(): Flow<Float>
    fun getTotalInterruptions(): Flow<Int>
    fun getSessionsForDay(dayStart: Long, dayEnd: Long): Flow<List<FocusSessionEntity>>
    fun getFocusTimeForDay(dayStart: Long, dayEnd: Long): Flow<Long>
    fun getCompletedSessionsForWeek(weekStart: Long, weekEnd: Long): Flow<Int>
    fun getAverageFocusScoreForMonth(monthStart: Long, monthEnd: Long): Flow<Float>
    fun getConsecutiveFocusDays(streakStart: Long, streakEnd: Long): Flow<Int>
    fun getSessionCompletionRate(): Flow<Float>
    fun getMostProductiveHour(): Flow<String>
    fun getRecentSessionsWithTasks(limit: Int): Flow<List<FocusSessionEntity>>
    suspend fun completeSession(
        sessionId: String,
        endTime: Long,
        actualDuration: Long,
        wasInterrupted: Boolean = false,
        interruptionCount: Int = 0,
        focusScore: Int? = null
    )
}

class FocusSessionRepositoryImpl(
    private val focusSessionDao: com.darweshbhai.data.dao.FocusSessionDao
) : FocusSessionRepository {
    
    override fun getAllSessions(): Flow<List<FocusSessionEntity>> = focusSessionDao.getAllSessions()
    
    override suspend fun getSessionById(sessionId: String): FocusSessionEntity? =
        focusSessionDao.getSessionById(sessionId)
    
    override fun getCompletedSessions(): Flow<List<FocusSessionEntity>> =
        focusSessionDao.getCompletedSessions()
    
    override fun getSessionsByType(sessionType: SessionType): Flow<List<FocusSessionEntity>> =
        focusSessionDao.getSessionsByType(sessionType)
    
    override fun getSessionsInTimeRange(
        startTime: Long,
        endTime: Long
    ): Flow<List<FocusSessionEntity>> =
        focusSessionDao.getSessionsInTimeRange(startTime, endTime)
    
    override suspend fun insertSession(session: FocusSessionEntity) =
        focusSessionDao.insertSession(session)
    
    override suspend fun updateSession(session: FocusSessionEntity) =
        focusSessionDao.updateSession(session)
    
    override suspend fun deleteSession(session: FocusSessionEntity) =
        focusSessionDao.deleteSession(session)
    
    override suspend fun deleteSessions(sessionIds: List<String>) =
        focusSessionDao.deleteSessions(sessionIds)
    
    override fun getTotalCompletedSessions(): Flow<Int> =
        focusSessionDao.getTotalCompletedSessions()
    
    override fun getCompletedSessionsByType(sessionType: SessionType): Flow<Int> =
        focusSessionDao.getCompletedSessionsByType(sessionType)
    
    override fun getTotalFocusTime(): Flow<Long> = focusSessionDao.getTotalFocusTime()
    
    override fun getAverageFocusScore(): Flow<Float> = focusSessionDao.getAverageFocusScore()
    
    override fun getTotalInterruptions(): Flow<Int> = focusSessionDao.getTotalInterruptions()
    
    override fun getSessionsForDay(dayStart: Long, dayEnd: Long): Flow<List<FocusSessionEntity>> =
        focusSessionDao.getSessionsForDay(dayStart, dayEnd)
    
    override fun getFocusTimeForDay(dayStart: Long, dayEnd: Long): Flow<Long> =
        focusSessionDao.getFocusTimeForDay(dayStart, dayEnd)
    
    override fun getCompletedSessionsForWeek(weekStart: Long, weekEnd: Long): Flow<Int> =
        focusSessionDao.getCompletedSessionsForWeek(weekStart, weekEnd)
    
    override fun getAverageFocusScoreForMonth(monthStart: Long, monthEnd: Long): Flow<Float> =
        focusSessionDao.getAverageFocusScoreForMonth(monthStart, monthEnd)
    
    override fun getConsecutiveFocusDays(streakStart: Long, streakEnd: Long): Flow<Int> =
        focusSessionDao.getConsecutiveFocusDays(streakStart, streakEnd)
    
    override fun getSessionCompletionRate(): Flow<Float> =
        focusSessionDao.getSessionCompletionRate()
    
    override fun getMostProductiveHour(): Flow<String> = focusSessionDao.getMostProductiveHour()
    
    override fun getRecentSessionsWithTasks(limit: Int): Flow<List<FocusSessionEntity>> =
        focusSessionDao.getRecentSessionsWithTasks(limit)
    
    override suspend fun completeSession(
        sessionId: String,
        endTime: Long,
        actualDuration: Long,
        wasInterrupted: Boolean,
        interruptionCount: Int,
        focusScore: Int?
    ) = focusSessionDao.completeSession(
        sessionId,
        endTime,
        actualDuration,
        wasInterrupted,
        interruptionCount,
        focusScore
    )
}
