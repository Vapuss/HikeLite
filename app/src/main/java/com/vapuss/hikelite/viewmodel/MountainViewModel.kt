package com.vapuss.hikelite.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vapuss.hikelite.data.local.AppDatabase
import com.vapuss.hikelite.data.model.NoteEntity
import com.vapuss.hikelite.data.model.Trail
import com.vapuss.hikelite.data.model.WeatherResponse
import com.vapuss.hikelite.data.repository.MountainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MountainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MountainRepository(
        AppDatabase.getInstance(application).noteDao()
    )

    // --- Trails (static data) ---
    val trails: List<Trail> = repository.trails

    // --- Weather ---
    private val _weatherState = MutableStateFlow<WeatherResponse?>(null)
    val weatherState: StateFlow<WeatherResponse?> = _weatherState

    private val _isLoadingWeather = MutableStateFlow(false)
    val isLoadingWeather: StateFlow<Boolean> = _isLoadingWeather

    // --- Selected mountain ---
    private val _selectedMountain = MutableStateFlow<Trail?>(null)
    val selectedMountain: StateFlow<Trail?> = _selectedMountain

    // --- Dark Mode ---
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    // --- Notes ---
    private val _notes = MutableStateFlow<List<NoteEntity>>(emptyList())
    val notes: StateFlow<List<NoteEntity>> = _notes

    // --- Public functions ---

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun selectTrail(trail: Trail) {
        _selectedMountain.value = trail
        fetchWeather(trail.name)
    }

    fun clearSelection() {
        _selectedMountain.value = null
        _weatherState.value = null
    }

    // Network call explicitly on Dispatchers.IO
    fun fetchWeather(mountainName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoadingWeather.value = true
            try {
                val result = repository.getWeather(mountainName)
                _weatherState.value = result
            } catch (e: Exception) {
                _weatherState.value = WeatherResponse("N/A", "Connection error", "Unknown")
            } finally {
                _isLoadingWeather.value = false
            }
        }
    }

    // Room read on Dispatchers.IO
    fun loadNotes(mountainName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getNotesForMountain(mountainName).collect { list ->
                _notes.value = list
            }
        }
    }

    // Room write on Dispatchers.IO
    fun saveNote(mountainName: String, text: String) {
        val sanitized = sanitizeInput(text)
        if (sanitized.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveNote(
                NoteEntity(mountainName = mountainName, textContent = sanitized)
            )
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNote(note)
        }
    }

    // Input sanitization: prevents SQL injection, trims whitespace
    private fun sanitizeInput(input: String): String {
        return input
            .replace("'", "''")
            .replace("\"", "\\\"")
            .replace("--", "")
            .replace(";", "")
            .replace("/*", "")
            .replace("*/", "")
            .trim()
    }
}
