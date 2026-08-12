package com.dailyroutine.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailyroutine.app.data.model.RoutineColor
import com.dailyroutine.app.data.model.RoutineCategory
import com.dailyroutine.app.data.model.RoutineItem
import com.dailyroutine.app.data.model.RoutineType
import com.dailyroutine.app.data.repository.RoutineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Shared across every tab and overlay: owns the live routine list from Firestore so Home,
 * Calendar, Stats and the Detail/Form screens all observe the same source of truth.
 */
class RoutineViewModel @JvmOverloads constructor(
    private val repository: RoutineRepository = RoutineRepository(),
) : ViewModel() {

    val routines: StateFlow<List<RoutineItem>> = repository.observeRoutines()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    fun toggleToday(item: RoutineItem) {
        viewModelScope.launch { repository.toggleToday(item) }
    }

    fun addRoutine(
        title: String,
        type: RoutineType,
        category: RoutineCategory,
        time: String,
        color: RoutineColor,
    ) {
        viewModelScope.launch {
            _isSaving.value = true
            repository.addRoutine(
                RoutineItem(title = title, type = type, category = category, time = time, color = color)
            )
            _isSaving.value = false
        }
    }

    fun updateRoutine(
        original: RoutineItem,
        title: String,
        type: RoutineType,
        category: RoutineCategory,
        time: String,
        color: RoutineColor,
    ) {
        viewModelScope.launch {
            _isSaving.value = true
            repository.updateRoutine(
                original.copy(title = title, type = type, category = category, time = time, color = color)
            )
            _isSaving.value = false
        }
    }

    fun deleteRoutine(id: String) {
        viewModelScope.launch { repository.deleteRoutine(id) }
    }
}
