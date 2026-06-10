package com.example.praktam_2417051056.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.praktam_2417051056.model.Event
import com.example.praktam_2417051056.model.Task
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "dailydo_store")

class LocalDataStore(private val context: Context) {

    private val gson = Gson()

    private val EVENTS_KEY = stringPreferencesKey("events")
    private val TASKS_KEY  = stringPreferencesKey("tasks")


    suspend fun getEvents(): List<Event> {
        val prefs = context.dataStore.data.first()
        val json  = prefs[EVENTS_KEY] ?: return emptyList()
        return try {
            val type = object : TypeToken<List<Event>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveEvents(events: List<Event>) {
        context.dataStore.edit { prefs ->
            prefs[EVENTS_KEY] = gson.toJson(events)
        }
    }

    suspend fun isEventsEmpty(): Boolean = getEvents().isEmpty()

    suspend fun getTasks(): List<Task> {
        val prefs = context.dataStore.data.first()
        val json  = prefs[TASKS_KEY] ?: return emptyList()
        return try {
            val type = object : TypeToken<List<Task>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveTasks(tasks: List<Task>) {
        context.dataStore.edit { prefs ->
            prefs[TASKS_KEY] = gson.toJson(tasks)
        }
    }

    suspend fun isTasksEmpty(): Boolean = getTasks().isEmpty()
}