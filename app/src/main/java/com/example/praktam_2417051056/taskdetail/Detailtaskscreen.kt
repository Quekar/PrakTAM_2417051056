package com.example.praktam_2417051056.taskdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.praktam_2417051056.getCategoryIcon
import com.example.praktam_2417051056.model.Task

@Composable
fun DetailTaskScreen(
    task: Task,
    onBack: () -> Unit,
    onDelete: (Task) -> Unit = {},
    onEdit: (Task) -> Unit = {},
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
    val priorityEmoji = when (task.priority) {
        1    -> "🔴"
        2    -> "🟡"
        else -> "🟢"
    }
    val categoryIcon = getCategoryIcon(task.category)

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            shape            = RoundedCornerShape(20.dp),
            containerColor   = Color.White,
            title = {
                Text(
                    text       = "Hapus Task?",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    color      = Color(0xFF1E293B)
                )
            },
            text = {
                Text(
                    text      = "\"${task.title}\" akan dihapus secara permanen.",
                    fontSize  = 14.sp,
                    color     = Color(0xFF64748B),
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDelete(task)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape  = RoundedCornerShape(10.dp)
                ) {
                    Text("Hapus", fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal", color = Color(0xFF64748B))
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF4F46E5), Color(0xFF6D28D9))
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier          = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick  = onBack,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector        = Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint               = Color.White,
                            modifier           = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text       = "Detail Task",
                            color      = Color.White,
                            fontSize   = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text     = task.category,
                            color    = Color.White.copy(alpha = 0.75f),
                            fontSize = 12.sp
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (task.isDone)
                            Color(0xFF22C55E).copy(alpha = 0.25f)
                        else
                            Color.White.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier              = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector        = if (task.isDone) Icons.Default.CheckCircle
                                else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint               = Color.White,
                                modifier           = Modifier.size(14.dp)
                            )
                            Text(
                                text       = if (task.isDone) "Selesai" else "Aktif",
                                color      = Color.White,
                                fontSize   = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text           = task.title,
                    color          = Color.White,
                    fontSize       = 22.sp,
                    fontWeight     = FontWeight.ExtraBold,
                    lineHeight     = 30.sp,
                    textDecoration = if (task.isDone) TextDecoration.LineThrough
                    else TextDecoration.None
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = priorityColor.copy(alpha = 0.25f)
                    ) {
                        Text(
                            text       = "$priorityEmoji  $priorityLabel",
                            color      = Color.White,
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier   = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier              = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector        = categoryIcon,
                                contentDescription = task.category,
                                tint               = Color.White,
                                modifier           = Modifier.size(13.dp)
                            )
                            Text(
                                text       = task.category,
                                color      = Color.White,
                                fontSize   = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Card(
                shape  = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    TaskDetailInfoRow(
                        icon  = Icons.Default.Schedule,
                        label = "Deadline",
                        value = if (task.deadline == "-") "Tidak ada deadline"
                        else task.deadline,
                        valueColor = deadlineColor(task)
                    )
                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFE2E8F0))

                    TaskDetailInfoRow(
                        icon  = Icons.Default.Flag,
                        label = "Prioritas",
                        value = priorityLabel,
                        valueColor = priorityColor
                    )
                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFE2E8F0))

                    TaskDetailInfoRow(
                        icon  = categoryIcon,
                        label = "Kategori",
                        value = task.category
                    )
                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFE2E8F0))

                    TaskDetailInfoRow(
                        icon  = Icons.Default.AccessTime,
                        label = "Dibuat",
                        value = task.createdAt
                    )
                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFE2E8F0))

                    TaskDetailInfoRow(
                        icon  = if (task.isDone) Icons.Default.CheckCircle
                        else Icons.Default.RadioButtonUnchecked,
                        label = "Status",
                        value = if (task.isDone) "Selesai" else "Belum selesai",
                        valueColor = if (task.isDone) Color(0xFF22C55E) else Color(0xFF94A3B8)
                    )
                }
            }

            if (task.description.isNotEmpty()) {
                Card(
                    shape  = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text       = "Deskripsi",
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text       = task.description,
                            fontSize   = 14.sp,
                            color      = Color(0xFF475569),
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            if (!task.isDone && task.deadline != "-" && isTaskOverdue(task)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFEE2E2))
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector        = Icons.Default.Warning,
                            contentDescription = null,
                            tint               = Color(0xFFEF4444),
                            modifier           = Modifier.size(20.dp)
                        )
                        Text(
                            text       = "Task ini sudah melewati deadline!",
                            fontSize   = 13.sp,
                            color      = Color(0xFFEF4444),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Surface(
            modifier       = Modifier.fillMaxWidth(),
            color          = Color.White,
            tonalElevation = 4.dp
        ) {
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick  = { onEdit(task) },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape  = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF4F46E5)
                    )
                ) {
                    Icon(
                        imageVector        = Icons.Default.Edit,
                        contentDescription = "Edit",
                        modifier           = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Edit", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
                Button(
                    onClick  = { showDeleteDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape  = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Icon(
                        imageVector        = Icons.Default.Delete,
                        contentDescription = "Hapus",
                        tint               = Color.White,
                        modifier           = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text       = "Hapus",
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 14.sp,
                        color      = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskDetailInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = Color(0xFF1E293B)
) {
    Row(
        modifier          = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier         = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF1F5F9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = label,
                tint               = Color(0xFF4F46E5),
                modifier           = Modifier.size(17.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = label,
                fontSize   = 11.sp,
                color      = Color(0xFF94A3B8),
                fontWeight = FontWeight.Medium
            )
            Text(
                text       = value,
                fontSize   = 14.sp,
                color      = valueColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private fun isTaskOverdue(task: Task): Boolean {
    if (task.isDone || task.deadline == "-") return false
    return try {
        val now = java.time.LocalDateTime.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        task.deadline < now
    } catch (e: Exception) { false }
}

private fun deadlineColor(task: Task): Color = when {
    task.deadline == "-"    -> Color(0xFF94A3B8)
    task.isDone             -> Color(0xFF22C55E)
    isTaskOverdue(task)     -> Color(0xFFEF4444)
    else                    -> Color(0xFF1E293B)
}