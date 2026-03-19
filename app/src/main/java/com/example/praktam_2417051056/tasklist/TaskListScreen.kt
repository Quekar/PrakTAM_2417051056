package com.example.praktam_2417051056.ui.tasklist

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.example.praktam_2417051056.model.Task
import com.example.praktam_2417051056.model.TaskDummy

enum class TaskFilter { SEMUA, AKTIF, SELESAI }

enum class TaskSort(val label: String) {
    PRIORITAS("Prioritas"),
    DEADLINE("Deadline"),
    DIBUAT("Dibuat")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    modifier: Modifier = Modifier,
    onTaskClick: (Task) -> Unit = {},
    onAddTask: () -> Unit = {}
) {
    var taskList by remember { mutableStateOf(TaskDummy.taskList) }
    var activeFilter by remember { mutableStateOf(TaskFilter.SEMUA) }
    var activeSort by remember { mutableStateOf(TaskSort.PRIORITAS) }
    var showSortMenu by remember { mutableStateOf(false) }

    val displayedTasks by remember(taskList, activeFilter, activeSort) {
        derivedStateOf {
            val filtered = when (activeFilter) {
                TaskFilter.SEMUA   -> taskList
                TaskFilter.AKTIF   -> taskList.filter { !it.isDone }
                TaskFilter.SELESAI -> taskList.filter { it.isDone }
            }
            when (activeSort) {
                TaskSort.PRIORITAS -> filtered.sortedBy { it.priority }
                TaskSort.DEADLINE  -> filtered.sortedBy { it.deadline }
                TaskSort.DIBUAT    -> filtered.sortedBy { it.createdAt }
            }
        }
    }

    val totalTask   = taskList.size
    val taskSelesai = taskList.count { it.isDone }
    val taskAktif   = taskList.count { !it.isDone }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTask,
                containerColor = Color(0xFF4F46E5),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Task", modifier = Modifier.size(28.dp))
            }
        },
        containerColor = Color(0xFFF1F5F9)
    ) { innerPadding ->

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF4F46E5), Color(0xFF6D28D9))
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(13.dp))
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Task Icon",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Task List",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.3.sp
                            )
                            Text(
                                text = "Kelola tugas harianmu",
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 12.sp
                            )
                        }

                        Box {
                            IconButton(
                                onClick = { showSortMenu = true },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.List,
                                    contentDescription = "Sort",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            DropdownMenu(
                                expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false }
                            ) {
                                Text(
                                    text = "Urutkan berdasarkan",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF94A3B8),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                                TaskSort.values().forEach { sort ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                if (activeSort == sort) {
                                                    Icon(
                                                        Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = Color(0xFF4F46E5),
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                }
                                                Text(
                                                    text = sort.label,
                                                    fontWeight = if (activeSort == sort) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (activeSort == sort) Color(0xFF4F46E5) else Color(0xFF1E293B)
                                                )
                                            }
                                        },
                                        onClick = {
                                            activeSort = sort
                                            showSortMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TaskStatChip(label = "Total",   value = "$totalTask",   modifier = Modifier.weight(1f))
                        TaskStatChip(label = "Aktif",   value = "$taskAktif",   modifier = Modifier.weight(1f))
                        TaskStatChip(label = "Selesai", value = "$taskSelesai", modifier = Modifier.weight(1f))
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskFilter.values().forEach { filter ->
                    val isActive = activeFilter == filter
                    FilterChip(
                        selected = isActive,
                        onClick = { activeFilter = filter },
                        label = {
                            Text(
                                text = when (filter) {
                                    TaskFilter.SEMUA   -> "Semua"
                                    TaskFilter.AKTIF   -> "Aktif"
                                    TaskFilter.SELESAI -> "Selesai"
                                },
                                fontSize = 13.sp,
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF4F46E5),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFFF1F5F9),
                            labelColor = Color(0xFF64748B)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isActive,
                            selectedBorderColor = Color.Transparent,
                            borderColor = Color(0xFFE2E8F0)
                        )
                    )
                }
                Spacer(modifier = Modifier.weight(1f))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFF1F5F9)
                ) {
                    Text(
                        text = "↕ ${activeSort.label}",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Divider(color = Color(0xFFE2E8F0), thickness = 1.dp)
            if (displayedTasks.isEmpty()) {
                TaskEmptyState(
                    filter = activeFilter,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(displayedTasks, key = { it.id }) { task ->
                        TaskCard(
                            task = task,
                            onClick = { onTaskClick(task) },
                            onToggleDone = { toggled ->
                                // ← STATE UPDATE: toggle isDone pada task
                                taskList = taskList.map {
                                    if (it.id == toggled.id) it.copy(isDone = !it.isDone) else it
                                }
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
fun TaskCard(
    task: Task,
    onClick: () -> Unit,
    onToggleDone: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    val priorityColor = when (task.priority) {
        1    -> Color(0xFFEF4444)
        2    -> Color(0xFFF59E0B)
        else -> Color(0xFF22C55E)
    }
    val priorityLabel = when (task.priority) {
        1    -> "Tinggi"
        2    -> "Sedang"
        else -> "Rendah"
    }
    val categoryEmoji = when (task.category) {
        "Kuliah"  -> "📚"
        "Pribadi" -> "🏃"
        "Kerja"   -> "💼"
        else      -> "📌"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (task.isDone) 0.dp else 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isDone) Color(0xFFF8FAFC) else Color.White
        )
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(
                        color = if (task.isDone) Color(0xFFCBD5E1) else priorityColor,
                        shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                    )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = categoryEmoji, fontSize = 13.sp)
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFFF1F5F9)
                        ) {
                            Text(
                                text = task.category,
                                fontSize = 11.sp,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = priorityColor.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = priorityLabel,
                                fontSize = 11.sp,
                                color = priorityColor,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Text(
                        text = formatDeadlineShort(task.deadline),
                        fontSize = 11.sp,
                        color = if (isOverdue(task)) Color(0xFFEF4444) else Color(0xFF94A3B8),
                        fontWeight = if (isOverdue(task)) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 2.dp, end = 10.dp)
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(
                                if (task.isDone) Color(0xFF4F46E5) else Color(0xFFF1F5F9)
                            )
                            .border(
                                width = 1.5.dp,
                                color = if (task.isDone) Color(0xFF4F46E5) else Color(0xFFCBD5E1),
                                shape = CircleShape
                            )
                            .clickable { onToggleDone(task) },  // ← STATE UPDATE trigger
                        contentAlignment = Alignment.Center
                    ) {
                        if (task.isDone) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selesai",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = task.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (task.isDone) Color(0xFF94A3B8) else Color(0xFF1E293B),
                            textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (task.description.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = task.description,
                                fontSize = 12.sp,
                                color = Color(0xFFCBD5E1),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isOverdue(task) && !task.isDone) "⚠️" else "🕐",
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Deadline: ${task.deadline}",
                            fontSize = 11.sp,
                            color = if (isOverdue(task) && !task.isDone) Color(0xFFEF4444) else Color(0xFF94A3B8)
                        )
                    }
                    if (task.isDone) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF22C55E).copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "✅ Selesai",
                                fontSize = 11.sp,
                                color = Color(0xFF22C55E),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskEmptyState(filter: TaskFilter, modifier: Modifier = Modifier) {
    val (emoji, title, subtitle) = when (filter) {
        TaskFilter.SEMUA   -> Triple("📋", "Belum ada task", "Tambahkan tugas pertama kamu!")
        TaskFilter.AKTIF   -> Triple("🎉", "Semua selesai!", "Kamu sudah menyelesaikan semua tugas.")
        TaskFilter.SELESAI -> Triple("⏳", "Belum ada task selesai", "Selesaikan tugas aktif kamu dulu.")
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = emoji, fontSize = 64.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF94A3B8)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            fontSize = 13.sp,
            color = Color(0xFFCBD5E1),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TaskStatChip(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.18f))
            .padding(vertical = 10.dp)
    ) {
        Text(
            text = value,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 11.sp
        )
    }
}

fun formatDeadlineShort(deadline: String): String {
    val parts = deadline.split(" ")
    if (parts.isEmpty()) return deadline
    val dateParts = parts[0].split("-")
    if (dateParts.size != 3) return deadline
    val month = when (dateParts[1]) {
        "01" -> "Jan"; "02" -> "Feb"; "03" -> "Mar"; "04" -> "Apr"
        "05" -> "Mei"; "06" -> "Jun"; "07" -> "Jul"; "08" -> "Agu"
        "09" -> "Sep"; "10" -> "Okt"; "11" -> "Nov"; "12" -> "Des"
        else -> dateParts[1]
    }
    val time = if (parts.size > 1) " ${parts[1]}" else ""
    return "${dateParts[2].toIntOrNull() ?: dateParts[2]} $month$time"
}

fun isOverdue(task: Task): Boolean {
    if (task.isDone) return false
    return task.deadline < "2026-03-17 23:59"
}