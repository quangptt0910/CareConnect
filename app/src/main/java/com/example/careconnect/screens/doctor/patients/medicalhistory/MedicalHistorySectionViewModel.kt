package com.example.careconnect.screens.doctor.patients.medicalhistory

import androidx.lifecycle.viewModelScope
import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.PatientRepository
import com.example.careconnect.dataclass.Allergy
import com.example.careconnect.dataclass.Condition
import com.example.careconnect.dataclass.Immunization
import com.example.careconnect.dataclass.Medication
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.dataclass.Surgery
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MedicalHistorySectionViewModel @Inject constructor(
    private val patientRepository: PatientRepository
) : MainViewModel() {
    private val _patient = MutableStateFlow<Patient?>(null)
    val patient: StateFlow<Patient?> = _patient

    private val _medications = MutableStateFlow<List<Medication>>(emptyList())
    val medications: StateFlow<List<Medication>> = _medications

    private val _allergies = MutableStateFlow<List<Allergy>>(emptyList())
    val allergies: StateFlow<List<Allergy>> = _allergies

    private val _conditions = MutableStateFlow<List<Condition>>(emptyList())
    val conditions: StateFlow<List<Condition>> = _conditions

    private val _surgeries = MutableStateFlow<List<Surgery>>(emptyList())
    val surgeries: StateFlow<List<Surgery>> = _surgeries

    private val _immunizations = MutableStateFlow<List<Immunization>>(emptyList())
    val immunizations: StateFlow<List<Immunization>> = _immunizations

    fun loadMedications(patientId: String) {
        viewModelScope.launch {
            val medicationsData = patientRepository.setMedicines(patientId)
            _medications.value = medicationsData
        }
    }

    fun loadPatient(patientId: String) {
        viewModelScope.launch {
            val patientData = patientRepository.getPatientById(patientId)
            _patient.value = patientData
        }
    }

    suspend fun addMedication(patientId: String, medication: Medication) {
        patientRepository.addMedication(patientId, medication)
    }

    suspend fun addAllergy(patientId: String, allergy: Allergy) {
        patientRepository.addAllergy(patientId, allergy)
    }

    suspend fun loadAllergies(patientId: String) {
        viewModelScope.launch {
            val allergiesData = patientRepository.setAllergies(patientId)
            _allergies.value = allergiesData
        }
    }

    suspend fun addCondition(patientId: String, condition: Condition) {
        patientRepository.addCondition(patientId, condition)
    }

    suspend fun loadConditions(patientId: String) {
        viewModelScope.launch {
            val conditionsData = patientRepository.setConditions(patientId)
            _conditions.value = conditionsData
        }
    }

    suspend fun addSurgery(patientId: String, surgery: Surgery) {
        patientRepository.addSurgery(patientId, surgery)
    }

    suspend fun loadSurgeries(patientId: String) {
        viewModelScope.launch {
            val surgeriesData = patientRepository.setSurgeries(patientId)
            _surgeries.value = surgeriesData
        }
    }

    suspend fun addImmunization(patientId: String, immunization: Immunization) {
        patientRepository.addImmunization(patientId, immunization)
    }

    suspend fun loadImmunizations(patientId: String) {
        viewModelScope.launch {
            val immunizationsData = patientRepository.setImmunizations(patientId)
            _immunizations.value = immunizationsData
        }
    }

    suspend fun updateCondition(patientId: String, condition: Condition) {
        patientRepository.updateCondition(patientId, condition)
    }

    suspend fun updateImmunization(patientId: String, immunization: Immunization) {
        patientRepository.updateImmunization(patientId, immunization)
    }

    suspend fun updateSurgery(patientId: String, surgery: Surgery) {
        patientRepository.updateSurgery(patientId, surgery)
    }

    suspend fun updateSurgery(patientId: String, medication: Medication) {
        patientRepository.updateMedication(patientId, medication)
    }

    suspend fun updateAllergy(patientId: String, allergy: Allergy) {
        patientRepository.updateAllergy(patientId, allergy)
    }

    suspend fun updateMedication(patientId: String, medication: Medication) {
        patientRepository.updateMedication(patientId, medication)
    }

    suspend fun deleteMedication(patientId: String, medication: Medication) {
        patientRepository.deleteMedication(patientId, medication)
    }

    suspend fun deleteAllergy(patientId: String, allergy: Allergy) {
        patientRepository.deleteAllergy(patientId, allergy)
    }

    suspend fun deleteCondition(patientId: String, condition: Condition) {
        patientRepository.deleteCondition(patientId, condition)
    }

    suspend fun deleteSurgery(patientId: String, surgery: Surgery) {
        patientRepository.deleteSurgery(patientId, surgery)
    }

    suspend fun deleteImmunization(patientId: String, immunization: Immunization) {
        patientRepository.deleteImmunization(patientId, immunization)
    }
}