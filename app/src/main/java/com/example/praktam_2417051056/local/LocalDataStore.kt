package com.example.praktam_2417051056.local

import android.content.Context
import com.example.praktam_2417051056.model.Event
import com.example.praktam_2417051056.model.Task
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class LocalDataStore(private val context: Context) {

    private val gson       = Gson()
    private val eventsFile get() = File(context.filesDir, "events.json")
    private val tasksFile  get() = File(context.filesDir, "tasks.json")

    suspend fun getEvents(): List<Event> = withContext(Dispatchers.IO) {
        if (!eventsFile.exists()) return@withContext emptyList()
        return@withContext try {
            val json = eventsFile.readText()
            val type = object : TypeToken<List<Event>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveEvents(events: List<Event>) = withContext(Dispatchers.IO) {
        eventsFile.writeText(gson.toJson(events))
    }

    suspend fun isEventsEmpty(): Boolean = getEvents().isEmpty()

    suspend fun getTasks(): List<Task> = withContext(Dispatchers.IO) {
        if (!tasksFile.exists()) return@withContext emptyList()
        return@withContext try {
            val json = tasksFile.readText()
            val type = object : TypeToken<List<Task>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveTasks(tasks: List<Task>) = withContext(Dispatchers.IO) {
        tasksFile.writeText(gson.toJson(tasks))
    }

    suspend fun isTasksEmpty(): Boolean = getTasks().isEmpty()
}