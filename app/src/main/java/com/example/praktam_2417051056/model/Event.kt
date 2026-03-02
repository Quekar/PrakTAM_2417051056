package com.example.praktam_2417051056.model

data class Event(
    val title: String,
    val description: String = "",
    val date: String,           // Format: YYYY-MM-DD
    val startTime: String,      // Format: HH:mm
    val endTime: String,        // Format: HH:mm
    val durationMinutes: Int,   // Durasi dalam menit
    val category: String,       // Kuliah, Pribadi, Kerja, Penting, Lainnya
)