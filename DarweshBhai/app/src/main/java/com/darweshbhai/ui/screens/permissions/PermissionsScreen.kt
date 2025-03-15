package com.darweshbhai.ui.screens.permissions

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Process
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.darweshbhai.ui.navigation.Destination
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

data class Permission(
    val title: String,
    val description: String,
    val icon: @Composable () -> Unit,
    val isGranted: (Context) -> Boolean,
    val request: (Context) -> Unit
)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsScreen(navController: NavController) {
    val context = LocalContext.current
    var allPermissionsGranted by remember { mutableStateOf(false) }

    val permissions = remember {
        listOf(
            Permission(
                title = "Usage Access",
                description = "Required to monitor app usage and help you maintain digital well-being",
                icon = { Icon(Icons.Default.Timeline, "Usage Access") },
                isGranted = { ctx ->
                    val appOps = ctx.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                    appOps.checkOpNoThrow(
                        AppOpsManager.OPSTR_GET_USAGE_STATS,
                        Process.myUid(),
                        ctx.packageName
                    ) == AppOpsManager.MODE_ALLOWED
                },
                request = { ctx ->
                    ctx.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                }
            ),
            Permission(
                title = "Overlay Permission",
                description = "Needed to display important notifications and reminders",
                icon = { Icon(Icons.Default.Layers, "Overlay Permission") },
                isGranted = { ctx ->
                    Settings.canDrawOverlays(ctx)
                },
                request = { ctx ->
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${ctx.packageName}")
                    )
                    ctx.startActivity(intent)
                }
            ),
            Permission(
                title = "Accessibility Service",
                description = "Required to help manage app usage and implement focus mode",
                icon = { Icon(Icons.Default.AccessibilityNew, "Accessibility Service") },
                isGranted = { ctx ->
                    Settings.Secure.getString(
                        ctx.contentResolver,
                        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                    )?.contains(ctx.packageName) == true
                },
                request = { ctx ->
                    ctx.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }
            )
        )
    }

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.SCHEDULE_EXACT_ALARM,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(Unit) {
        permissionsState.launchMultiplePermissionRequest()
    }

    LaunchedEffect(permissionsState.allPermissionsGranted) {
        val specialPermissionsGranted = permissions.all { it.isGranted(context) }
        allPermissionsGranted = permissionsState.allPermissionsGranted && specialPermissionsGranted
        
        if (allPermissionsGranted) {
            navController.navigate(Destination.Dashboard.route) {
                popUpTo(Destination.Permissions.route) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Required Permissions",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        Text(
            text = "Darwesh Bhai needs the following permissions to help you maintain digital well-being effectively:",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(permissions) { permission ->
                PermissionItem(
                    permission = permission,
                    isGranted = permission.isGranted(context),
                    onRequest = { permission.request(context) }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                navController.navigate(Destination.Dashboard.route) {
                    popUpTo(Destination.Permissions.route) { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            enabled = allPermissionsGranted
        ) {
            Text("Continue")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PermissionItem(
    permission: Permission,
    isGranted: Boolean,
    onRequest: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                permission.icon()
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = permission.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = permission.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            if (isGranted) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Granted",
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                TextButton(onClick = onRequest) {
                    Text("Grant")
                }
            }
        }
    }
}
