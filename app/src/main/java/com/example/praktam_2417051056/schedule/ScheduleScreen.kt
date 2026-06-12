package com.example.praktam_2417051056.schedule

import androidx.core.graphics.toColorInt
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.example.praktam_2417051056.getCategoryIcon
import com.example.praktam_2417051056.model.Event
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

data class DayItem(
    val date: String,       // Format: YYYY-MM-DD
    val dayName: String,
    val dayNumber: Int
)

private val dayNames = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")

fun generateWeekDays(weekOffset: Int): List<DayItem> {
    val today     = LocalDate.now()
    val monday    = today
        .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        .plusWeeks(weekOffset.toLong())
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return (0..6).map { i ->
        val date = monday.plusDays(i.toLong())
        DayItem(
            date      = date.format(formatter),
            dayName   = dayNames[i],
            dayNumber = date.dayOfMonth
        )
    }
}

fun eventOccursOnDate(event: Event, targetDate: String): Boolean {
    if (event.date == targetDate) return true
    if (!event.isRepeating) return false

    return try {
        val original = LocalDate.parse(event.date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val target   = LocalDate.parse(targetDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        target.dayOfWeek == original.dayOfWeek && !target.isBefore(original)
    } catch (e: Exception) {
        false
    }
}

@Composable
fun ScheduleScreen(
    events: List<Event> = emptyList(),
    onEventClick: (Event) -> Unit = {},
    onAddEvent: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val today        = remember { LocalDate.now() }
    val formatter    = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }

    var weekOffset   by remember { mutableStateOf(0) }
    var selectedDate by remember { mutableStateOf(today.format(formatter)) }

    val currentWeekDays by remember(weekOffset) {
        derivedStateOf { generateWeekDays(weekOffset) }
    }

    LaunchedEffect(weekOffset) {
        val weekDates = currentWeekDays.map { it.date }
        if (selectedDate !in weekDates) {
            selectedDate = weekDates.first()
        }
    }

    val filteredEvents by remember(selectedDate, events) {
        derivedStateOf {
            events
                .filter { eventOccursOnDate(it, selectedDate) }
                .sortedBy { it.startTime }
        }
    }

    val monthLabel by remember(currentWeekDays) {
        derivedStateOf {
            val first = LocalDate.parse(currentWeekDays.first().date)
            val last  = LocalDate.parse(currentWeekDays.last().date)
            if (first.month == last.month) {
                "${monthName(first.monthValue)} ${first.year}"
            } else {
                "${monthNameShort(first.monthValue)} – ${monthNameShort(last.monthValue)} ${last.year}"
            }
        }
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
                        colors = listOf(Color(0xFF4F46E5), Color(0xFF7C3AED))
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
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "App Icon",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "DailyDo",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.3.sp
                        )
                        Text(
                            text = "Daily Scheduler",
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 12.sp
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = { weekOffset-- },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Minggu sebelumnya",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        IconButton(
                            onClick = { weekOffset++ },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                        ) {
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = "Minggu berikutnya",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = monthLabel,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(currentWeekDays) { day ->
                        val isSelected = selectedDate == day.date
                        val hasEvent = events.any { eventOccursOnDate(it, day.date) }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isSelected) Color.White
                                    else Color.White.copy(alpha = 0.15f)
                                )
                                .clickable { selectedDate = day.date }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = day.dayName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isSelected) Color(0xFF4F46E5)
                                else Color.White.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${day.dayNumber}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSelected) Color(0xFF4F46E5) else Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isSelected && hasEvent -> Color(0xFF4F46E5)
                                            hasEvent               -> Color.White.copy(alpha = 0.8f)
                                            else                   -> Color.Transparent
                                        }
                                    )
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = formatFullDate(selectedDate),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = "${filteredEvents.size} kegiatan",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            Button(
                onClick = { onAddEvent() },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Tambah Event",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Tambah", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Divider(color = Color(0xFFE2E8F0), thickness = 1.dp)

        if (filteredEvents.isEmpty()) {
            ScheduleEmptyState(
                date = selectedDate,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Text(
                        text = "Jadwal Hari Ini",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                items(filteredEvents) { event ->
                    EventCard(
                        event = event,
                        onClick = { onEventClick(event) }
                    )
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun EventCard(event: Event, onClick: () -> Unit) {
    val eventColor   = parseColorSchedule(event.color)
    val categoryIcon = getCategoryIcon(event.category)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(
                        color = eventColor,
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier         = Modifier
                                .size(26.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(eventColor.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector        = categoryIcon,
                                contentDescription = event.category,
                                tint               = eventColor,
                                modifier           = Modifier.size(15.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = eventColor.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = event.category,
                                color = eventColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        if (event.isRepeating) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color(0xFFF1F5F9)
                            ) {
                                Row(
                                    modifier              = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment     = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector        = Icons.Default.Repeat,
                                        contentDescription = "Berulang setiap minggu",
                                        tint               = Color(0xFF64748B),
                                        modifier           = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = "Mingguan",
                                        color = Color(0xFF64748B),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF1F5F9)
                    ) {
                        Text(
                            text = formatDurationSchedule(event.durationMinutes),
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = event.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (event.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = event.description,
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(eventColor)
                    )
                    Text(
                        text = "${event.startTime} – ${event.endTime}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF475569)
                    )
                }
            }
        }
    }
}

@Composable
fun ScheduleEmptyState(date: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier         = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(0xFFEEF2FF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Default.CalendarMonth,
                contentDescription = "Tidak ada kegiatan",
                tint               = Color(0xFF4F46E5),
                modifier           = Modifier.size(40.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Tidak ada kegiatan",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF94A3B8)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Belum ada jadwal untuk ${formatFullDate(date)}",
            fontSize = 13.sp,
            color = Color(0xFFCBD5E1),
            textAlign = TextAlign.Center
        )
    }
}

fun parseColorSchedule(hex: String): Color {
    return try { Color(hex.toColorInt()) } catch (e: Exception) { Color(0xFF4F46E5) }
}

fun formatDurationSchedule(minutes: Int): String = when {
    minutes < 60      -> "$minutes menit"
    minutes % 60 == 0 -> "${minutes / 60} jam"
    else              -> "${minutes / 60} jam ${minutes % 60} mnt"
}

fun formatFullDate(date: String): String {
    return try {
        val localDate  = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val dayOfWeek  = when (localDate.dayOfWeek) {
            DayOfWeek.MONDAY    -> "Senin"
            DayOfWeek.TUESDAY   -> "Selasa"
            DayOfWeek.WEDNESDAY -> "Rabu"
            DayOfWeek.THURSDAY  -> "Kamis"
            DayOfWeek.FRIDAY    -> "Jumat"
            DayOfWeek.SATURDAY  -> "Sabtu"
            DayOfWeek.SUNDAY    -> "Minggu"
        }
        "$dayOfWeek, ${localDate.dayOfMonth} ${monthName(localDate.monthValue)} ${localDate.year}"
    } catch (e: Exception) { date }
}

fun monthName(month: Int): String = when (month) {
    1  -> "Januari";  2  -> "Februari"; 3  -> "Maret"
    4  -> "April";    5  -> "Mei";       6  -> "Juni"
    7  -> "Juli";     8  -> "Agustus";   9  -> "September"
    10 -> "Oktober";  11 -> "November";  12 -> "Desember"
    else -> "$month"
}

fun monthNameShort(month: Int): String = when (month) {
    1  -> "Jan"; 2  -> "Feb"; 3  -> "Mar"; 4  -> "Apr"
    5  -> "Mei"; 6  -> "Jun"; 7  -> "Jul"; 8  -> "Agu"
    9  -> "Sep"; 10 -> "Okt"; 11 -> "Nov"; 12 -> "Des"
    else -> "$month"
}