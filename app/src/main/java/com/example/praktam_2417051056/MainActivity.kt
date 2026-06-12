package com.example.praktam_2417051056

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import com.example.praktam_2417051056.addevent.AddEventBottomSheet
import com.example.praktam_2417051056.addevent.EditEventBottomSheet
import com.example.praktam_2417051056.addtask.AddTaskScreen
import com.example.praktam_2417051056.addtask.EditTaskScreen
import com.example.praktam_2417051056.model.Event
import com.example.praktam_2417051056.model.Task
import com.example.praktam_2417051056.notification.NotificationHelper
import com.example.praktam_2417051056.schedule.ScheduleScreen
import com.example.praktam_2417051056.splash.SplashScreen
import com.example.praktam_2417051056.taskdetail.DetailTaskScreen
import com.example.praktam_2417051056.tasklist.TaskListScreen
import com.example.praktam_2417051056.ui.theme.PrakTAM_2417051056Theme
import com.example.praktam_2417051056.viewmodel.AppViewModel
import com.example.praktam_2417051056.viewmodel.UiState

enum class Screen { SPLASH, MAIN }
enum class AppTab { SCHEDULE, TASKS }

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        NotificationHelper.createChannel(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

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
fun AppRoot(
    viewModel: AppViewModel = viewModel()
) {
    val eventState by viewModel.eventState.collectAsState()
    val taskState  by viewModel.taskState.collectAsState()

    var currentTab    by remember { mutableStateOf(AppTab.SCHEDULE) }
    var selectedEvent by remember { mutableStateOf<Event?>(null) }
    var selectedTask  by remember { mutableStateOf<Task?>(null) }
    var showAddTask   by remember { mutableStateOf(false) }
    var showAddEvent  by remember { mutableStateOf(false) }
    var editingEvent  by remember { mutableStateOf<Event?>(null) }
    var editingTask   by remember { mutableStateOf<Task?>(null) }

    val bottomSheetState  = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snackbarHostState = remember { SnackbarHostState() }

    if (selectedEvent != null) {
        DetailScreen(
            event    = selectedEvent!!,
            onBack   = { selectedEvent = null },
            onDelete = { event ->
                viewModel.deleteEvent(event.id)
                selectedEvent = null
            },
            onEdit   = { event ->
                editingEvent  = event
                selectedEvent = null
            }
        )
        return
    }

    if (selectedTask != null) {
        DetailTaskScreen(
            task     = selectedTask!!,
            onBack   = { selectedTask = null },
            onDelete = { task ->
                viewModel.deleteTask(task.id)
                selectedTask = null
            },
            onEdit   = { task ->
                editingTask  = task
                selectedTask = null
            }
        )
        return
    }

    if (editingTask != null) {
        EditTaskScreen(
            task = editingTask!!,
            onBack = { editingTask = null },
            onTaskUpdated = { title, description, category, priority, deadline ->
                viewModel.editTask(editingTask!!.id, title, description, category, priority, deadline)
                editingTask = null
            }
        )
        return
    }

    if (showAddTask) {
        AddTaskScreen(
            onBack = { showAddTask = false },
            onTaskSaved = { title, description, category, priority, deadline, createdAt ->
                viewModel.addTask(title, description, category, priority, deadline, createdAt)
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

                AppTab.SCHEDULE -> when (val state = eventState) {
                    is UiState.Loading ->
                        FullScreenLoading(message = "Memuat jadwal…")

                    is UiState.Success ->
                        ScheduleScreen(
                            events       = state.data,
                            onEventClick = { event -> selectedEvent = event },
                            onAddEvent   = { showAddEvent = true }
                        )

                    is UiState.Error ->
                        FullScreenError(
                            message = state.message,
                            onRetry = { viewModel.fetchEvents() }
                        )
                }

                AppTab.TASKS -> when (val state = taskState) {
                    is UiState.Loading ->
                        FullScreenLoading(message = "Memuat task…")

                    is UiState.Success ->
                        TaskListScreen(
                            tasks          = state.data,
                            onTasksChanged = { updatedList ->
                                viewModel.updateTaskList(updatedList)
                            },
                            onTaskClick    = { task -> selectedTask = task },
                            onAddTask      = { showAddTask = true }
                        )

                    is UiState.Error ->
                        FullScreenError(
                            message = state.message,
                            onRetry = { viewModel.fetchTasks() }
                        )
                }
            }
        }
    }

    if (showAddEvent) {
        ModalBottomSheet(
            onDismissRequest = { showAddEvent = false },
            sheetState       = bottomSheetState,
            containerColor   = Color.White,
            dragHandle       = null,
            shape            = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            AddEventBottomSheet(
                snackbarHostState = snackbarHostState,
                onDismiss         = { showAddEvent = false },
                onEventSaved      = { title, date, startTime, endTime, category, color, description ->
                    viewModel.addEvent(title, date, startTime, endTime, category, color, description)
                    showAddEvent = false
                }
            )
        }
    }

    if (editingEvent != null) {
        ModalBottomSheet(
            onDismissRequest = { editingEvent = null },
            sheetState       = bottomSheetState,
            containerColor   = Color.White,
            dragHandle       = null,
            shape            = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            EditEventBottomSheet(
                event             = editingEvent!!,
                snackbarHostState = snackbarHostState,
                onDismiss         = { editingEvent = null },
                onEventUpdated    = { title, date, startTime, endTime, category, color, description ->
                    viewModel.editEvent(editingEvent!!.id, title, date, startTime, endTime, category, color, description)
                    editingEvent = null
                }
            )
        }
    }
}

@Composable
fun FullScreenLoading(message: String = "Memuat data…") {
    Box(
        modifier         = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color       = Color(0xFF4F46E5),
                strokeWidth = 3.dp,
                modifier    = Modifier.size(48.dp)
            )
            Text(
                text       = message,
                fontSize   = 14.sp,
                color      = Color(0xFF94A3B8),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun FullScreenError(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier         = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier            = Modifier.padding(horizontal = 32.dp)
        ) {
            Box(
                modifier         = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFEE2E2)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Default.WifiOff,
                    contentDescription = "Error",
                    tint               = Color(0xFFEF4444),
                    modifier           = Modifier.size(36.dp)
                )
            }
            Text(
                text       = "Oops! Terjadi Kesalahan",
                fontSize   = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = Color(0xFF1E293B)
            )
            Text(
                text       = message,
                fontSize   = 13.sp,
                color      = Color(0xFF64748B),
                textAlign  = TextAlign.Center,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = onRetry,
                shape   = RoundedCornerShape(12.dp),
                colors  = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
            ) {
                Icon(
                    imageVector        = Icons.Default.Refresh,
                    contentDescription = "Coba Lagi",
                    modifier           = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text       = "Coba Lagi",
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 14.sp
                )
            }
        }
    }
}

