package com.example.careconnect.dataclass

import com.google.firebase.firestore.DocumentId

/*
 * Data class for patient medical history
 * Medical history - Information about the patient
 */

// Base interface for all medical history entries
interface MedicalHistoryEntry {
    val id: String
        get() = ""
    val type: String
        get() = ""
}

data class Allergy(
    @DocumentId override val id: String = "",
    override val type: String = "ALLERGY",
    val allergen: String = "",
    val reaction: String = "",
    val severity: String = "",
    val diagnosedDate: String = ""
) : MedicalHistoryEntry

data class Condition(
    @DocumentId override val id: String = "",
    override val type: String = "CONDITION",
    val name: String = "",
    val diagnosedDate: String = "",
    val status: String = ""
) : MedicalHistoryEntry

data class Medication(
    @DocumentId override val id: String = "",
    override val type: String = "MEDICATION",
    val name: String = "",
    val dosage: String = "",
    val frequency: String = "",
    val startDate: String = "",
    val endDate: String = "",
) : MedicalHistoryEntry

data class Surgery(
    @DocumentId override val id: String = "",
    override val type: String = "SURGERY",
    val surgeryName: String = "",
    val surgeryDate: String = "",
    val hospital: String = "",
    val notes: String = ""
) : MedicalHistoryEntry

data class Immunization(
    @DocumentId override val id: String = "",
    override val type: String = "IMMUNIZATION",
    val vaccineName: String = "",
    val dateAdministered: String = "",
    val administeredBy: String = "",
    val nextDueDate: String = ""
) : MedicalHistoryEntry

// Enum for medical history types to avoid string literals
enum class MedicalHistoryType(val value: String, val collectionName: String, val type: String) {
    ALLERGY("ALLERGY", "allergies", "Allergies"),
    CONDITION("CONDITION", "conditions", "Conditions"),
    MEDICATION("MEDICATION", "medications", "Medications"),
    SURGERY("SURGERY", "surgeries", "Surgeries"),
    IMMUNIZATION("IMMUNIZATION", "immunizations", "Immunizations");

    companion object {
        fun fromValue(value: String): MedicalHistoryType? = entries.find { it.value == value }
        fun fromCollectionName(name: String): MedicalHistoryType? = entries.find { it.collectionName == name }
        fun fromType(type: String): MedicalHistoryType? = entries.find { it.type == type }
    }
    fun displayName(): String = when (this) {
        ALLERGY -> "Allergies"
        CONDITION -> "Conditions"
        MEDICATION -> "Medications"
        SURGERY -> "Surgeries"
        IMMUNIZATION -> "Immunizations"
    }
}