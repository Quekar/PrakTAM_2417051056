package com.example.praktam_2417051056.addevent

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
import com.example.praktam_2417051056.model.Event
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private data class EditCategoryOption(
    val name: String,
    val color: Color,
    val hex: String,
    val emoji: String
)

private val editCategoryOptions = listOf(
    EditCategoryOption("Kuliah",  Color(0xFF3B82F6), "#3B82F6", "📚"),
    EditCategoryOption("Pribadi", Color(0xFF22C55E), "#22C55E", "🏃"),
    EditCategoryOption("Kerja",   Color(0xFFF59E0B), "#F59E0B", "💼"),
    EditCategoryOption("Penting", Color(0xFFEF4444), "#EF4444", "🔴"),
    EditCategoryOption("Lainnya", Color(0xFF8B5CF6), "#8B5CF6", "📌"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventBottomSheet(
    event: Event,
    onDismiss: () -> Unit = {},
    onEventUpdated: (
        title: String, date: String, startTime: String, endTime: String,
        category: String, color: String, description: String, isRepeating: Boolean
    ) -> Unit = { _, _, _, _, _, _, _, _ -> },
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val coroutineScope = rememberCoroutineScope()

    var title       by remember { mutableStateOf(event.title) }
    var description by remember { mutableStateOf(event.description) }
    var date        by remember { mutableStateOf(event.date) }
    var startTime   by remember { mutableStateOf(event.startTime) }
    var endTime     by remember { mutableStateOf(event.endTime) }
    var selectedCat by remember {
        mutableStateOf(
            editCategoryOptions.find { it.name == event.category } ?: editCategoryOptions[0]
        )
    }
    var isRepeating by remember { mutableStateOf(event.isRepeating) }

    var isLoading  by remember { mutableStateOf(false) }
    var titleError by remember { mutableStateOf(false) }
    var timeError  by remember { mutableStateOf(false) }

    val durationText by remember(startTime, endTime) {
        derivedStateOf { computeEditDuration(startTime, endTime) }
    }

    var showDatePicker  by remember { mutableStateOf(false) }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker   by remember { mutableStateOf(false) }

    val loadingProgress = remember { Animatable(0f) }
    LaunchedEffect(isLoading) {
        if (isLoading) loadingProgress.animateTo(1f, tween(1800, easing = FastOutSlowInEasing))
        else loadingProgress.snapTo(0f)
    }

    fun trySave() {
        titleError = title.isBlank()
        timeError  = durationText.startsWith("⚠")
        if (titleError || timeError) return

        coroutineScope.launch {
            isLoading = true
            delay(1200)
            isLoading = false
            onEventUpdated(
                title.trim(), date, startTime, endTime,
                selectedCat.name, selectedCat.hex, description.trim(), isRepeating
            )
            snackbarHostState.showSnackbar(
                message  = "✅  Kegiatan \"${title.trim()}\" berhasil diperbarui!",
                duration = SnackbarDuration.Short
            )
            onDismiss()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        Box(
            modifier         = Modifier.fillMaxWidth().padding(top = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp).height(4.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0))
            )
        }

        AnimatedVisibility(
            visible = isLoading,
            enter   = fadeIn() + expandVertically(),
            exit    = fadeOut() + shrinkVertically()
        ) {
            LinearProgressIndicator(
                progress   = { loadingProgress.value },
                modifier   = Modifier.fillMaxWidth().height(3.dp),
                color      = Color(0xFF4F46E5),
                trackColor = Color(0xFFE0E7FF),
                strokeCap  = StrokeCap.Round
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text       = "Edit Kegiatan",
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = Color(0xFF1E293B)
                    )
                    Text(
                        text     = "Ubah detail jadwal kamu",
                        fontSize = 12.sp,
                        color    = Color(0xFF94A3B8)
                    )
                }
                Box(
                    modifier         = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF1F5F9))
                        .clickable(enabled = !isLoading) { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = Icons.Default.Close,
                        contentDescription = "Tutup",
                        tint               = Color(0xFF64748B),
                        modifier           = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            EditFormLabel2("Judul Kegiatan", required = true)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value         = title,
                onValueChange = { title = it; titleError = false },
                placeholder   = { Text("Contoh: Kuliah TAM Responsi", color = Color(0xFFCBD5E1)) },
                isError       = titleError,
                enabled       = !isLoading,
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                colors        = editInputColors2(),
                leadingIcon   = {
                    Text(
                        text     = selectedCat.emoji,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            )
            AnimatedVisibility(visible = titleError) {
                Text(
                    text     = "⚠️  Judul tidak boleh kosong",
                    fontSize = 11.sp,
                    color    = Color(0xFFEF4444),
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            EditFormLabel2("Tanggal", required = true)
            Spacer(modifier = Modifier.height(6.dp))
            EditPickerButton2(
                icon    = "📅",
                text    = editFormatDisplayDate(date),
                enabled = !isLoading,
                onClick = { showDatePicker = true }
            )

            Spacer(modifier = Modifier.height(16.dp))
            EditFormLabel2("Waktu", required = true)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text       = "Mulai",
                        fontSize   = 11.sp,
                        color      = Color(0xFF94A3B8),
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    EditPickerButton2(
                        icon    = "🕐",
                        text    = startTime,
                        enabled = !isLoading,
                        onClick = { showStartPicker = true }
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text       = "Selesai",
                        fontSize   = 11.sp,
                        color      = Color(0xFF94A3B8),
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    EditPickerButton2(
                        icon    = "🕔",
                        text    = endTime,
                        enabled = !isLoading,
                        onClick = { showEndPicker = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            AnimatedContent(
                targetState    = durationText,
                transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
                label          = "dur"
            ) { dur ->
                val isErr = dur.startsWith("⚠")
                Row(
                    modifier              = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isErr) Color(0xFFFEE2E2) else Color(0xFFEEF2FF))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = if (isErr) "⚠️" else "⏱️", fontSize = 13.sp)
                    Text(
                        text       = if (isErr) "Jam selesai tidak boleh sebelum jam mulai"
                        else "Durasi: $dur",
                        fontSize   = 12.sp,
                        color      = if (isErr) Color(0xFFEF4444) else Color(0xFF4F46E5),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            EditFormLabel2("Kategori")
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                editCategoryOptions.forEach { cat ->
                    val isSel = selectedCat.name == cat.name
                    Column(
                        modifier            = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSel) cat.color.copy(alpha = 0.12f) else Color(0xFFF8FAFC))
                            .border(
                                width = if (isSel) 1.5.dp else 1.dp,
                                color = if (isSel) cat.color else Color(0xFFE2E8F0),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable(enabled = !isLoading) { selectedCat = cat }
                            .padding(vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = cat.emoji, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text       = cat.name,
                            fontSize   = 10.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color      = if (isSel) cat.color else Color(0xFF94A3B8),
                            textAlign  = TextAlign.Center,
                            maxLines   = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            EditFormLabel2("Deskripsi (opsional)")
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value         = description,
                onValueChange = { description = it },
                placeholder   = { Text("Tambahkan catatan…", color = Color(0xFFCBD5E1)) },
                enabled       = !isLoading,
                minLines      = 3,
                maxLines      = 4,
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                colors        = editInputColors2()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (isRepeating) Color(0xFFEEF2FF) else Color(0xFFF8FAFC)
                    )
                    .border(
                        width = if (isRepeating) 1.5.dp else 1.dp,
                        color = if (isRepeating) Color(0xFF4F46E5) else Color(0xFFE2E8F0),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable(enabled = !isLoading) { isRepeating = !isRepeating }
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier          = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier         = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isRepeating) Color(0xFF4F46E5).copy(alpha = 0.12f)
                                else Color(0xFFE2E8F0).copy(alpha = 0.5f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = Icons.Default.Repeat,
                            contentDescription = "Repeat",
                            tint               = if (isRepeating) Color(0xFF4F46E5) else Color(0xFF94A3B8),
                            modifier           = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text       = "Ulangi setiap minggu",
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = if (isRepeating) Color(0xFF4F46E5) else Color(0xFF1E293B)
                        )
                        Text(
                            text       = if (isRepeating)
                                "Aktif — tampil di semua minggu pada hari yang sama"
                            else
                                "Nonaktif — hanya tampil pada tanggal ini",
                            fontSize   = 11.sp,
                            color      = if (isRepeating) Color(0xFF6366F1) else Color(0xFF94A3B8),
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    Checkbox(
                        checked         = isRepeating,
                        onCheckedChange = { if (!isLoading) isRepeating = it },
                        enabled         = !isLoading,
                        colors          = CheckboxDefaults.colors(
                            checkedColor   = Color(0xFF4F46E5),
                            uncheckedColor = Color(0xFFCBD5E1),
                            checkmarkColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick  = onDismiss,
                    enabled  = !isLoading,
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape    = RoundedCornerShape(14.dp),
                    colors   = ButtonDefaults.outlinedButtonColors(
                        contentColor         = Color(0xFF64748B),
                        disabledContentColor = Color(0xFFCBD5E1)
                    ),
                    border   = BorderStroke(1.dp, if (!isLoading) Color(0xFFE2E8F0) else Color(0xFFF1F5F9))
                ) {
                    Text("Batal", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
                Button(
                    onClick  = { trySave() },
                    enabled  = !isLoading,
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape    = RoundedCornerShape(14.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor         = Color(0xFF4F46E5),
                        disabledContainerColor = Color(0xFF4F46E5).copy(alpha = 0.5f)
                    )
                ) {
                    AnimatedContent(
                        targetState    = isLoading,
                        transitionSpec = { fadeIn(tween(150)) togetherWith fadeOut(tween(150)) },
                        label          = "btn"
                    ) { loading ->
                        if (loading) {
                            Row(
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier    = Modifier.size(18.dp),
                                    color       = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text       = "Menyimpan…",
                                    fontSize   = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color      = Color.White
                                )
                            }
                        } else {
                            Row(
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector        = Icons.Default.Check,
                                    contentDescription = null,
                                    tint               = Color.White,
                                    modifier           = Modifier.size(18.dp)
                                )
                                Text(
                                    text       = "Simpan",
                                    fontSize   = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color      = Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }

    if (showDatePicker) {
        EditPickerDialog2(
            title     = "Pilih Tanggal",
            options   = editGenerateDateOptions2(),
            current   = date,
            onSelect  = { date = it; showDatePicker = false },
            onDismiss = { showDatePicker = false }
        )
    }
    if (showStartPicker) {
        EditPickerDialog2(
            title     = "Jam Mulai",
            options   = editGenerateTimeOptions2(),
            current   = startTime,
            onSelect  = { startTime = it; showStartPicker = false },
            onDismiss = { showStartPicker = false }
        )
    }
    if (showEndPicker) {
        EditPickerDialog2(
            title     = "Jam Selesai",
            options   = editGenerateTimeOptions2(),
            current   = endTime,
            onSelect  = { endTime = it; showEndPicker = false },
            onDismiss = { showEndPicker = false }
        )
    }
}

@Composable
private fun EditFormLabel2(text: String, required: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text       = text,
            fontSize   = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color      = Color(0xFF475569)
        )
        if (required) {
            Spacer(modifier = Modifier.width(4.dp))
            Text("*", fontSize = 13.sp, color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun EditPickerButton2(icon: String, text: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (enabled) Color(0xFFF8FAFC) else Color(0xFFF1F5F9))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 14.dp, vertical = 13.dp)
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = icon, fontSize = 16.sp)
            Text(
                text       = text,
                fontSize   = 14.sp,
                color      = if (enabled) Color(0xFF1E293B) else Color(0xFF94A3B8),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun editInputColors2() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor      = Color(0xFF4F46E5),
    unfocusedBorderColor    = Color(0xFFE2E8F0),
    errorBorderColor        = Color(0xFFEF4444),
    focusedLabelColor       = Color(0xFF4F46E5),
    cursorColor             = Color(0xFF4F46E5),
    focusedContainerColor   = Color.White,
    unfocusedContainerColor = Color(0xFFF8FAFC),
    disabledContainerColor  = Color(0xFFF1F5F9),
    disabledBorderColor     = Color(0xFFF1F5F9)
)

@Composable
private fun EditPickerDialog2(
    title: String,
    options: List<String>,
    current: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape            = RoundedCornerShape(20.dp),
        containerColor   = Color.White,
        title = {
            Text(
                text       = title,
                fontWeight = FontWeight.Bold,
                fontSize   = 16.sp,
                color      = Color(0xFF1E293B)
            )
        },
        text = {
            Column(
                modifier            = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 320.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                options.forEach { opt ->
                    val isSel = opt == current
                    Row(
                        modifier              = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSel) Color(0xFFEEF2FF) else Color.Transparent)
                            .clickable { onSelect(opt) }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text       = opt,
                            fontSize   = 14.sp,
                            color      = if (isSel) Color(0xFF4F46E5) else Color(0xFF1E293B),
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                        )
                        if (isSel) {
                            Icon(
                                imageVector        = Icons.Default.Check,
                                contentDescription = null,
                                tint               = Color(0xFF4F46E5),
                                modifier           = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup", color = Color(0xFF4F46E5), fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

private fun computeEditDuration(start: String, end: String): String {
    return try {
        val s    = LocalTime.parse(start)
        val e    = LocalTime.parse(end)
        val diff = java.time.Duration.between(s, e).toMinutes()
        when {
            diff <= 0       -> "⚠ invalid"
            diff < 60       -> "$diff menit"
            diff % 60 == 0L -> "${diff / 60} jam"
            else            -> "${diff / 60} jam ${diff % 60} mnt"
        }
    } catch (ex: Exception) { "–" }
}

private fun editFormatDisplayDate(date: String): String {
    return try {
        val p = date.split("-")
        val month = when (p[1]) {
            "01" -> "Januari";  "02" -> "Februari"; "03" -> "Maret";    "04" -> "April"
            "05" -> "Mei";      "06" -> "Juni";      "07" -> "Juli";     "08" -> "Agustus"
            "09" -> "September";"10" -> "Oktober";   "11" -> "November"; "12" -> "Desember"
            else -> p[1]
        }
        "${p[2].toInt()} $month ${p[0]}"
    } catch (e: Exception) { date }
}

private fun editGenerateDateOptions2(): List<String> {
    val today = LocalDate.now()
    return (-30..90).map { today.plusDays(it.toLong()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) }
}

private fun editGenerateTimeOptions2(): List<String> =
    (0..23).flatMap { h ->
        listOf("00", "15", "30", "45").map { m ->
            String.format("%02d:%s", h, m)
        }
    }