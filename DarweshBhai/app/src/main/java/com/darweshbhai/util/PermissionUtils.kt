package com.darweshbhai.util

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.provider.Settings
import androidx.core.content.ContextCompat

object PermissionUtils {
    
    // Permission groups
    private val USAGE_STATS_PERMISSIONS = arrayOf(
        Manifest.permission.PACKAGE_USAGE_STATS
    )

    private val NOTIFICATION_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        emptyArray()
    }

    private val BACKGROUND_PERMISSIONS = arrayOf(
        Manifest.permission.FOREGROUND_SERVICE,
        Manifest.permission.RECEIVE_BOOT_COMPLETED
    )

    // Permission checks
    fun hasUsageStatsPermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        } else {
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun hasBackgroundPermissions(context: Context): Boolean {
        return BACKGROUND_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun hasAllRequiredPermissions(context: Context): Boolean {
        return hasUsageStatsPermission(context) &&
                hasNotificationPermission(context) &&
                hasBackgroundPermissions(context)
    }

    // Permission request intents
    fun getUsageStatsSettingsIntent(): Intent {
        return Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
    }

    fun getNotificationSettingsIntent(): Intent {
        return Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, "com.darweshbhai")
        }
    }

    fun getBackgroundSettingsIntent(): Intent {
        return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = android.net.Uri.parse("package:com.darweshbhai")
        }
    }

    // Permission descriptions
    fun getPermissionDescription(permissionType: PermissionType): String {
        return when (permissionType) {
            PermissionType.USAGE_STATS -> """
                This permission is required to monitor app usage and help you maintain digital wellbeing.
                We use this data to:
                • Track app usage time
                • Enforce app limits
                • Generate usage reports
                • Provide insights for better productivity
            """.trimIndent()

            PermissionType.NOTIFICATIONS -> """
                This permission allows us to:
                • Send task reminders
                • Alert you about app usage limits
                • Provide focus session updates
                • Show important productivity insights
            """.trimIndent()

            PermissionType.BACKGROUND -> """
                This permission enables:
                • Automatic tracking of app usage
                • Focus session monitoring
                • Scheduled reminders
                • App blocking during focus time
            """.trimIndent()
        }
    }

    // Permission types
    enum class PermissionType {
        USAGE_STATS,
        NOTIFICATIONS,
        BACKGROUND
    }

    // Permission status
    sealed class PermissionStatus {
        object Granted : PermissionStatus()
        object Denied : PermissionStatus()
        object ShowRationale : PermissionStatus()
        object PermanentlyDenied : PermissionStatus()
    }

    fun getPermissionStatus(
        context: Context,
        permissionType: PermissionType
    ): PermissionStatus {
        return when (permissionType) {
            PermissionType.USAGE_STATS -> {
                if (hasUsageStatsPermission(context)) {
                    PermissionStatus.Granted
                } else {
                    PermissionStatus.Denied
                }
            }
            PermissionType.NOTIFICATIONS -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    when {
                        hasNotificationPermission(context) -> PermissionStatus.Granted
                        shouldShowRequestPermissionRationale(context) -> PermissionStatus.ShowRationale
                        else -> PermissionStatus.PermanentlyDenied
                    }
                } else {
                    PermissionStatus.Granted
                }
            }
            PermissionType.BACKGROUND -> {
                if (hasBackgroundPermissions(context)) {
                    PermissionStatus.Granted
                } else {
                    PermissionStatus.Denied
                }
            }
        }
    }

    private fun shouldShowRequestPermissionRationale(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            false
        }
    }
}
