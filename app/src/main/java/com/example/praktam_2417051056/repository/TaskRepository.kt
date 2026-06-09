package com.example.praktam_2417051056.repository

import com.example.praktam_2417051056.model.Task
import com.example.praktam_2417051056.network.RetrofitClient

class TaskRepository {
    suspend fun getTasks(): List<Task> {
        return RetrofitClient.instance.getTasks()
    }
}