package com.example.careconnect.screens.doctor.profile

import androidx.lifecycle.viewModelScope
import com.example.careconnect.MainViewModel
import com.example.careconnect.R
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.data.repository.DoctorRepository
import com.example.careconnect.dataclass.SlotType
import com.example.careconnect.dataclass.SnackBarMessage
import com.example.careconnect.dataclass.TimeSlot
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Represents the UI state for the schedule screen.
 *
 * @property selectedDate The currently selected date.
 * @property slots The list of time slots for the selected date.
 * @property isLoading Indicates whether the slots data is currently loading.
 * @property dialogState State of the slot editing dialog (open/closed and the slot being edited).
 */
data class ScheduleUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val slots: List<TimeSlot> = emptyList(),
    val isLoading: Boolean = false,
    val dialogState: DialogState = DialogState(),
)

/**
 * Represents the state of the slot editing dialog.
 *
 * @property isOpen Whether the dialog is currently open.
 * @property editingSlot The [TimeSlot] being edited or null if adding a new slot.
 */
data class DialogState(
    val isOpen: Boolean = false,
    val editingSlot: TimeSlot? = null
)

/**
 * ViewModel responsible for managing doctor's schedule data.
 *
 * - Loads, adds, updates, and deletes time slots for the selected date.
 * - Manages dialog visibility and slot generation logic.
 * - Integrates with the [DoctorRepository] for data persistence.
 * - Handles error reporting via snack bar messages.
 *
 * @property repo The repository to fetch and manipulate schedule data.
 * @property authRepository Provides the current authenticated user ID.
 */
@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repo: DoctorRepository,
    authRepository: AuthRepository
): MainViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    private val _slots = MutableStateFlow<List<TimeSlot>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _dialogState = MutableStateFlow(DialogState())

    private var currentUserId =  authRepository.currentUserIdFlow.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    // Expose a single UI state combining all flows
    val uiState: StateFlow<ScheduleUiState> = combine(
        _selectedDate,
        _slots,
        _isLoading,
        _dialogState,
        currentUserId
    ) { date, slots, isLoading, dialogState, userId ->
        ScheduleUiState(
            selectedDate = date,
            slots = slots,
            isLoading = isLoading,
            dialogState = dialogState,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ScheduleUiState()
    )


    init {
        launchCatching {
            // Load only when doctor id is valid
            currentUserId.collect { userId ->
                if (userId!!.isNotEmpty()) {
                    loadSlotsFor(_selectedDate.value, userId)
                }
            }
        }
    }

    fun selectDate(date: LocalDate, showSnackBar: (SnackBarMessage) -> Unit) {
        _selectedDate.value = date
        val doctorId = currentUserId.value.toString()
        launchCatching(showSnackBar) {
            if (doctorId.isNotEmpty()) {
                loadSlotsFor(date, doctorId)
            }
        }
    }

    private suspend fun loadSlotsFor(date: LocalDate, doctorId: String) {
        _isLoading.value = true
        try {
            val schedule = repo.getScheduleForDate(doctorId, date)
            _slots.value = schedule
        } catch (e: Exception) {
            // Handle error - could emit to a UI error channel
            throw e
        } finally {
            _isLoading.value = false
        }
    }

    fun showSlotDialog(slot: TimeSlot?) {
        _dialogState.value = DialogState(isOpen = true, editingSlot = slot)
    }

    fun closeDialog() {
        _dialogState.value = DialogState(isOpen = false, editingSlot = null)
    }

    fun addOrUpdateSlot(slot: TimeSlot, showSnackBar: (SnackBarMessage) -> Unit) {
        val doctorId = currentUserId.value.toString()
        launchCatching(showSnackBar) {
            if (doctorId.isNotEmpty()) {

                val generatedSlots = generateTimeSlot(slot.startTime, slot.endTime, slot.appointmentMinutes, slot.slotType)
                //Delete exist slots in range
                repo.deleteSlotInRange(doctorId, _selectedDate.value, slot.startTime, slot.endTime)

                // Save all new slots
                generatedSlots.forEach { generatedSlot ->
                    repo.saveSlot(doctorId, _selectedDate.value, generatedSlot)
                }

                // Reload
                loadSlotsFor(_selectedDate.value, doctorId)
                closeDialog()
                showSnackBar(SnackBarMessage.IdMessage(R.string.slot_saved))
            } else {
                // Handle error - could emit to a UI error channel
                showSnackBar(SnackBarMessage.IdMessage(R.string.generic_error))
                throw Exception("Doctor ID is empty")
            }

        }
    }

    fun removeSlot(slot: TimeSlot, showSnackBar: (SnackBarMessage) -> Unit) {
        val doctorId = currentUserId.value.toString()
        launchCatching(showSnackBar) {
            if (doctorId.isNotEmpty()) {
                repo.deleteSlot(doctorId, _selectedDate.value, slot)
                loadSlotsFor(_selectedDate.value, doctorId)
                closeDialog()
                showSnackBar(SnackBarMessage.IdMessage(R.string.slot_deleted))
            } else {
                showSnackBar(SnackBarMessage.IdMessage(R.string.generic_error))
                throw Exception("Doctor ID is empty")
            }
        }
    }

    fun generateTimeSlot(startTime: String, endTime: String, appointmentMinutes: Int, slotType: SlotType = SlotType.CONSULT): List<TimeSlot> {
        val slots = mutableListOf<TimeSlot>()
        val formatter = DateTimeFormatter.ofPattern("H:mm")

        val start = LocalTime.parse(startTime, formatter)
        val end = LocalTime.parse(endTime, formatter)

        if (start.isAfter(end) || start == end) { return slots }

        var currentStart = start
        while (currentStart.isBefore(end)) {
            val currentEnd = currentStart.plusMinutes(appointmentMinutes.toLong())
            if (currentEnd.isAfter(end)) {
                break
            }
            slots.add(TimeSlot(formatter.format(currentStart), formatter.format(currentEnd), appointmentMinutes,slotType))
            currentStart = currentEnd
        }
        return slots

    }
}

