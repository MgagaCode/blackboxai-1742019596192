package com.darweshbhai.ui.screens.tasks

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.util.*

enum class TaskPriority(val color: Color, val icon: @Composable () -> Unit) {
    HIGH(Color.Red, { Icon(Icons.Default.PriorityHigh, "High Priority") }),
    MEDIUM(Color(0xFFFFA000), { Icon(Icons.Default.Warning, "Medium Priority") }),
    LOW(Color(0xFF4CAF50), { Icon(Icons.Default.Info, "Low Priority") })
}

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val priority: TaskPriority,
    val dueDate: Long? = null,
    var isCompleted: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TasksScreen(navController: NavController) {
    var tasks by remember { mutableStateOf(listOf<Task>()) }
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var selectedPriority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    var taskTitle by remember { mutableStateOf("") }
    var taskDescription by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTaskDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, "Add Task")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header
            TopAppBar(
                title = {
                    Text(
                        text = "Tasks",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                actions = {
                    // Filter button (to be implemented)
                    IconButton(onClick = { /* TODO: Implement filtering */ }) {
                        Icon(Icons.Default.FilterList, "Filter Tasks")
                    }
                    // Sort button (to be implemented)
                    IconButton(onClick = { /* TODO: Implement sorting */ }) {
                        Icon(Icons.Default.Sort, "Sort Tasks")
                    }
                }
            )

            if (tasks.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "No Tasks",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No tasks yet",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Add your first task by tapping the + button",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            } else {
                // Tasks list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = tasks,
                        key = { it.id }
                    ) { task ->
                        TaskItem(
                            task = task,
                            onTaskChecked = { checked ->
                                tasks = tasks.map {
                                    if (it.id == task.id) it.copy(isCompleted = checked)
                                    else it
                                }
                            },
                            onDeleteTask = {
                                tasks = tasks.filter { it.id != task.id }
                            },
                            modifier = Modifier.animateItemPlacement()
                        )
                    }
                }
            }
        }

        // Add Task Dialog
        if (showAddTaskDialog) {
            AlertDialog(
                onDismissRequest = { showAddTaskDialog = false },
                title = { Text("Add New Task") },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = taskTitle,
                            onValueChange = { taskTitle = it },
                            label = { Text("Task Title") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        OutlinedTextField(
                            value = taskDescription,
                            onValueChange = { taskDescription = it },
                            label = { Text("Description (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )

                        // Priority selection
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TaskPriority.values().forEach { priority ->
                                FilterChip(
                                    selected = selectedPriority == priority,
                                    onClick = { selectedPriority = priority },
                                    label = { Text(priority.name) },
                                    leadingIcon = priority.icon
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (taskTitle.isNotBlank()) {
                                tasks = tasks + Task(
                                    title = taskTitle,
                                    description = taskDescription,
                                    priority = selectedPriority
                                )
                                taskTitle = ""
                                taskDescription = ""
                                selectedPriority = TaskPriority.MEDIUM
                                showAddTaskDialog = false
                            }
                        }
                    ) {
                        Text("Add Task")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddTaskDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskItem(
    task: Task,
    onTaskChecked: (Boolean) -> Unit,
    onDeleteTask: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = if (task.isCompleted) 0.5f else 1f
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = onTaskChecked
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (task.isCompleted) 
                        TextDecoration.LineThrough 
                    else 
                        TextDecoration.None
                )
                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textDecoration = if (task.isCompleted) 
                            TextDecoration.LineThrough 
                        else 
                            TextDecoration.None
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                modifier = Modifier.size(16.dp),
                contentDescription = "Priority",
                tint = task.priority.color,
                imageVector = when (task.priority) {
                    TaskPriority.HIGH -> Icons.Default.PriorityHigh
                    TaskPriority.MEDIUM -> Icons.Default.Warning
                    TaskPriority.LOW -> Icons.Default.Info
                }
            )

            IconButton(onClick = onDeleteTask) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete Task",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
