package com.example.praktam_2417051056

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.example.praktam_2417051056.addevent.AddEventBottomSheet
import com.example.praktam_2417051056.addtask.AddTaskScreen
import com.example.praktam_2417051056.model.Event
import com.example.praktam_2417051056.model.EventDummy
import com.example.praktam_2417051056.model.Task
import com.example.praktam_2417051056.model.TaskDummy
import com.example.praktam_2417051056.schedule.ScheduleScreen
import com.example.praktam_2417051056.splash.SplashScreen
import com.example.praktam_2417051056.tasklist.TaskListScreen
import com.example.praktam_2417051056.ui.theme.PrakTAM_2417051056Theme

enum class Screen { SPLASH, MAIN }
enum class AppTab { SCHEDULE, TASKS }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrakTAM_2417051056Theme {
                DailyDoApp()
            }
        }
    }
}

@Composable
fun DailyDoApp() {
    var currentScreen by remember { mutableStateOf(Screen.SPLASH) }

    when (currentScreen) {
        Screen.SPLASH -> SplashScreen(
            onNavigateToMain = { currentScreen = Screen.MAIN }
        )
        Screen.MAIN -> AppRoot()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot() {
    var currentTab by remember { mutableStateOf(AppTab.SCHEDULE) }

    var eventList by remember { mutableStateOf(EventDummy.eventList) }
    var taskList  by remember { mutableStateOf(TaskDummy.taskList) }

    var selectedEvent by remember { mutableStateOf<Event?>(null) }

    var showAddTask by remember { mutableStateOf(false) }

    var showAddEvent by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snackbarHostState = remember { SnackbarHostState() }

    if (selectedEvent != null) {
        DetailScreen(
            event  = selectedEvent!!,
            onBack = { selectedEvent = null }
        )
        return
    }

    if (showAddTask) {
        AddTaskScreen(
            onBack = { showAddTask = false },
            onTaskSaved = { title, description, category, priority, deadline, createdAt ->
                val newId = (taskList.maxOfOrNull { it.id } ?: 0) + 1
                val newTask = Task(
                    id          = newId,
                    title       = title,
                    description = description,
                    category    = category,
                    priority    = priority,
                    deadline    = deadline,
                    isDone      = false,
                    createdAt   = createdAt
                )
                taskList = taskList + newTask
                showAddTask = false
            }
        )
        return
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData   = data,
                    containerColor = Color(0xFF1E293B),
                    contentColor   = Color.White,
                    actionColor    = Color(0xFF818CF8),
                    shape          = RoundedCornerShape(14.dp),
                    modifier       = Modifier.padding(16.dp)
                )
            }
        },
        bottomBar = {
            AppBottomNavBar(
                currentTab    = currentTab,
                onTabSelected = { currentTab = it }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentTab) {
                AppTab.SCHEDULE -> ScheduleScreen(
                    events       = eventList,
                    onEventClick = { event -> selectedEvent = event },
                    onAddEvent   = { showAddEvent = true }
                )
                AppTab.TASKS -> TaskListScreen(
                    tasks      = taskList,
                    onTasksChanged = { updatedList -> taskList = updatedList },
                    onAddTask  = { showAddTask = true }
                )
            }
        }
    }

    if (showAddEvent) {
        ModalBottomSheet(
            onDismissRequest  = { showAddEvent = false },
            sheetState        = bottomSheetState,
            containerColor    = Color.White,
            dragHandle        = null,
            shape             = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            AddEventBottomSheet(
                snackbarHostState = snackbarHostState,
                onDismiss         = { showAddEvent = false },
                onEventSaved      = { title, date, startTime, endTime, category, color, description ->
                    val durationMinutes = computeDurationMinutes(startTime, endTime)
                    val newId = (eventList.maxOfOrNull { it.id } ?: 0) + 1
                    val newEvent = Event(
                        id              = newId,
                        title           = title,
                        description     = description,
                        date            = date,
                        startTime       = startTime,
                        endTime         = endTime,
                        durationMinutes = durationMinutes,
                        category        = category,
                        color           = color
                    )
                    eventList     = eventList + newEvent
                    showAddEvent  = false
                }
            )
        }
    }
}

private fun computeDurationMinutes(start: String, end: String): Int {
    return try {
        val (sh, sm) = start.split(":").map { it.toInt() }
        val (eh, em) = end.split(":").map { it.toInt() }
        val diff = (eh * 60 + em) - (sh * 60 + sm)
        diff.coerceAtLeast(0)
    } catch (e: Exception) { 0 }
}

data class NavItem(
    val tab: AppTab,
    val label: String,
    val icon: ImageVector,
    val emoji: String
)

