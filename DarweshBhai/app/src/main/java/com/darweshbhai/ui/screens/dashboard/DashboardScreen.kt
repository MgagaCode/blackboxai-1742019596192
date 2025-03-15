package com.darweshbhai.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.darweshbhai.ui.navigation.Destination
import java.time.LocalTime
import kotlin.random.Random

data class DashboardItem(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val backgroundColor: androidx.compose.ui.graphics.Color,
    val value: String = "",
    val subtitle: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    val greeting = remember {
        when (LocalTime.now().hour) {
            in 0..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }

    val dashboardItems = remember {
        listOf(
            DashboardItem(
                title = "Screen Time",
                icon = Icons.Default.PhoneAndroid,
                route = Destination.UsageMonitor.route,
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                value = "${Random.nextInt(1, 6)}h ${Random.nextInt(0, 60)}m",
                subtitle = "Today"
            ),
            DashboardItem(
                title = "Focus Mode",
                icon = Icons.Default.Timer,
                route = Destination.Focus.route,
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                value = "Start Session",
                subtitle = "Pomodoro Timer"
            ),
            DashboardItem(
                title = "App Limits",
                icon = Icons.Default.Block,
                route = Destination.AppManagement.route,
                backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                value = "${Random.nextInt(2, 8)} apps",
                subtitle = "Currently Limited"
            ),
            DashboardItem(
                title = "Tasks",
                icon = Icons.Default.TaskAlt,
                route = Destination.Tasks.route,
                backgroundColor = MaterialTheme.colorScheme.errorContainer,
                value = "${Random.nextInt(0, 5)} pending",
                subtitle = "Today's Tasks"
            ),
            DashboardItem(
                title = "Insights",
                icon = Icons.Default.InsertChart,
                route = Destination.Insights.route,
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                value = "View Report",
                subtitle = "Weekly Analysis"
            ),
            DashboardItem(
                title = "Reminders",
                icon = Icons.Default.Notifications,
                route = Destination.AlarmReminder.route,
                backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                value = "${Random.nextInt(1, 4)} active",
                subtitle = "Upcoming"
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Greeting Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Let's maintain your digital well-being",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Dashboard Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(dashboardItems) { item ->
                DashboardCard(
                    item = item,
                    onClick = { navController.navigate(item.route) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardCard(
    item: DashboardItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        colors = CardDefaults.cardColors(
            containerColor = item.backgroundColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(32.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                if (item.value.isNotEmpty()) {
                    Text(
                        text = item.value,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                if (item.subtitle.isNotEmpty()) {
                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
