package com.darweshbhai.di

import android.content.Context
import com.darweshbhai.data.AppDatabase
import com.darweshbhai.data.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideTaskDao(database: AppDatabase): TaskDao {
        return database.taskDao()
    }

    @Provides
    @Singleton
    fun provideAppUsageDao(database: AppDatabase): AppUsageDao {
        return database.appUsageDao()
    }

    @Provides
    @Singleton
    fun provideFocusSessionDao(database: AppDatabase): FocusSessionDao {
        return database.focusSessionDao()
    }

    @Provides
    @Singleton
    fun provideReminderDao(database: AppDatabase): ReminderDao {
        return database.reminderDao()
    }

    @Provides
    @Singleton
    fun provideUserPreferencesDao(database: AppDatabase): UserPreferencesDao {
        return database.userPreferencesDao()
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): android.content.SharedPreferences {
        return context.getSharedPreferences(
            "darwesh_bhai_preferences",
            Context.MODE_PRIVATE
        )
    }
}
