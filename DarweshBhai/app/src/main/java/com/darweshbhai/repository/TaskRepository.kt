package com.darweshbhai.repository

import com.darweshbhai.data.entity.TaskEntity
import com.darweshbhai.data.entity.TaskPriority
import kotlinx.coroutines.flow.Flow
import java.util.*

interface TaskRepository {
    fun getAllTasks(): Flow<List<TaskEntity>>
    fun getActiveTasks(): Flow<List<TaskEntity>>
    suspend fun getTaskById(taskId: String): TaskEntity?
    fun getTasksByDateRange(startDate: Long, endDate: Long): Flow<List<TaskEntity>>
    fun getUpcomingTasks(): Flow<List<TaskEntity>>
    fun getTasksByPriority(priority: TaskPriority): Flow<List<TaskEntity>>
    fun getActiveTaskCount(): Flow<Int>
    fun getCompletedTaskCount(): Flow<Int>
    fun getRecentlyCompletedTasks(limit: Int = 10): Flow<List<TaskEntity>>
    fun searchTasks(query: String): Flow<List<TaskEntity>>
    suspend fun insertTask(task: TaskEntity)
    suspend fun insertTasks(tasks: List<TaskEntity>)
    suspend fun updateTask(task: TaskEntity)
    suspend fun deleteTask(task: TaskEntity)
    suspend fun deleteCompletedTasks()
    suspend fun deleteTasks(taskIds: List<String>)
    suspend fun setTaskCompleted(taskId: String, completed: Boolean)
    suspend fun updateTaskPriority(taskId: String, priority: TaskPriority)
    suspend fun toggleTaskCompleted(taskId: String)
    fun getOverdueTasks(): Flow<List<TaskEntity>>
    fun getOverdueTaskCount(): Flow<Int>
    fun getTasksForDay(startOfDay: Long, endOfDay: Long): Flow<List<TaskEntity>>
    fun getPriorityTasks(priority: TaskPriority): Flow<List<TaskEntity>>
}

class TaskRepositoryImpl(
    private val taskDao: com.darweshbhai.data.dao.TaskDao
) : TaskRepository {
    
    override fun getAllTasks(): Flow<List<TaskEntity>> = taskDao.getAllTasks()
    
    override fun getActiveTasks(): Flow<List<TaskEntity>> = taskDao.getActiveTasks()
    
    override suspend fun getTaskById(taskId: String): TaskEntity? = taskDao.getTaskById(taskId)
    
    override fun getTasksByDateRange(startDate: Long, endDate: Long): Flow<List<TaskEntity>> =
        taskDao.getTasksByDateRange(startDate, endDate)
    
    override fun getUpcomingTasks(): Flow<List<TaskEntity>> = taskDao.getUpcomingTasks()
    
    override fun getTasksByPriority(priority: TaskPriority): Flow<List<TaskEntity>> =
        taskDao.getTasksByPriority(priority)
    
    override fun getActiveTaskCount(): Flow<Int> = taskDao.getActiveTaskCount()
    
    override fun getCompletedTaskCount(): Flow<Int> = taskDao.getCompletedTaskCount()
    
    override fun getRecentlyCompletedTasks(limit: Int): Flow<List<TaskEntity>> =
        taskDao.getRecentlyCompletedTasks(limit)
    
    override fun searchTasks(query: String): Flow<List<TaskEntity>> = taskDao.searchTasks(query)
    
    override suspend fun insertTask(task: TaskEntity) = taskDao.insertTask(task)
    
    override suspend fun insertTasks(tasks: List<TaskEntity>) = taskDao.insertTasks(tasks)
    
    override suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)
    
    override suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)
    
    override suspend fun deleteCompletedTasks() = taskDao.deleteCompletedTasks()
    
    override suspend fun deleteTasks(taskIds: List<String>) = taskDao.deleteTasks(taskIds)
    
    override suspend fun setTaskCompleted(taskId: String, completed: Boolean) =
        taskDao.setTaskCompleted(taskId, completed)
    
    override suspend fun updateTaskPriority(taskId: String, priority: TaskPriority) =
        taskDao.updateTaskPriority(taskId, priority)
    
    override suspend fun toggleTaskCompleted(taskId: String) = taskDao.toggleTaskCompleted(taskId)
    
    override fun getOverdueTasks(): Flow<List<TaskEntity>> = taskDao.getOverdueTasks()
    
    override fun getOverdueTaskCount(): Flow<Int> = taskDao.getOverdueTaskCount()
    
    override fun getTasksForDay(startOfDay: Long, endOfDay: Long): Flow<List<TaskEntity>> =
        taskDao.getTasksForDay(startOfDay, endOfDay)
    
    override fun getPriorityTasks(priority: TaskPriority): Flow<List<TaskEntity>> =
        taskDao.getPriorityTasks(priority)
}