data class NavItem(
    val tab: AppTab,
    val label: String,
    val icon: ImageVector
)

@Composable
fun AppBottomNavBar(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit
) {
    val navItems = listOf(
        NavItem(AppTab.SCHEDULE, "Schedule", Icons.Default.CalendarMonth),
        NavItem(AppTab.TASKS,    "Tasks",    Icons.Default.CheckCircle)
    )

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp,
        modifier       = Modifier
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
                    Icon(
                        imageVector        = item.icon,
                        contentDescription = item.label,
                        modifier           = Modifier.size(24.dp)
                    )
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
    onDelete: (Event) -> Unit = {},
    onEdit: (Event) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val eventColor   = parseColor(event.color)
    val imageUrl     = getCategoryImageUrl(event.category)
    val categoryIcon = getCategoryIcon(event.category)
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            shape            = RoundedCornerShape(20.dp),
            containerColor   = Color.White,
            title = {
                Text(
                    text       = "Hapus Kegiatan?",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    color      = Color(0xFF1E293B)
                )
            },
            text = {
                Text(
                    text       = "\"${event.title}\" akan dihapus secara permanen.",
                    fontSize   = 14.sp,
                    color      = Color(0xFF64748B),
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDelete(event)
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
                .height(240.dp)
        ) {
            SubcomposeAsyncImage(
                model              = imageUrl,
                contentDescription = event.category,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.fillMaxSize(),
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(eventColor, eventColor.copy(alpha = 0.6f))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color       = Color.White,
                            modifier    = Modifier.size(36.dp),
                            strokeWidth = 3.dp
                        )
                    }
                },
                error = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(eventColor, eventColor.copy(alpha = 0.6f))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = categoryIcon,
                            contentDescription = event.category,
                            tint               = Color.White.copy(alpha = 0.6f),
                            modifier           = Modifier.size(80.dp)
                        )
                    }
                }
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f))
                        )
                    )
            )
            Box(
                modifier         = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier         = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.22f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = categoryIcon,
                            contentDescription = event.category,
                            tint               = Color.White,
                            modifier           = Modifier.size(34.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.22f)
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
                    .background(Color.Black.copy(alpha = 0.25f))
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

            DetailInfoRow(Icons.Default.DateRange, "Tanggal",  formatDate(event.date))
            Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFE2E8F0))
            DetailInfoRow(Icons.Default.Schedule,  "Waktu",    "${event.startTime} – ${event.endTime}")
            Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFE2E8F0))
            DetailInfoRow(Icons.Default.Timer,     "Durasi",   formatDuration(event.durationMinutes))
            Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFE2E8F0))
            DetailInfoRow(categoryIcon,            "Kategori", event.category)

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
                onClick  = { onEdit(event) },
                modifier = Modifier.weight(1f).height(48.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF4F46E5))
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Edit", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
            Button(
                onClick  = { showDeleteDialog = true },
                modifier = Modifier.weight(1f).height(48.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Hapus", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.White)
            }
        }
    }
}

@Composable
fun DetailInfoRow(icon: ImageVector, label: String, value: String) {
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
            Text(text = label, fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Medium)
            Text(text = value, fontSize = 14.sp, color = Color(0xFF1E293B), fontWeight = FontWeight.SemiBold)
        }
    }
}

fun parseColor(hex: String): Color =
    try { Color(hex.toColorInt()) } catch (e: Exception) { Color(0xFF4F46E5) }

fun getCategoryImageUrl(category: String): String = when (category) {
    "Kuliah"  -> "https://images.unsplash.com/photo-1523050854058-8df90110c9f1?w=800&q=80"
    "Pribadi" -> "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=800&q=80"
    "Kerja"   -> "https://images.unsplash.com/photo-1497366754035-f200968a6e72?w=800&q=80"
    "Penting" -> "https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=800&q=80"
    else      -> "https://images.unsplash.com/photo-1484480974693-6ca0a78fb36b?w=800&q=80"
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