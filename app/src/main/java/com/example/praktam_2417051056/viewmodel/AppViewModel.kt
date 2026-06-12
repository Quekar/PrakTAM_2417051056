package com.example.praktam_2417051056.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.praktam_2417051056.local.LocalDataStore
import com.example.praktam_2417051056.model.Event
import com.example.praktam_2417051056.model.Task
import com.example.praktam_2417051056.notification.NotificationScheduler
import com.example.praktam_2417051056.repository.EventRepository
import com.example.praktam_2417051056.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val ctx             = application.applicationContext
    private val store           = LocalDataStore(application)
    private val eventRepository = EventRepository(store)
    private val taskRepository  = TaskRepository(store)

    private val _eventState = MutableStateFlow<UiState<List<Event>>>(UiState.Loading)
    val eventState: StateFlow<UiState<List<Event>>> = _eventState.asStateFlow()

    private val _taskState = MutableStateFlow<UiState<List<Task>>>(UiState.Loading)
    val taskState: StateFlow<UiState<List<Task>>> = _taskState.asStateFlow()

    init {
        fetchEvents()
        fetchTasks()
    }

    fun fetchEvents() {
        viewModelScope.launch {
            _eventState.value = UiState.Loading
            try {
                eventRepository.seedFromApiIfEmpty()
                val events = eventRepository.getEvents()
                _eventState.value = UiState.Success(events)
                NotificationScheduler.scheduleAllEvents(ctx, events)
            } catch (e: Exception) {
                try {
                    val events = eventRepository.getEvents()
                    _eventState.value = UiState.Success(events)
                    NotificationScheduler.scheduleAllEvents(ctx, events)
                } catch (e2: Exception) {
                    _eventState.value = UiState.Error(
                        "Gagal memuat jadwal. Periksa koneksi internet kamu."
                    )
                }
            }
        }
    }

    fun fetchTasks() {
        viewModelScope.launch {
            _taskState.value = UiState.Loading
            try {
                taskRepository.seedFromApiIfEmpty()
                val tasks = taskRepository.getTasks()
                _taskState.value = UiState.Success(tasks)
                NotificationScheduler.scheduleAllTasks(ctx, tasks)
            } catch (e: Exception) {
                try {
                    val tasks = taskRepository.getTasks()
                    _taskState.value = UiState.Success(tasks)
                    NotificationScheduler.scheduleAllTasks(ctx, tasks)
                } catch (e2: Exception) {
                    _taskState.value = UiState.Error(
                        "Gagal memuat task. Periksa koneksi internet kamu."
                    )
                }
            }
        }
    }

    fun addEvent(
        title: String, date: String, startTime: String, endTime: String,
        category: String, color: String, description: String
    ) {
        viewModelScope.launch {
            val durationMinutes = calcDuration(startTime, endTime)
            val newEvent = Event(
                id = 0, title = title, description = description,
                date = date, startTime = startTime, endTime = endTime,
                durationMinutes = durationMinutes, category = category, color = color
            )
            eventRepository.addEvent(newEvent)
            val events = eventRepository.getEvents()
            _eventState.value = UiState.Success(events)
            events.maxByOrNull { it.id }?.let {
                NotificationScheduler.scheduleEventReminders(ctx, it)
            }
        }
    }

    fun deleteEvent(id: Int) {
        viewModelScope.launch {
            NotificationScheduler.cancelEventReminders(ctx, id)
            eventRepository.deleteEvent(id)
            _eventState.value = UiState.Success(eventRepository.getEvents())
        }
    }

    fun editEvent(
        id: Int, title: String, date: String, startTime: String, endTime: String,
        category: String, color: String, description: String
    ) {
        viewModelScope.launch {
            val durationMinutes = calcDuration(startTime, endTime)
            val updated = Event(
                id = id, title = title, description = description,
                date = date, startTime = startTime, endTime = endTime,
                durationMinutes = durationMinutes, category = category, color = color
            )
            eventRepository.updateEvent(updated)
            _eventState.value = UiState.Success(eventRepository.getEvents())
            NotificationScheduler.scheduleEventReminders(ctx, updated)
        }
    }

    fun addTask(
        title: String, description: String, category: String,
        priority: Int, deadline: String, createdAt: String
    ) {
        viewModelScope.launch {
            val newTask = Task(
                id = 0, title = title, description = description,
                category = category, priority = priority,
                deadline = deadline, isDone = false, createdAt = createdAt
            )
            taskRepository.addTask(newTask)
            val tasks = taskRepository.getTasks()
            _taskState.value = UiState.Success(tasks)
            tasks.maxByOrNull { it.id }?.let {
                NotificationScheduler.scheduleTaskReminders(ctx, it)
            }
        }
    }

    fun deleteTask(id: Int) {
        viewModelScope.launch {
            NotificationScheduler.cancelTaskReminders(ctx, id)
            taskRepository.deleteTask(id)
            _taskState.value = UiState.Success(taskRepository.getTasks())
        }
    }

    fun editTask(
        id: Int, title: String, description: String,
        category: String, priority: Int, deadline: String
    ) {
        viewModelScope.launch {
            val current = taskRepository.getTasks().find { it.id == id } ?: return@launch
            val updated = current.copy(
                title = title, description = description,
                category = category, priority = priority, deadline = deadline
            )
            taskRepository.updateTask(updated)
            _taskState.value = UiState.Success(taskRepository.getTasks())
            NotificationScheduler.scheduleTaskReminders(ctx, updated)
        }
    }

    fun updateTaskList(updatedList: List<Task>) {
        viewModelScope.launch {
            val current = (_taskState.value as? UiState.Success)?.data ?: return@launch
            updatedList.forEach { updated ->
                val original = current.find { it.id == updated.id }
                if (original?.isDone != updated.isDone) {
                    taskRepository.updateTask(updated)
                    if (updated.isDone) {
                        NotificationScheduler.cancelTaskReminders(ctx, updated.id)
                    } else {
                        NotificationScheduler.scheduleTaskReminders(ctx, updated)
                    }
                }
            }
            _taskState.value = UiState.Success(taskRepository.getTasks())
        }
    }

    private fun calcDuration(startTime: String, endTime: String): Int {
        return try {
            val (sh, sm) = startTime.split(":").map { it.toInt() }
            val (eh, em) = endTime.split(":").map { it.toInt() }
            ((eh * 60 + em) - (sh * 60 + sm)).coerceAtLeast(0)
        } catch (e: Exception) { 0 }
    }
}