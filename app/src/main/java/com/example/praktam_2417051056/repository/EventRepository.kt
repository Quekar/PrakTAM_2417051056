package com.example.praktam_2417051056.repository

import com.example.praktam_2417051056.model.Event
import com.example.praktam_2417051056.network.RetrofitClient

class EventRepository {
    suspend fun getEvents(): List<Event> {
        return RetrofitClient.instance.getEvents()
    }
}