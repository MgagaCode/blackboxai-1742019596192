package com.darweshbhai.util

import com.darweshbhai.data.entity.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

// TaskEntity JSON Extensions
fun TaskEntity.toJson(): JSONObject = JSONObject().apply {
    put("id", id)
    put("title", title)
    put("description", description)
    put("priority", priority.name)
    put("dueDate", dueDate)
    put("isCompleted", isCompleted)
    put("createdAt", createdAt)
    put("updatedAt", updatedAt)
}

fun TaskEntity.Companion.fromJson(json: JSONObject): TaskEntity = TaskEntity(
    id = json.getString("id"),
    title = json.getString("title"),
    description = json.getString("description"),
    priority = TaskPriority.valueOf(json.getString("priority")),
    dueDate = if (json.has("dueDate")) json.getLong("dueDate") else null,
    isCompleted = json.getBoolean("isCompleted"),
    createdAt = json.getLong("createdAt"),
    updatedAt = json.getLong("updatedAt")
)

// AppUsageEntity JSON Extensions
fun AppUsageEntity.toJson(): JSONObject = JSONObject().apply {
    put("packageName", packageName)
    put("appName", appName)
    put("dailyLimit", dailyLimit)
    put("isBlocked", isBlocked)
    put("lastUsageTime", lastUsageTime)
    put("totalUsageToday", totalUsageToday)
    put("updatedAt", updatedAt)
    put("isWhitelisted", isWhitelisted)
    put("category", category.name)
}

fun AppUsageEntity.Companion.fromJson(json: JSONObject): AppUsageEntity = AppUsageEntity(
    packageName = json.getString("packageName"),
    appName = json.getString("appName"),
    dailyLimit = json.getLong("dailyLimit"),
    isBlocked = json.getBoolean("isBlocked"),
    lastUsageTime = json.getLong("lastUsageTime"),
    totalUsageToday = json.getLong("totalUsageToday"),
    updatedAt = json.getLong("updatedAt"),
    isWhitelisted = json.getBoolean("isWhitelisted"),
    category = AppCategory.valueOf(json.getString("category"))
)

// FocusSessionEntity JSON Extensions
fun FocusSessionEntity.toJson(): JSONObject = JSONObject().apply {
    put("id", id)
    put("startTime", startTime)
    put("endTime", endTime)
    put("duration", duration)
    put("actualDuration", actualDuration)
    put("type", type.name)
    put("isCompleted", isCompleted)
    put("wasInterrupted", wasInterrupted)
    put("interruptionCount", interruptionCount)
    put("focusScore", focusScore)
    put("notes", notes)
    put("associatedTasks", JSONArray(associatedTasks))
    put("createdAt", createdAt)
}

fun FocusSessionEntity.Companion.fromJson(json: JSONObject): FocusSessionEntity = FocusSessionEntity(
    id = json.getString("id"),
    startTime = json.getLong("startTime"),
    endTime = if (json.has("endTime")) json.getLong("endTime") else null,
    duration = json.getLong("duration"),
    actualDuration = if (json.has("actualDuration")) json.getLong("actualDuration") else null,
    type = SessionType.valueOf(json.getString("type")),
    isCompleted = json.getBoolean("isCompleted"),
    wasInterrupted = json.getBoolean("wasInterrupted"),
    interruptionCount = json.getInt("interruptionCount"),
    focusScore = if (json.has("focusScore")) json.getInt("focusScore") else null,
    notes = json.getString("notes"),
    associatedTasks = json.getJSONArray("associatedTasks").let { array ->
        (0 until array.length()).map { array.getString(it) }
    },
    createdAt = json.getLong("createdAt")
)

// ReminderEntity JSON Extensions
fun ReminderEntity.toJson(): JSONObject = JSONObject().apply {
    put("id", id)
    put("title", title)
    put("description", description)
    put("time", time)
    put("type", type.name)
    put("repeatInterval", repeatInterval?.name)
    put("customRepeatInterval", customRepeatInterval)
    put("isEnabled", isEnabled)
    put("lastTriggered", lastTriggered)
    put("nextTrigger", nextTrigger)
    put("associatedTaskId", associatedTaskId)
    put("priority", priority.name)
    put("tags", JSONArray(tags))
    put("createdAt", createdAt)
    put("updatedAt", updatedAt)
}

fun ReminderEntity.Companion.fromJson(json: JSONObject): ReminderEntity = ReminderEntity(
    id = json.getString("id"),
    title = json.getString("title"),
    description = json.getString("description"),
    time = json.getLong("time"),
    type = ReminderType.valueOf(json.getString("type")),
    repeatInterval = if (json.has("repeatInterval")) 
        RepeatInterval.valueOf(json.getString("repeatInterval")) else null,
    customRepeatInterval = if (json.has("customRepeatInterval")) 
        json.getLong("customRepeatInterval") else null,
    isEnabled = json.getBoolean("isEnabled"),
    lastTriggered = if (json.has("lastTriggered")) json.getLong("lastTriggered") else null,
    nextTrigger = if (json.has("nextTrigger")) json.getLong("nextTrigger") else null,
    associatedTaskId = if (json.has("associatedTaskId")) json.getString("associatedTaskId") else null,
    priority = ReminderPriority.valueOf(json.getString("priority")),
    tags = json.getJSONArray("tags").let { array ->
        (0 until array.length()).map { array.getString(it) }
    },
    createdAt = json.getLong("createdAt"),
    updatedAt = json.getLong("updatedAt")
)

