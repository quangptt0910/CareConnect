package com.example.careconnect.screens.doctor.profile

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AddChatRoomRepository
import com.example.careconnect.data.repository.DoctorRepository
import com.example.careconnect.dataclass.Doctor
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@HiltViewModel
class DoctorProfileViewModel @Inject constructor(
    private val addChatRoomRepository: AddChatRoomRepository,
    private val doctorRepository: DoctorRepository
): MainViewModel() {
    private val _doctor = MutableStateFlow<Doctor?>(null)
    val doctor: StateFlow<Doctor?> = _doctor

    init {
        getDoctor()
    }

    fun getDoctor(){
        viewModelScope.launch {
            _doctor.value = addChatRoomRepository.getCurrentDoctor()
        }
    }

    fun updateDoctorPhoto(uri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference
        val fileName = "doctor_profilePhoto/${_doctor.value?.id}.jpg"
        val imageRef = storageRef.child(fileName)

        launchCatching {
            imageRef.putFile(uri).await()
            val downloadUrl = imageRef.downloadUrl.await()

            _doctor.value = _doctor.value?.copy(profilePhoto = downloadUrl.toString())
            doctorRepository.updateDoctor(_doctor.value!!)
        }
    }

}