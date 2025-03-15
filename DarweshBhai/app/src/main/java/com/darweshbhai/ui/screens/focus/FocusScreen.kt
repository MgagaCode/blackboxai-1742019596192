package com.darweshbhai.ui.screens.focus

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun FocusScreen(navController: NavController) {
    var isTimerRunning by remember { mutableStateOf(false) }
    var remainingTimeInSeconds by remember { mutableStateOf(25 * 60) } // 25 minutes
    var selectedMode by remember { mutableStateOf(FocusMode.POMODORO) }
    
    val progress by remember(remainingTimeInSeconds) {
        derivedStateOf {
            when (selectedMode) {
                FocusMode.POMODORO -> remainingTimeInSeconds / (25f * 60)
                FocusMode.SHORT_BREAK -> remainingTimeInSeconds / (5f * 60)
                FocusMode.LONG_BREAK -> remainingTimeInSeconds / (15f * 60)
            }
        }
    }

    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning && remainingTimeInSeconds > 0) {
            delay(1000L)
            remainingTimeInSeconds--
        }
        if (remainingTimeInSeconds == 0) {
            isTimerRunning = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Focus Mode Selector
        FocusModeSelector(
            selectedMode = selectedMode,
            onModeSelected = { mode ->
                selectedMode = mode
                remainingTimeInSeconds = when (mode) {
                    FocusMode.POMODORO -> 25 * 60
                    FocusMode.SHORT_BREAK -> 5 * 60
                    FocusMode.LONG_BREAK -> 15 * 60
                }
                isTimerRunning = false
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Timer Display
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(300.dp)
                .padding(16.dp)
        ) {
            TimerCircle(
                progress = progress,
                color = when (selectedMode) {
                    FocusMode.POMODORO -> MaterialTheme.colorScheme.primary
                    FocusMode.SHORT_BREAK -> MaterialTheme.colorScheme.secondary
                    FocusMode.LONG_BREAK -> MaterialTheme.colorScheme.tertiary
                }
            )
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = formatTime(remainingTimeInSeconds),
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = selectedMode.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Control Buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { isTimerRunning = !isTimerRunning },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isTimerRunning) 
                        MaterialTheme.colorScheme.error 
                    else 
                        MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isTimerRunning) "Pause" else "Start"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isTimerRunning) "Pause" else "Start")
            }

            Button(
                onClick = {
                    remainingTimeInSeconds = when (selectedMode) {
                        FocusMode.POMODORO -> 25 * 60
                        FocusMode.SHORT_BREAK -> 5 * 60
                        FocusMode.LONG_BREAK -> 15 * 60
                    }
                    isTimerRunning = false
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Focus Tips
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Focus Tips",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = when (selectedMode) {
                        FocusMode.POMODORO -> "Stay focused for 25 minutes, then take a break"
                        FocusMode.SHORT_BREAK -> "Take a quick 5-minute break to refresh"
                        FocusMode.LONG_BREAK -> "Enjoy a longer 15-minute break after 4 pomodoros"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun FocusModeSelector(
    selectedMode: FocusMode,
    onModeSelected: (FocusMode) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        FocusMode.values().forEach { mode ->
            FilterChip(
                selected = selectedMode == mode,
                onClick = { onModeSelected(mode) },
                label = { Text(mode.title) },
                leadingIcon = {
                    Icon(
                        imageVector = when (mode) {
                            FocusMode.POMODORO -> Icons.Default.Timer
                            FocusMode.SHORT_BREAK -> Icons.Default.Coffee
                            FocusMode.LONG_BREAK -> Icons.Default.Weekend
                        },
                        contentDescription = mode.title
                    )
                }
            )
        }
    }
}

@Composable
private fun TimerCircle(
    progress: Float,
    color: Color
) {
    Canvas(
        modifier = Modifier
            .size(300.dp)
            .padding(16.dp)
    ) {
        // Background circle
        drawArc(
            color = color.copy(alpha = 0.2f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = 12f, cap = StrokeCap.Round),
            size = Size(size.width, size.height)
        )

        // Progress arc
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = 360f * progress,
            useCenter = false,
            style = Stroke(width = 12f, cap = StrokeCap.Round),
            size = Size(size.width, size.height)
        )
    }
}

private fun formatTime(timeInSeconds: Int): String {
    val minutes = timeInSeconds / 60
    val seconds = timeInSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

enum class FocusMode(val title: String) {
    POMODORO("Pomodoro"),
    SHORT_BREAK("Short Break"),
    LONG_BREAK("Long Break")
}
