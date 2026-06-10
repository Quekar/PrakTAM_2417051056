package com.example.praktam_2417051056.addtask

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private data class EditPriorityOption(
    val value: Int, val label: String, val emoji: String,
    val color: Color, val bgColor: Color
)

private val editPriorityOptions = listOf(
    EditPriorityOption(1, "Tinggi", "🔴", Color(0xFFEF4444), Color(0xFFFEE2E2)),
    EditPriorityOption(2, "Sedang", "🟡", Color(0xFFF59E0B), Color(0xFFFEF3C7)),
    EditPriorityOption(3, "Rendah", "🟢", Color(0xFF22C55E), Color(0xFFDCFCE7)),
)

private data class EditCategoryOption2(val name: String, val emoji: String, val color: Color)

private val editCategoryOptions2 = listOf(
    EditCategoryOption2("Kuliah",  "📚", Color(0xFF3B82F6)),
    EditCategoryOption2("Pribadi", "🏃", Color(0xFF22C55E)),
    EditCategoryOption2("Kerja",   "💼", Color(0xFFF59E0B)),
    EditCategoryOption2("Lainnya", "📌", Color(0xFF8B5CF6)),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(
    task: Task,
    onBack: () -> Unit = {},
    onTaskUpdated: (
        title: String, description: String, category: String,
        priority: Int, deadline: String
    ) -> Unit = { _, _, _, _, _ -> }
) {
    val coroutineScope    = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var title        by remember { mutableStateOf(task.title) }
    var description  by remember { mutableStateOf(task.description) }
    var selectedCat  by remember {
        mutableStateOf(editCategoryOptions2.find { it.name == task.category } ?: editCategoryOptions2[0])
    }
    var selectedPrio by remember {
        mutableStateOf(editPriorityOptions.find { it.value == task.priority } ?: editPriorityOptions[0])
    }

    val (initDate, initTime, initNoDeadline) = remember(task.deadline) {
        if (task.deadline == "-") {
            Triple(
                LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                "23:59",
                true
            )
        } else {
            val parts = task.deadline.split(" ")
            Triple(
                parts.getOrElse(0) { LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) },
                parts.getOrElse(1) { "23:59" },
                false
            )
        }
    }

    var deadlineDate by remember { mutableStateOf(initDate) }
    var deadlineTime by remember { mutableStateOf(initTime) }
    var noDeadline   by remember { mutableStateOf(initNoDeadline) }

    var isLoading  by remember { mutableStateOf(false) }
    var titleError by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val deadlineString by remember(deadlineDate, deadlineTime, noDeadline) {
        derivedStateOf { if (noDeadline) "-" else "$deadlineDate $deadlineTime" }
    }

    val shimmer = rememberInfiniteTransition(label = "shimmer")
    val shimmerX by shimmer.animateFloat(
        initialValue  = -300f, targetValue = 1000f,
        animationSpec = infiniteRepeatable(tween(1400, easing = LinearEasing)),
        label = "shimmerX"
    )

    fun trySave() {
        titleError = title.isBlank()
        if (titleError) return
        coroutineScope.launch {
            isLoading = true
            delay(1200)
            isLoading = false
            onTaskUpdated(title.trim(), description.trim(), selectedCat.name, selectedPrio.value, deadlineString)
            snackbarHostState.showSnackbar(
                message  = "✅  Task \"${title.trim()}\" berhasil diperbarui!",
                duration = SnackbarDuration.Short
            )
            onBack()
        }
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
        containerColor = Color(0xFFF1F5F9)
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {

            Box(modifier = Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(Color(0xFF4F46E5), Color(0xFF6D28D9))))) {
                if (isLoading) {
                    Box(modifier = Modifier.matchParentSize().background(
                        Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.06f), Color.Transparent),
                            startX = shimmerX, endX = shimmerX + 300f
                        )
                    ))
                }
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick  = { if (!isLoading) onBack() },
                            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.15f))
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Edit Task", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                            Text("Ubah detail tugas kamu", color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp)
                        }
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(Color.White.copy(alpha = 0.15f)).padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("${selectedPrio.emoji} ${selectedPrio.label}", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    AnimatedVisibility(visible = isLoading, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
                        Column {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().height(3.dp).clip(CircleShape), color = Color.White, trackColor = Color.White.copy(alpha = 0.25f))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Menyimpan perubahan…", color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp)
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                EditTaskFormCard {
                    EditTaskFormLabel("Judul Task", required = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value         = title,
                        onValueChange = { title = it; titleError = false },
                        placeholder   = { Text("Contoh: Kerjakan LKP 4", color = Color(0xFFCBD5E1)) },
                        isError       = titleError,
                        enabled       = !isLoading,
                        singleLine    = true,
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(12.dp),
                        colors        = editTaskInputColors(),
                        leadingIcon   = { Text(selectedCat.emoji, fontSize = 18.sp, modifier = Modifier.padding(start = 4.dp)) }
                    )
                    AnimatedVisibility(visible = titleError) {
                        Text("⚠️  Judul tidak boleh kosong", fontSize = 11.sp, color = Color(0xFFEF4444), modifier = Modifier.padding(top = 4.dp, start = 4.dp))
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    EditTaskFormLabel("Deskripsi (opsional)")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value         = description,
                        onValueChange = { description = it },
                        placeholder   = { Text("Catatan tambahan…", color = Color(0xFFCBD5E1)) },
                        enabled       = !isLoading,
                        minLines      = 2, maxLines = 3,
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(12.dp),
                        colors        = editTaskInputColors()
                    )
                }

                EditTaskFormCard {
                    EditTaskFormLabel("Prioritas", required = true)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        editPriorityOptions.forEach { prio ->
                            val isSel = selectedPrio.value == prio.value
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isSel) prio.bgColor else Color(0xFFF8FAFC))
                                    .border(if (isSel) 2.dp else 1.dp, if (isSel) prio.color else Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                                    .clickable(enabled = !isLoading) { selectedPrio = prio }
                                    .padding(vertical = 14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(prio.emoji, fontSize = 22.sp)
                                Text(prio.label, fontSize = 12.sp, fontWeight = if (isSel) FontWeight.ExtraBold else FontWeight.Normal, color = if (isSel) prio.color else Color(0xFF94A3B8))
                                if (isSel) Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(prio.color))
                            }
                        }
                    }
                }

                EditTaskFormCard {
                    EditTaskFormLabel("Kategori")
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        editCategoryOptions2.forEach { cat ->
                            val isSel = selectedCat.name == cat.name
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSel) cat.color.copy(alpha = 0.1f) else Color(0xFFF8FAFC))
                                    .border(if (isSel) 1.5.dp else 1.dp, if (isSel) cat.color else Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                    .clickable(enabled = !isLoading) { selectedCat = cat }
                                    .padding(horizontal = 6.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment     = Alignment.CenterVertically
                            ) {
                                Text(cat.emoji, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(cat.name, fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal, color = if (isSel) cat.color else Color(0xFF94A3B8), maxLines = 1)
                            }
                        }
                    }
                }

                EditTaskFormCard {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        EditTaskFormLabel("Deadline")
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Tanpa deadline", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            Switch(
                                checked         = noDeadline,
                                onCheckedChange = { noDeadline = it },
                                enabled         = !isLoading,
                                colors          = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF4F46E5), uncheckedThumbColor = Color.White, uncheckedTrackColor = Color(0xFFCBD5E1)),
                                modifier        = Modifier.height(24.dp)
                            )
                        }
                    }

                    AnimatedVisibility(visible = !noDeadline, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Column(modifier = Modifier.weight(1.4f)) {
                                    Text("Tanggal", fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    EditTaskPickerButton(icon = "📅", text = editTaskFormatDate(deadlineDate), enabled = !isLoading, onClick = { showDatePicker = true })
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Jam", fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    EditTaskPickerButton(icon = "🕐", text = deadlineTime, enabled = !isLoading, onClick = { showTimePicker = true })
                                }
                            }
                            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFFFEF3C7)).padding(horizontal = 10.dp, vertical = 6.dp)) {
                                Text("⏰  Deadline: $deadlineDate $deadlineTime", fontSize = 12.sp, color = Color(0xFFF59E0B), fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    if (noDeadline) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFFF1F5F9)).padding(horizontal = 10.dp, vertical = 6.dp)) {
                            Text("📌  Tidak ada deadline", fontSize = 12.sp, color = Color(0xFF94A3B8))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
            }

            Surface(modifier = Modifier.fillMaxWidth(), color = Color.White, tonalElevation = 4.dp) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick  = { if (!isLoading) onBack() }, enabled = !isLoading,
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape    = RoundedCornerShape(14.dp),
                        colors   = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF64748B), disabledContentColor = Color(0xFFCBD5E1)),
                        border   = BorderStroke(1.dp, if (!isLoading) Color(0xFFE2E8F0) else Color(0xFFF1F5F9))
                    ) {
                        Text("Batal", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                    Button(
                        onClick  = { trySave() }, enabled = !isLoading,
                        modifier = Modifier.weight(1.6f).height(52.dp),
                        shape    = RoundedCornerShape(14.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5), disabledContainerColor = Color(0xFF4F46E5).copy(alpha = 0.5f))
                    ) {
                        AnimatedContent(targetState = isLoading, transitionSpec = { fadeIn(tween(150)) togetherWith fadeOut(tween(150)) }, label = "btnSave") { loading ->
                            if (loading) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                                    Text("Menyimpan…", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                                }
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                    Text("Simpan Task", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        EditTaskPickerDialog("Pilih Tanggal Deadline", editTaskGenerateDates(), deadlineDate, { deadlineDate = it; showDatePicker = false }, { showDatePicker = false })
    }
    if (showTimePicker) {
        EditTaskPickerDialog("Pilih Jam Deadline", editTaskGenerateTimes(), deadlineTime, { deadlineTime = it; showTimePicker = false }, { showTimePicker = false })
    }
}

@Composable
private fun EditTaskFormCard(content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
private fun EditTaskFormLabel(text: String, required: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF475569))
        if (required) { Spacer(modifier = Modifier.width(4.dp)); Text("*", fontSize = 13.sp, color = Color(0xFFEF4444), fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun EditTaskPickerButton(icon: String, text: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (enabled) Color(0xFFF8FAFC) else Color(0xFFF1F5F9))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 10.dp, vertical = 11.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(icon, fontSize = 14.sp)
            Text(text, fontSize = 13.sp, color = if (enabled) Color(0xFF1E293B) else Color(0xFF94A3B8), fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun editTaskInputColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFF4F46E5), unfocusedBorderColor = Color(0xFFE2E8F0),
    errorBorderColor = Color(0xFFEF4444), focusedLabelColor = Color(0xFF4F46E5),
    cursorColor = Color(0xFF4F46E5), focusedContainerColor = Color.White,
    unfocusedContainerColor = Color(0xFFF8FAFC), disabledContainerColor = Color(0xFFF1F5F9), disabledBorderColor = Color(0xFFF1F5F9)
)

@Composable
private fun EditTaskPickerDialog(title: String, options: List<String>, current: String, onSelect: (String) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss, shape = RoundedCornerShape(20.dp), containerColor = Color.White,
        title = { Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B)) },
        text = {
            Column(modifier = Modifier.fillMaxWidth().heightIn(max = 340.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                options.forEach { opt ->
                    val isSel = opt == current
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(if (isSel) Color(0xFFEEF2FF) else Color.Transparent).clickable { onSelect(opt) }.padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(opt, fontSize = 14.sp, color = if (isSel) Color(0xFF4F46E5) else Color(0xFF1E293B), fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                        if (isSel) Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF4F46E5), modifier = Modifier.size(16.dp))
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Tutup", color = Color(0xFF4F46E5), fontWeight = FontWeight.SemiBold) } }
    )
}

private fun editTaskFormatDate(date: String): String {
    return try {
        val p = date.split("-")
        val month = when (p[1]) {
            "01" -> "Jan"; "02" -> "Feb"; "03" -> "Mar"; "04" -> "Apr"
            "05" -> "Mei"; "06" -> "Jun"; "07" -> "Jul"; "08" -> "Agu"
            "09" -> "Sep"; "10" -> "Okt"; "11" -> "Nov"; "12" -> "Des"
            else -> p[1]
        }
        "${p[2].toInt()} $month ${p[0]}"
    } catch (e: Exception) { date }
}

private fun editTaskGenerateDates(): List<String> {
    val today = LocalDate.now()
    return (-3..60).map { today.plusDays(it.toLong()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) }
}

private fun editTaskGenerateTimes(): List<String> =
    (0..23).flatMap { h -> listOf("00", "15", "30", "45").map { m -> String.format("%02d:%s", h, m) } }