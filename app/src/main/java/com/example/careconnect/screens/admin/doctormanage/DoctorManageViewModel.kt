package com.example.careconnect.screens.admin.doctormanage

import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.DoctorRepository
import com.example.careconnect.dataclass.Doctor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


sealed interface DoctorManageUiState {
    data class Success(val doctors: List<Doctor>) : DoctorManageUiState
    object Error : DoctorManageUiState
    object Loading : DoctorManageUiState
}

@HiltViewModel
class DoctorManageViewModel @Inject constructor(
    private val doctorRepository: DoctorRepository
): MainViewModel() {

    private val _navigateToAddDoctor = MutableStateFlow(false)
    val navigateToAddDoctor: StateFlow<Boolean>
        get() = _navigateToAddDoctor.asStateFlow()

    private val _doctorsList = MutableStateFlow<List<Doctor>>(emptyList())
    val doctorsList: StateFlow<List<Doctor>>
        get() = _doctorsList.asStateFlow()

//    private val _isLoadingDoctors = MutableStateFlow(true)
//    val isLoadingDoctors: StateFlow<Boolean>
//        get() = _isLoadingDoctors.asStateFlow()

    val allDoctors = doctorRepository.getAllDoctorsFlow()

    fun loadDoctors() {
        launchCatching {
            _doctorsList.value = doctorRepository.getAllDoctors()
        }
    }
}