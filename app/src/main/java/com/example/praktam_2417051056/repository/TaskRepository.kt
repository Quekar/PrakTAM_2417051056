package com.example.praktam_2417051056.repository

import com.example.praktam_2417051056.local.LocalDataStore
import com.example.praktam_2417051056.model.Task
import com.example.praktam_2417051056.network.RetrofitClient

class TaskRepository(private val store: LocalDataStore) {

    suspend fun getTasks(): List<Task> = store.getTasks()

    suspend fun seedFromApiIfEmpty() {
        if (store.isTasksEmpty()) {
            val apiData = RetrofitClient.instance.getTasks()
            store.saveTasks(apiData)
        }
    }

    suspend fun addTask(task: Task) {
        val current = store.getTasks().toMutableList()
        val newId   = (current.maxOfOrNull { it.id } ?: 0) + 1
        current.add(task.copy(id = newId))
        store.saveTasks(current)
    }

    suspend fun updateTask(task: Task) {
        val updated = store.getTasks().map {
            if (it.id == task.id) task else it
        }
        store.saveTasks(updated)
    }

    suspend fun deleteTask(id: Int) {
        val updated = store.getTasks().filter { it.id != id }
        store.saveTasks(updated)
    }
}