// UserPreferencesEntity JSON Extensions
fun UserPreferencesEntity.toJson(): JSONObject = JSONObject().apply {
    put("id", id)
    put("isDarkMode", isDarkMode)
    put("accentColor", accentColor)
    put("isNotificationsEnabled", isNotificationsEnabled)
    put("notificationSound", notificationSound)
    put("notificationVibration", notificationVibration)
    put("quietHoursEnabled", quietHoursEnabled)
    put("quietHoursStart", quietHoursStart)
    put("quietHoursEnd", quietHoursEnd)
    put("pomodoroLength", pomodoroLength)
    put("shortBreakLength", shortBreakLength)
    put("longBreakLength", longBreakLength)
    put("longBreakInterval", longBreakInterval)
    put("autoStartBreaks", autoStartBreaks)
    put("autoStartPomodoros", autoStartPomodoros)
    put("isStrictModeEnabled", isStrictModeEnabled)
    put("blockNotifications", blockNotifications)
    put("allowEmergencyOverride", allowEmergencyOverride)
    put("emergencyOverrideLimit", emergencyOverrideLimit)
    put("language", language)
    put("timeFormat", timeFormat.name)
    put("dateFormat", dateFormat.name)
    put("showScreenTime", showScreenTime)
    put("showFocusScore", showFocusScore)
    put("showProductivityTips", showProductivityTips)
    put("showWeeklyReport", showWeeklyReport)
    put("defaultTaskPriority", defaultTaskPriority.name)
    put("showCompletedTasks", showCompletedTasks)
    put("sortTasksBy", sortTasksBy.name)
    put("autoBackupEnabled", autoBackupEnabled)
    put("backupFrequency", backupFrequency.name)
    put("lastBackupTime", lastBackupTime)
    put("firstLaunch", firstLaunch)
    put("lastUpdateVersion", lastUpdateVersion)
    put("updatedAt", updatedAt)
}

fun UserPreferencesEntity.Companion.fromJson(json: JSONObject): UserPreferencesEntity = UserPreferencesEntity(
    id = json.getInt("id"),
    isDarkMode = json.getBoolean("isDarkMode"),
    accentColor = json.getString("accentColor"),
    isNotificationsEnabled = json.getBoolean("isNotificationsEnabled"),
    notificationSound = json.getBoolean("notificationSound"),
    notificationVibration = json.getBoolean("notificationVibration"),
    quietHoursEnabled = json.getBoolean("quietHoursEnabled"),
    quietHoursStart = json.getInt("quietHoursStart"),
    quietHoursEnd = json.getInt("quietHoursEnd"),
    pomodoroLength = json.getInt("pomodoroLength"),
    shortBreakLength = json.getInt("shortBreakLength"),
    longBreakLength = json.getInt("longBreakLength"),
    longBreakInterval = json.getInt("longBreakInterval"),
    autoStartBreaks = json.getBoolean("autoStartBreaks"),
    autoStartPomodoros = json.getBoolean("autoStartPomodoros"),
    isStrictModeEnabled = json.getBoolean("isStrictModeEnabled"),
    blockNotifications = json.getBoolean("blockNotifications"),
    allowEmergencyOverride = json.getBoolean("allowEmergencyOverride"),
    emergencyOverrideLimit = json.getInt("emergencyOverrideLimit"),
    language = json.getString("language"),
    timeFormat = TimeFormat.valueOf(json.getString("timeFormat")),
    dateFormat = DateFormat.valueOf(json.getString("dateFormat")),
    showScreenTime = json.getBoolean("showScreenTime"),
    showFocusScore = json.getBoolean("showFocusScore"),
    showProductivityTips = json.getBoolean("showProductivityTips"),
    showWeeklyReport = json.getBoolean("showWeeklyReport"),
    defaultTaskPriority = TaskPriority.valueOf(json.getString("defaultTaskPriority")),
    showCompletedTasks = json.getBoolean("showCompletedTasks"),
    sortTasksBy = TaskSortOption.valueOf(json.getString("sortTasksBy")),
    autoBackupEnabled = json.getBoolean("autoBackupEnabled"),
    backupFrequency = BackupFrequency.valueOf(json.getString("backupFrequency")),
    lastBackupTime = if (json.has("lastBackupTime")) json.getLong("lastBackupTime") else null,
    firstLaunch = json.getBoolean("firstLaunch"),
    lastUpdateVersion = if (json.has("lastUpdateVersion")) json.getString("lastUpdateVersion") else null,
    updatedAt = json.getLong("updatedAt")
)
