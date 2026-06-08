package com.example.praktam_2417051056.model

import com.google.gson.annotations.SerializedName

data class Task(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String = "",

    @SerializedName("category")
    val category: String,

    @SerializedName("priority")
    val priority: Int,

    @SerializedName("deadline")
    val deadline: String,       // Format: YYYY-MM-DD HH:mm

    @SerializedName("is_done")
    val isDone: Boolean = false,

    @SerializedName("created_at")
    val createdAt: String       // Format: YYYY-MM-DD HH:mm
)