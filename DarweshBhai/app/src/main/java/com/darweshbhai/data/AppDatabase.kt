package com.darweshbhai.data

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.darweshbhai.data.dao.*
import com.darweshbhai.data.entity.*
import com.darweshbhai.data.converter.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TaskEntity::class,
        AppUsageEntity::class,
        FocusSessionEntity::class,
        ReminderEntity::class,
        UserPreferencesEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    DateConverter::class,
    ListConverter::class,
    EnumConverters::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun appUsageDao(): AppUsageDao
    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun reminderDao(): ReminderDao
    abstract fun userPreferencesDao(): UserPreferencesDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "darwesh_bhai_db"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Initialize database with default values
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.let { database ->
                                initializeDatabase(database)
                            }
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun initializeDatabase(database: AppDatabase) {
            // Initialize user preferences with default values
            database.userPreferencesDao().insertUserPreferences(
                UserPreferencesEntity()
            )

            // Add default app categories
            val defaultApps = listOf(
                AppUsageEntity(
                    packageName = "com.whatsapp",
                    appName = "WhatsApp",
                    category = AppCategory.SOCIAL_MEDIA
                ),
                AppUsageEntity(
                    packageName = "com.instagram.android",
                    appName = "Instagram",
                    category = AppCategory.SOCIAL_MEDIA
                ),
                AppUsageEntity(
                    packageName = "com.google.android.youtube",
                    appName = "YouTube",
                    category = AppCategory.ENTERTAINMENT
                )
            )
            database.appUsageDao().insertAppUsages(defaultApps)
        }
    }
}

// Type converters
object DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}

object ListConverter {
    @TypeConverter
    fun fromString(value: String): List<String> {
        return if (value.isBlank()) {
            emptyList()
        } else {
            value.split(",").map { it.trim() }
        }
    }

    @TypeConverter
    fun toString(list: List<String>): String {
        return list.joinToString(",")
    }
}

object EnumConverters {
    @TypeConverter
    fun toTaskPriority(value: String) = enumValueOf<TaskPriority>(value)
    @TypeConverter
    fun fromTaskPriority(value: TaskPriority) = value.name

    @TypeConverter
    fun toAppCategory(value: String) = enumValueOf<AppCategory>(value)
    @TypeConverter
    fun fromAppCategory(value: AppCategory) = value.name

    @TypeConverter
    fun toSessionType(value: String) = enumValueOf<SessionType>(value)
    @TypeConverter
    fun fromSessionType(value: SessionType) = value.name

    @TypeConverter
    fun toReminderType(value: String) = enumValueOf<ReminderType>(value)
    @TypeConverter
    fun fromReminderType(value: ReminderType) = value.name

    @TypeConverter
    fun toReminderPriority(value: String) = enumValueOf<ReminderPriority>(value)
    @TypeConverter
    fun fromReminderPriority(value: ReminderPriority) = value.name

    @TypeConverter
    fun toRepeatInterval(value: String?) = value?.let { enumValueOf<RepeatInterval>(it) }
    @TypeConverter
    fun fromRepeatInterval(value: RepeatInterval?) = value?.name

    @TypeConverter
    fun toTimeFormat(value: String) = enumValueOf<TimeFormat>(value)
    @TypeConverter
    fun fromTimeFormat(value: TimeFormat) = value.name

    @TypeConverter
    fun toDateFormat(value: String) = enumValueOf<DateFormat>(value)
    @TypeConverter
    fun fromDateFormat(value: DateFormat) = value.name

    @TypeConverter
    fun toTaskSortOption(value: String) = enumValueOf<TaskSortOption>(value)
    @TypeConverter
    fun fromTaskSortOption(value: TaskSortOption) = value.name

    @TypeConverter
    fun toBackupFrequency(value: String) = enumValueOf<BackupFrequency>(value)
    @TypeConverter
    fun fromBackupFrequency(value: BackupFrequency) = value.name
}
