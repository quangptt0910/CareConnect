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

/**
 * ViewModel responsible for handling the doctor's profile data.
 *
 * - Retrieves current doctor data from repository.
 * - Updates doctor's profile photo by uploading to Firebase Storage.
 * - Persists updated doctor info in the repository.
 *
 * @property addChatRoomRepository Repository for chat room and current doctor info.
 * @property doctorRepository Repository for updating doctor information.
 */
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

    /**
     * Loads the current doctor data from the repository and updates [_doctor].
     */
    fun getDoctor(){
        viewModelScope.launch {
            _doctor.value = addChatRoomRepository.getCurrentDoctor()
        }
    }

    /**
     * Uploads a new profile photo to Firebase Storage and updates the doctor profile.
     *
     * @param uri The URI of the selected image to upload.
     */
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

    /**
     * Updates the doctor data both locally and in the repository.
     *
     * @param updateDoctor The updated doctor information to save.
     */
    fun updateDoctor(updateDoctor: Doctor) {
        _doctor.value = updateDoctor
        viewModelScope.launch {
            doctorRepository.updateDoctor(updateDoctor)
        }
    }

}