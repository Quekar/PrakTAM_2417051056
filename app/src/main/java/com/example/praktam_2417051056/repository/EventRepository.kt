package com.example.praktam_2417051056.repository

import com.example.praktam_2417051056.local.LocalDataStore
import com.example.praktam_2417051056.model.Event
import com.example.praktam_2417051056.network.RetrofitClient

class EventRepository(private val store: LocalDataStore) {

    suspend fun getEvents(): List<Event> = store.getEvents()

    suspend fun seedFromApiIfEmpty() {
        if (store.isEventsEmpty()) {
            val apiData = RetrofitClient.instance.getEvents()
            store.saveEvents(apiData)
        }
    }

    suspend fun addEvent(event: Event) {
        val current = store.getEvents().toMutableList()
        val newId   = (current.maxOfOrNull { it.id } ?: 0) + 1
        current.add(event.copy(id = newId))
        store.saveEvents(current)
    }

    suspend fun updateEvent(event: Event) {
        val updated = store.getEvents().map {
            if (it.id == event.id) event else it
        }
        store.saveEvents(updated)
    }

    suspend fun deleteEvent(id: Int) {
        val updated = store.getEvents().filter { it.id != id }
        store.saveEvents(updated)
    }
}