package com.darweshbhai.di

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.usage.UsageStatsManager
import android.content.Context
import com.darweshbhai.service.AlarmService
import com.darweshbhai.service.NotificationService
import com.darweshbhai.service.PomodoroService
import com.darweshbhai.service.UsageMonitoringService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Provides
    @Singleton
    fun provideAlarmManager(
        @ApplicationContext context: Context
    ): AlarmManager {
        return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    @Provides
    @Singleton
    fun provideNotificationManager(
        @ApplicationContext context: Context
    ): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @Provides
    @Singleton
    fun provideUsageStatsManager(
        @ApplicationContext context: Context
    ): UsageStatsManager {
        return context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    }

    @Provides
    @Singleton
    fun provideAlarmService(
        alarmManager: AlarmManager,
        notificationManager: NotificationManager,
        @ApplicationContext context: Context
    ): AlarmService {
        return AlarmService().apply {
            onCreate()
        }
    }

    @Provides
    @Singleton
    fun provideNotificationService(
        notificationManager: NotificationManager,
        @ApplicationContext context: Context
    ): NotificationService {
        return NotificationService().apply {
            onCreate()
        }
    }

    @Provides
    @Singleton
    fun providePomodoroService(
        notificationManager: NotificationManager,
        @ApplicationContext context: Context
    ): PomodoroService {
        return PomodoroService().apply {
            onCreate()
        }
    }

    @Provides
    @Singleton
    fun provideUsageMonitoringService(
        usageStatsManager: UsageStatsManager,
        notificationManager: NotificationManager,
        @ApplicationContext context: Context
    ): UsageMonitoringService {
        return UsageMonitoringService().apply {
            onCreate()
        }
    }

    @Provides
    @Singleton
    fun provideWorkManager(
        @ApplicationContext context: Context
    ): androidx.work.WorkManager {
        return androidx.work.WorkManager.getInstance(context)
    }
}
