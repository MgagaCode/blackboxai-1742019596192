package com.darweshbhai.di

import com.darweshbhai.data.dao.*
import com.darweshbhai.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideTaskRepository(
        taskDao: TaskDao
    ): TaskRepository {
        return TaskRepositoryImpl(taskDao)
    }

    @Provides
    @Singleton
    fun provideAppUsageRepository(
        appUsageDao: AppUsageDao
    ): AppUsageRepository {
        return AppUsageRepositoryImpl(appUsageDao)
    }

    @Provides
    @Singleton
    fun provideFocusSessionRepository(
        focusSessionDao: FocusSessionDao
    ): FocusSessionRepository {
        return FocusSessionRepositoryImpl(focusSessionDao)
    }

    @Provides
    @Singleton
    fun provideReminderRepository(
        reminderDao: ReminderDao
    ): ReminderRepository {
        return ReminderRepositoryImpl(reminderDao)
    }

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(
        userPreferencesDao: UserPreferencesDao,
        sharedPreferences: android.content.SharedPreferences
    ): UserPreferencesRepository {
        return UserPreferencesRepositoryImpl(userPreferencesDao, sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideAnalyticsRepository(
        appUsageDao: AppUsageDao,
        focusSessionDao: FocusSessionDao,
        taskDao: TaskDao
    ): AnalyticsRepository {
        return AnalyticsRepositoryImpl(
            appUsageDao,
            focusSessionDao,
            taskDao
        )
    }

    @Provides
    @Singleton
    fun provideBackupRepository(
        appDatabase: com.darweshbhai.data.AppDatabase,
        userPreferencesRepository: UserPreferencesRepository
    ): BackupRepository {
        return BackupRepositoryImpl(
            appDatabase,
            userPreferencesRepository
        )
    }
}
