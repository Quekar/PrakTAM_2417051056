package com.example.praktam_2417051056.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.praktam_2417051056.model.Event
import com.example.praktam_2417051056.model.Task
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

class AppViewModel(
    private val eventRepository: EventRepository = EventRepository(),
    private val taskRepository: TaskRepository   = TaskRepository()
) : ViewModel() {

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
                val data = eventRepository.getEvents()
                _eventState.value = UiState.Success(data)
            } catch (e: Exception) {
                _eventState.value = UiState.Error(
                    "Gagal memuat jadwal. Periksa koneksi internet kamu."
                )
            }
        }
    }

    fun fetchTasks() {
        viewModelScope.launch {
            _taskState.value = UiState.Loading
            try {
                val data = taskRepository.getTasks()
                _taskState.value = UiState.Success(data)
            } catch (e: Exception) {
                _taskState.value = UiState.Error(
                    "Gagal memuat task. Periksa koneksi internet kamu."
                )
            }
        }
    }

    fun addEvent(
        title: String,
        date: String,
        startTime: String,
        endTime: String,
        category: String,
        color: String,
        description: String
    ) {
        val current = (_eventState.value as? UiState.Success)?.data ?: return
        val newId   = (current.maxOfOrNull { it.id } ?: 0) + 1
        val durationMinutes = try {
            val (sh, sm) = startTime.split(":").map { it.toInt() }
            val (eh, em) = endTime.split(":").map { it.toInt() }
            ((eh * 60 + em) - (sh * 60 + sm)).coerceAtLeast(0)
        } catch (e: Exception) { 0 }

        val newEvent = Event(
            id              = newId,
            title           = title,
            description     = description,
            date            = date,
            startTime       = startTime,
            endTime         = endTime,
            durationMinutes = durationMinutes,
            category        = category,
            color           = color
        )
        _eventState.value = UiState.Success(current + newEvent)
    }

    fun addTask(
        title: String,
        description: String,
        category: String,
        priority: Int,
        deadline: String,
        createdAt: String
    ) {
        val current = (_taskState.value as? UiState.Success)?.data ?: return
        val newId   = (current.maxOfOrNull { it.id } ?: 0) + 1
        val newTask = Task(
            id          = newId,
            title       = title,
            description = description,
            category    = category,
            priority    = priority,
            deadline    = deadline,
            isDone      = false,
            createdAt   = createdAt
        )
        _taskState.value = UiState.Success(current + newTask)
    }

    fun deleteEvent(id: Int) {
        val current = (_eventState.value as? UiState.Success)?.data ?: return
        _eventState.value = UiState.Success(current.filter { it.id != id })
    }

    fun editEvent(
        id: Int,
        title: String,
        date: String,
        startTime: String,
        endTime: String,
        category: String,
        color: String,
        description: String
    ) {
        val current = (_eventState.value as? UiState.Success)?.data ?: return
        val durationMinutes = try {
            val (sh, sm) = startTime.split(":").map { it.toInt() }
            val (eh, em) = endTime.split(":").map { it.toInt() }
            ((eh * 60 + em) - (sh * 60 + sm)).coerceAtLeast(0)
        } catch (e: Exception) { 0 }

        _eventState.value = UiState.Success(
            current.map { event ->
                if (event.id == id) event.copy(
                    title           = title,
                    date            = date,
                    startTime       = startTime,
                    endTime         = endTime,
                    durationMinutes = durationMinutes,
                    category        = category,
                    color           = color,
                    description     = description
                ) else event
            }
        )
    }

    fun deleteTask(id: Int) {
        val current = (_taskState.value as? UiState.Success)?.data ?: return
        _taskState.value = UiState.Success(current.filter { it.id != id })
    }

    fun editTask(
        id: Int,
        title: String,
        description: String,
        category: String,
        priority: Int,
        deadline: String
    ) {
        val current = (_taskState.value as? UiState.Success)?.data ?: return
        _taskState.value = UiState.Success(
            current.map { task ->
                if (task.id == id) task.copy(
                    title       = title,
                    description = description,
                    category    = category,
                    priority    = priority,
                    deadline    = deadline
                ) else task
            }
        )
    }

    fun updateTaskList(updatedList: List<Task>) {
        _taskState.value = UiState.Success(updatedList)
    }
}