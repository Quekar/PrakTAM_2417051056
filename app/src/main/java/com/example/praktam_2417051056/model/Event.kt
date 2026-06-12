package com.example.praktam_2417051056.model

import com.google.gson.annotations.SerializedName

data class Event(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("date")
    val date: String,           // Format: YYYY-MM-DD

    @SerializedName("start_time")
    val startTime: String,      // Format: HH:mm

    @SerializedName("end_time")
    val endTime: String,        // Format: HH:mm

    @SerializedName("duration_minutes")
    val durationMinutes: Int,

    @SerializedName("category")
    val category: String,

    @SerializedName("color")
    val color: String,

    @SerializedName("is_repeating")
    val isRepeating: Boolean = false
)