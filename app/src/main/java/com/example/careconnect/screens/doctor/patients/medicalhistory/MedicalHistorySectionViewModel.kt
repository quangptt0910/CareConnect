package com.example.careconnect.screens.doctor.patients.medicalhistory

import android.util.Log
import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.PatientRepository
import com.example.careconnect.dataclass.Allergy
import com.example.careconnect.dataclass.Condition
import com.example.careconnect.dataclass.Immunization
import com.example.careconnect.dataclass.MedicalHistoryEntry
import com.example.careconnect.dataclass.MedicalHistoryType
import com.example.careconnect.dataclass.Medication
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.dataclass.Surgery
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


/**
 * ViewModel responsible for managing and providing medical history data
 * for a selected patient. It interacts with the [PatientRepository] to load,
 * add, update, and delete entries of various medical history types.
 *
 * @property patientRepository Repository instance used for accessing patient data.
 */
@HiltViewModel
class MedicalHistorySectionViewModel @Inject constructor(
    private val patientRepository: PatientRepository
) : MainViewModel() {
    private val _patient = MutableStateFlow<Patient?>(null)
    val patient: StateFlow<Patient?> = _patient

    private val _entries = MutableStateFlow<List<MedicalHistoryEntry>>(emptyList())
    val entries: StateFlow<List<MedicalHistoryEntry>> = _entries

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading


    /**
     * Loads patient details by patient ID.
     *
     * @param patientId Unique identifier of the patient to be loaded.
     */
    fun loadPatient(patientId: String) {
        launchCatching {
            _isLoading.value = true
            try {
                val patientData = patientRepository.getPatientById(patientId)
                _patient.value = patientData
            } catch (e: Exception) {

            } finally {
                _isLoading.value = false
            }

        }
    }

    /**
     * Loads a list of medical history entries of a specific type for the given patient.
     *
     * @param patientId Unique identifier of the patient whose entries are to be loaded.
     * @param type The type of medical history entries to retrieve.
     */
    //  function to load entries by type
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

    /**
     * Adds a new medical history entry for the specified patient.
     *
     * @param patientId Unique identifier of the patient.
     * @param entry The medical history entry to add.
     * @return The ID of the newly added entry if successful, or null otherwise.
     */
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

    /**
     * Updates an existing medical history entry for the specified patient.
     *
     * @param patientId Unique identifier of the patient.
     * @param entry The medical history entry to update.
     * @return True if the update was successful, false otherwise.
     */
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

    /**
     * Deletes an existing medical history entry for the specified patient.
     *
     * @param patientId Unique identifier of the patient.
     * @param entry The medical history entry to delete.
     * @return True if the deletion was successful, false otherwise.
     */
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

    /**
     * Refreshes the current list of medical history entries based on the patient ID and entry type.
     *
     * @param patientId Unique identifier of the patient.
     * @param type The type of medical history entries to refresh.
     */
    fun refreshEntries(patientId: String, type: MedicalHistoryType) {
        loadEntries(patientId, type)
    }

}