@Composable
fun AppBottomNavBar(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit
) {
    val navItems = listOf(
        NavItem(AppTab.SCHEDULE, "Schedule", Icons.Default.DateRange, "🗓️"),
        NavItem(AppTab.TASKS,    "Tasks",    Icons.Default.Done,      "✅")
    )

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color(0xFFE2E8F0),
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
            )
    ) {
        navItems.forEach { item ->
            val isSelected = currentTab == item.tab
            NavigationBarItem(
                selected = isSelected,
                onClick  = { onTabSelected(item.tab) },
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = item.emoji, fontSize = 20.sp)
                    }
                },
                label = {
                    Text(
                        text       = item.label,
                        fontSize   = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = Color(0xFF4F46E5),
                    selectedTextColor   = Color(0xFF4F46E5),
                    unselectedIconColor = Color(0xFF94A3B8),
                    unselectedTextColor = Color(0xFF94A3B8),
                    indicatorColor      = Color(0xFFF1F5F9)
                )
            )
        }
    }
}

@Composable
fun DetailScreen(
    event: Event,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val eventColor = parseColor(event.color)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(eventColor, eventColor.copy(alpha = 0.7f))
                    )
                )
        ) {
            Box(
                modifier         = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier         = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = getCategoryEmoji(event.category), fontSize = 38.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.25f)
                    ) {
                        Text(
                            text       = event.category,
                            color      = Color.White,
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier   = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                        )
                    }
                }
            }
            IconButton(
                onClick  = onBack,
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.TopStart)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
            ) {
                Icon(
                    imageVector        = Icons.Default.ArrowBack,
                    contentDescription = "Kembali",
                    tint               = Color.White
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(20.dp)
        ) {
            Text(
                text       = event.title,
                fontSize   = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = Color(0xFF1E293B),
                lineHeight = 28.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            DetailInfoRow(icon = "📅", label = "Tanggal", value = formatDate(event.date))
            Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFE2E8F0))
            DetailInfoRow(icon = "🕐", label = "Waktu",   value = "${event.startTime} – ${event.endTime}")
            Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFE2E8F0))
            DetailInfoRow(icon = "⏱️", label = "Durasi",  value = formatDuration(event.durationMinutes))
            Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFE2E8F0))
            DetailInfoRow(icon = getCategoryEmoji(event.category), label = "Kategori", value = event.category)

            Spacer(modifier = Modifier.height(20.dp))

            if (event.description.isNotEmpty()) {
                Text(
                    text       = "Deskripsi",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .padding(14.dp)
                ) {
                    Text(
                        text       = event.description,
                        fontSize   = 14.sp,
                        color      = Color(0xFF475569),
                        lineHeight = 22.sp
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick  = { /* TODO: Edit Event */ },
                modifier = Modifier.weight(1f).height(48.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF4F46E5))
            ) {
                Text(text = "✏️  Edit", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
            Button(
                onClick  = { onBack() },
                modifier = Modifier.weight(1f).height(48.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
            ) {
                Text(text = "🗑️  Hapus", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.White)
            }
        }
    }
}

@Composable
fun DetailInfoRow(icon: String, label: String, value: String) {
    Row(
        modifier          = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, fontSize = 18.sp, modifier = Modifier.width(32.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Medium)
            Text(text = value, fontSize = 14.sp, color = Color(0xFF1E293B), fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun EmptyStateView(modifier: Modifier = Modifier) {
    Column(
        modifier               = modifier,
        verticalArrangement    = Arrangement.Center,
        horizontalAlignment    = Alignment.CenterHorizontally
    ) {
        Text(text = "🗓️", fontSize = 56.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Belum ada jadwal", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF94A3B8))
        Text(text = "Tambahkan kegiatan pertama kamu!", fontSize = 13.sp, color = Color(0xFFCBD5E1))
    }
}

fun parseColor(hex: String): Color =
    try { Color(hex.toColorInt()) } catch (e: Exception) { Color(0xFF4F46E5) }

fun getCategoryEmoji(category: String): String = when (category) {
    "Kuliah"  -> "📚"
    "Pribadi" -> "🏃"
    "Kerja"   -> "💼"
    "Penting" -> "🔴"
    else      -> "📌"
}

fun formatDate(date: String): String {
    val parts = date.split("-")
    if (parts.size != 3) return date
    val day = parts[2].toIntOrNull() ?: return date
    val month = when (parts[1]) {
        "01" -> "Januari";  "02" -> "Februari"; "03" -> "Maret"
        "04" -> "April";    "05" -> "Mei";       "06" -> "Juni"
        "07" -> "Juli";     "08" -> "Agustus";   "09" -> "September"
        "10" -> "Oktober";  "11" -> "November";  "12" -> "Desember"
        else -> parts[1]
    }
    return "$day $month ${parts[0]}"
}

fun formatDateShort(date: String): String {
    val parts = date.split("-")
    if (parts.size != 3) return date
    val month = when (parts[1]) {
        "01" -> "Jan"; "02" -> "Feb"; "03" -> "Mar"; "04" -> "Apr"
        "05" -> "Mei"; "06" -> "Jun"; "07" -> "Jul"; "08" -> "Agu"
        "09" -> "Sep"; "10" -> "Okt"; "11" -> "Nov"; "12" -> "Des"
        else -> parts[1]
    }
    return "${parts[2].toIntOrNull() ?: parts[2]} $month"
}

fun formatDuration(minutes: Int): String = when {
    minutes < 60      -> "$minutes menit"
    minutes % 60 == 0 -> "${minutes / 60} jam"
    else              -> "${minutes / 60} jam ${minutes % 60} mnt"
}