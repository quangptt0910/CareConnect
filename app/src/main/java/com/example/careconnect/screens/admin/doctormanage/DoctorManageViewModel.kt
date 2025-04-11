package com.example.careconnect.screens.admin.doctormanage

import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.DoctorRepository
import com.example.careconnect.dataclass.Doctor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

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

    private val _isLoadingDoctors = MutableStateFlow(false)
    val isLoadingDoctors: StateFlow<Boolean>
        get() = _isLoadingDoctors.asStateFlow()

    init {
        loadDoctors()
    }

    fun loadDoctors() {
        launchCatching {
            _isLoadingDoctors.value = true
            _doctorsList.value = doctorRepository.getDoctors()
        }
        _isLoadingDoctors.value = false
    }

}