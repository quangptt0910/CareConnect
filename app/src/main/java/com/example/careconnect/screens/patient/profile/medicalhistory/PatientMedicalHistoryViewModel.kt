package com.example.careconnect.screens.patient.profile.medicalhistory

import android.util.Log
import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.data.repository.PatientRepository
import com.example.careconnect.dataclass.Allergy
import com.example.careconnect.dataclass.Condition
import com.example.careconnect.dataclass.Immunization
import com.example.careconnect.dataclass.MedicalHistoryEntry
import com.example.careconnect.dataclass.MedicalHistoryType
import com.example.careconnect.dataclass.Medication
import com.example.careconnect.dataclass.Surgery
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class PatientMedicalHistoryViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
    private val authRepository: AuthRepository
) : MainViewModel() {
    private val _patientId = MutableStateFlow<String?>(null)
    val patientId: StateFlow<String?> = _patientId

    private val _entries = MutableStateFlow<List<MedicalHistoryEntry>>(emptyList())
    val entries: StateFlow<List<MedicalHistoryEntry>> = _entries

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        _patientId.value = authRepository.currentUser?.uid
    }

    fun loadEntries(patientId: String, type: MedicalHistoryType) {
        launchCatching {
            _isLoading.value = true
            try {
                val data = when (type) {
                    MedicalHistoryType.MEDICATION -> patientRepository.getMedications(patientId)
                    MedicalHistoryType.ALLERGY -> patientRepository.getAllergies(patientId)
                    MedicalHistoryType.CONDITION -> patientRepository.getConditions(patientId)
                    MedicalHistoryType.SURGERY -> patientRepository.getSurgeries(patientId)
                    MedicalHistoryType.IMMUNIZATION -> patientRepository.getImmunizations(patientId)
                }
                _entries.value = data
            } catch (e: Exception) {
                Log.e("MedicalHistorySectionViewModel", "Error loading entries", e)
                _entries.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }


    // Generic add function
    suspend fun addEntry(patientId: String, entry: MedicalHistoryEntry): String? {
        return try {
            when (entry) {
                is Medication -> patientRepository.addMedication(patientId, entry)
                is Allergy -> patientRepository.addAllergy(patientId, entry)
                is Condition -> patientRepository.addCondition(patientId, entry)
                is Surgery -> patientRepository.addSurgery(patientId, entry)
                is Immunization -> patientRepository.addImmunization(patientId, entry)
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    // Generic update function
    suspend fun updateEntry(patientId: String, entry: MedicalHistoryEntry): Boolean {
        return try {
            when (entry) {
                is Medication -> patientRepository.updateMedication(patientId, entry)
                is Allergy -> patientRepository.updateAllergy(patientId, entry)
                is Condition -> patientRepository.updateCondition(patientId, entry)
                is Surgery -> patientRepository.updateSurgery(patientId, entry)
                is Immunization -> patientRepository.updateImmunization(patientId, entry)
                else -> false
            }
        } catch (e: Exception) {
            false
        }
    }

    // Generic delete function
    suspend fun deleteEntry(patientId: String, entry: MedicalHistoryEntry): Boolean {
        return try {
            when (entry) {
                is Medication -> patientRepository.deleteMedication(patientId, entry)
                is Allergy -> patientRepository.deleteAllergy(patientId, entry)
                is Condition -> patientRepository.deleteCondition(patientId, entry)
                is Surgery -> patientRepository.deleteSurgery(patientId, entry)
                is Immunization -> patientRepository.deleteImmunization(patientId, entry)
                else -> false
            }
        } catch (e: Exception) {
            false
        }
    }
}