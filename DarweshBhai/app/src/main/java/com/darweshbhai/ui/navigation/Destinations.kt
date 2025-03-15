package com.darweshbhai.ui.navigation

sealed class Destination(val route: String) {
    // Main screens
    object Splash : Destination("splash")
    object Permissions : Destination("permissions")
    object Home : Destination("home")
    
    // Feature screens
    object UsageMonitor : Destination("usage_monitor")
    object AppManagement : Destination("app_management")
    object Pomodoro : Destination("pomodoro")
    object TodoList : Destination("todo_list")
    object AlarmReminder : Destination("alarm_reminder")
    object Insights : Destination("insights")
    object Settings : Destination("settings")

    // Bottom navigation screens
    object Dashboard : Destination("dashboard")
    object Focus : Destination("focus")
    object Tasks : Destination("tasks")
    object Profile : Destination("profile")

    // Nested routes
    fun createRoute(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}

// Bottom navigation items
val bottomNavItems = listOf(
    Destination.Dashboard,
    Destination.Focus,
    Destination.Tasks,
    Destination.Profile
)
