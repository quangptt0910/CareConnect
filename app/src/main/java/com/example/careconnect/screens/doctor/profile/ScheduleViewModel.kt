package com.example.careconnect.screens.doctor.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.DoctorRepository
import com.example.careconnect.dataclass.TimeSlot
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repo: DoctorRepository
): MainViewModel() {
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    private val _slots = MutableStateFlow<List<TimeSlot>>(emptyList())
    val slots: StateFlow<List<TimeSlot>> = _slots

    private var currentUserId =  "123"

    var dialogState by mutableStateOf(DialogState(isOpen = false, editingSlot = null))
        private set

    init {
        loadSlotsFor(_selectedDate.value)
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        loadSlotsFor(date)
    }

    private fun loadSlotsFor(date: LocalDate) {
        viewModelScope.launch {
            val schedule = repo.getScheduleForDate(currentUserId, date)
            _slots.value = schedule
        }
    }

    fun showSlotDialog(slot: TimeSlot?) {
        dialogState = DialogState(isOpen = true, editingSlot = slot)
    }

    fun closeDialog() {
        dialogState = DialogState(isOpen = false, editingSlot = null)
    }

    fun addOrUpdateSlot(date: LocalDate, slot: TimeSlot) {
        viewModelScope.launch {
            repo.saveSlot(currentUserId, date, slot)
            loadSlotsFor(date)
        }
    }

    fun removeSlot(date: LocalDate, slot: TimeSlot) {
        viewModelScope.launch {
            repo.deleteSlot(currentUserId, date, slot)
            loadSlotsFor(date)
        }
    }
}

data class DialogState(
    val isOpen: Boolean,
    val editingSlot: TimeSlot?
)