package com.example.praktam_2417051056.model

data class Task(
    val id: Int,
    val title: String,
    val description: String = "",
    val category: String,
    val priority: Int,
    val deadline: String,       // Format: YYYY-MM-DD HH:mm
    val isDone: Boolean = false,
    val createdAt: String       // Format: YYYY-MM-DD HH:mm
)