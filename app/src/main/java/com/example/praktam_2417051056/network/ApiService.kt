package com.example.praktam_2417051056.network

import com.example.praktam_2417051056.model.Event
import com.example.praktam_2417051056.model.Task
import retrofit2.http.GET

interface ApiService {

    @GET("events.json")
    suspend fun getEvents(): List<Event>

    @GET("tasks.json")
    suspend fun getTasks(): List<Task>
}