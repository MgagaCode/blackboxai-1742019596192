package com.darweshbhai.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.darweshbhai.ui.navigation.Destination
import com.darweshbhai.ui.navigation.bottomNavItems
import com.darweshbhai.ui.screens.splash.SplashScreen
import com.darweshbhai.ui.screens.permissions.PermissionsScreen
import com.darweshbhai.ui.screens.dashboard.DashboardScreen
import com.darweshbhai.ui.screens.focus.FocusScreen
import com.darweshbhai.ui.screens.tasks.TasksScreen
import com.darweshbhai.ui.screens.profile.ProfileScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DarweshBhaiApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // State for showing bottom bar
    val shouldShowBottomBar = remember(currentDestination) {
        currentDestination?.route in bottomNavItems.map { it.route }
    }

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = when (screen) {
                                        Destination.Dashboard -> Icons.Default.Dashboard
                                        Destination.Focus -> Icons.Default.Timer
                                        Destination.Tasks -> Icons.Default.TaskAlt
                                        Destination.Profile -> Icons.Default.Person
                                        else -> Icons.Default.Dashboard
                                    },
                                    contentDescription = screen.route
                                )
                            },
                            label = { Text(screen.route.capitalize()) },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NavHost(
                navController = navController,
                startDestination = Destination.Splash.route
            ) {
                // Initial screens
                composable(Destination.Splash.route) {
                    SplashScreen(navController)
                }
                composable(Destination.Permissions.route) {
                    PermissionsScreen(navController)
                }

                // Bottom navigation screens
                composable(Destination.Dashboard.route) {
                    DashboardScreen(navController)
                }
                composable(Destination.Focus.route) {
                    FocusScreen(navController)
                }
                composable(Destination.Tasks.route) {
                    TasksScreen(navController)
                }
                composable(Destination.Profile.route) {
                    ProfileScreen(navController)
                }

                // Feature screens will be added here as we implement them
            }
        }
    }
}

private fun String.capitalize(): String {
    return replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}
