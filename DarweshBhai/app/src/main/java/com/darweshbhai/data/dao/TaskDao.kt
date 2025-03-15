package com.darweshbhai.data.dao

import androidx.room.*
import com.darweshbhai.data.entity.TaskEntity
import com.darweshbhai.data.entity.TaskPriority
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY " +
           "CASE WHEN dueDate IS NOT NULL THEN 0 ELSE 1 END, " +
           "dueDate ASC, " +
           "CASE priority " +
           "WHEN 'HIGH' THEN 1 " +
           "WHEN 'MEDIUM' THEN 2 " +
           "WHEN 'LOW' THEN 3 END")
    fun getActiveTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: String): TaskEntity?

    @Query("SELECT * FROM tasks WHERE dueDate BETWEEN :startDate AND :endDate")
    fun getTasksByDateRange(startDate: Long, endDate: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE dueDate IS NOT NULL AND dueDate > :now ORDER BY dueDate ASC")
    fun getUpcomingTasks(now: Long = System.currentTimeMillis()): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE priority = :priority ORDER BY createdAt DESC")
    fun getTasksByPriority(priority: TaskPriority): Flow<List<TaskEntity>>

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 0")
    fun getActiveTaskCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 1")
    fun getCompletedTaskCount(): Flow<Int>

    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentlyCompletedTasks(limit: Int = 10): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE " +
           "title LIKE '%' || :query || '%' OR " +
           "description LIKE '%' || :query || '%'")
    fun searchTasks(query: String): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE isCompleted = 1")
    suspend fun deleteCompletedTasks()

    @Query("DELETE FROM tasks WHERE id IN (:taskIds)")
    suspend fun deleteTasks(taskIds: List<String>)

    @Query("UPDATE tasks SET isCompleted = :completed WHERE id = :taskId")
    suspend fun setTaskCompleted(taskId: String, completed: Boolean)

    @Query("UPDATE tasks SET priority = :priority WHERE id = :taskId")
    suspend fun updateTaskPriority(taskId: String, priority: TaskPriority)

    @Transaction
    suspend fun toggleTaskCompleted(taskId: String) {
        val task = getTaskById(taskId)
        task?.let {
            updateTask(it.copy(
                isCompleted = !it.isCompleted,
                updatedAt = System.currentTimeMillis()
            ))
        }
    }

    @Query("SELECT * FROM tasks WHERE dueDate < :now AND isCompleted = 0")
    fun getOverdueTasks(now: Long = System.currentTimeMillis()): Flow<List<TaskEntity>>

    @Query("SELECT COUNT(*) FROM tasks WHERE dueDate < :now AND isCompleted = 0")
    fun getOverdueTaskCount(now: Long = System.currentTimeMillis()): Flow<Int>

    @Query("SELECT * FROM tasks WHERE dueDate BETWEEN :startOfDay AND :endOfDay")
    fun getTasksForDay(startOfDay: Long, endOfDay: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE " +
           "isCompleted = 0 AND priority = :priority " +
           "ORDER BY dueDate ASC NULLS LAST")
    fun getPriorityTasks(priority: TaskPriority): Flow<List<TaskEntity>>
}
