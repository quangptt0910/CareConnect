package com.example.careconnect.dataclass

import com.google.firebase.firestore.DocumentId

/*
 * Data class for patient medical history
 * Medical history - Information about the patient
 */

// Base interface for all medical history entries
/**
 * Marker interface for all medical history entries (Allergy, Condition, etc.).
 */
interface MedicalHistoryEntry {
    val id: String
        get() = ""
    val type: String
        get() = ""
}


/**
 * Represents a known allergy in a patient's medical history.
 *
 * @property id Unique ID for this allergy entry.
 * @property type Fixed type string: "ALLERGY".
 * @property allergen Allergen substance (e.g., "Peanuts").
 * @property reaction Reaction experienced (e.g., "Anaphylaxis").
 * @property severity Severity of the reaction.
 * @property diagnosedDate Date when the allergy was diagnosed.
 */
data class Allergy(
    @DocumentId override val id: String = "",
    override val type: String = "ALLERGY",
    val allergen: String = "",
    val reaction: String = "",
    val severity: String = "",
    val diagnosedDate: String = ""
) : MedicalHistoryEntry

/**
 * Represents a chronic or acute condition in the patient's medical history.
 *
 * @property id Unique ID for this condition entry.
 * @property type Fixed type string: "CONDITION".
 * @property name Name of the condition.
 * @property diagnosedDate Date of diagnosis.
 * @property status Current status of the condition (e.g., "Ongoing", "Resolved").
 */
data class Condition(
    @DocumentId override val id: String = "",
    override val type: String = "CONDITION",
    val name: String = "",
    val diagnosedDate: String = "",
    val status: String = ""
) : MedicalHistoryEntry

/**
 * Represents a prescribed or over-the-counter medication in the patient's history.
 *
 * @property id Unique ID for this medication entry.
 * @property type Fixed type string: "MEDICATION".
 * @property name Name of the medication.
 * @property dosage Dosage details.
 * @property frequency How often the medication is taken.
 * @property startDate Start date of the medication.
 * @property endDate End date or expected end date.
 */
data class Medication(
    @DocumentId override val id: String = "",
    override val type: String = "MEDICATION",
    val name: String = "",
    val dosage: String = "",
    val frequency: String = "",
    val startDate: String = "",
    val endDate: String = "",
) : MedicalHistoryEntry

/**
 * Represents a surgery the patient has undergone.
 *
 * @property id Unique ID for this surgery entry.
 * @property type Fixed type string: "SURGERY".
 * @property surgeryName Name of the surgery performed.
 * @property surgeryDate Date of the surgery.
 * @property hospital Hospital where the surgery was performed.
 * @property notes Additional notes or details.
 */
data class Surgery(
    @DocumentId override val id: String = "",
    override val type: String = "SURGERY",
    val surgeryName: String = "",
    val surgeryDate: String = "",
    val hospital: String = "",
    val notes: String = ""
) : MedicalHistoryEntry

/**
 * Represents an immunization record in a patient's history.
 *
 * @property id Unique ID for this immunization entry.
 * @property type Fixed type string: "IMMUNIZATION".
 * @property vaccineName Name of the vaccine.
 * @property dateAdministered Date when the vaccine was given.
 * @property administeredBy Name of the person or clinic that administered the vaccine.
 * @property nextDueDate Optional due date for the next dose.
 */
data class Immunization(
    @DocumentId override val id: String = "",
    override val type: String = "IMMUNIZATION",
    val vaccineName: String = "",
    val dateAdministered: String = "",
    val administeredBy: String = "",
    val nextDueDate: String = ""
) : MedicalHistoryEntry

// Enum for medical history types to avoid string literals
/**
 * Enum class to represent different types of medical history records.
 *
 * @property value Backend identifier.
 * @property collectionName Firestore collection name.
 * @property type Display label for UI.
 */
enum class MedicalHistoryType(val value: String, val collectionName: String, val type: String) {
    ALLERGY("ALLERGY", "allergies", "Allergies"),
    CONDITION("CONDITION", "conditions", "Conditions"),
    MEDICATION("MEDICATION", "medications", "Medications"),
    SURGERY("SURGERY", "surgeries", "Surgeries"),
    IMMUNIZATION("IMMUNIZATION", "immunizations", "Immunizations");

    companion object {
        /**
         * Returns the [MedicalHistoryType] corresponding to a raw backend value.
         */
        fun fromValue(value: String): MedicalHistoryType? = entries.find { it.value == value }

        /**
         * Returns the [MedicalHistoryType] matching a Firestore collection name.
         */
        fun fromCollectionName(name: String): MedicalHistoryType? = entries.find { it.collectionName == name }

        /**
         * Returns the [MedicalHistoryType] matching a UI type label.
         */
        fun fromType(type: String): MedicalHistoryType? = entries.find { it.type == type }
    }
    /**
     * Gets the user-facing display name for the type.
     */
    fun displayName(): String = when (this) {
        ALLERGY -> "Allergies"
        CONDITION -> "Conditions"
        MEDICATION -> "Medications"
        SURGERY -> "Surgeries"
        IMMUNIZATION -> "Immunizations"
    }
}