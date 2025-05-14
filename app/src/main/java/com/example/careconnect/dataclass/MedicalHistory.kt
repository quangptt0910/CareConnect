package com.example.careconnect.dataclass


/*
 * Data class for patient medical history
 * Medical history - Information about the patient
 * Patient and Doctors can view, add their medical history here
 * Patient should have their own medical history before they start a consultation
 */
data class MedicalHistory(
    val patientId: String = "",  // Firebase UID of the patient
    val patientName: String = "",
    val patientSurname: String = "",
    val dateOfBirth: String = "",  // Date of birth of the patient (e.g., "1990-05-15")

    val allergies: List<Allergy> = listOf(),  // List of allergies
    val medicalConditions: List<Condition> = listOf(),  // List of medical conditions (e.g., diabetes, asthma)
    val medications: List<Medication> = listOf(),  // List of medications the patient is taking
    val surgeries: List<Surgery> = listOf(),  // List of surgeries
    val immunizations: List<Immunization> = listOf()  // List of immunizations received
)

data class Allergy(
    val type: String = "ALLERGY",
    val allergen: String = "",  // Allergen name (e.g., "Peanuts")
    val reaction: String = "",  // Description of the reaction (e.g., "Hives, difficulty breathing")
    val severity: String = "",  // Severity of the allergic reaction (e.g., "Mild", "Severe")
    val diagnosedDate: String = ""  // Date when the allergy was diagnosed
)

data class Condition(
    val type: String = "CONDITION",
    val name: String = "",  // Name of the condition (e.g., "Diabetes")
    val diagnosedDate: String = "",  // Date when the condition was diagnosed
    val status: String = ""  // Current status (e.g., "Controlled", "Active", "In remission")
)

data class Medication(
    val type: String = "MEDICATION",
    val name: String = "",  // Name of the medication (e.g., "Insulin")
    val dosage: String = "",  // Dosage (e.g., "10mg")
    val startDate: String = "",  // When the medication was started
    val endDate: String = "",  // When the medication was stopped (if applicable)
    val frequency: String = ""  // How often the medication is taken (e.g., "Twice a day")
)

data class Surgery(
    val type: String = "SURGERY",
    val surgeryName: String = "",  // Name of the surgery (e.g., "Appendectomy")
    val surgeryDate: String = "",  // Date of the surgery
    val hospital: String = "",  // Name of the hospital where the surgery was performed
    val notes: String = ""  // Additional notes (e.g., recovery time, complications)
)

data class Immunization(
    val type: String = "IMMUNIZATION",
    val vaccineName: String = "",  // Name of the vaccine (e.g., "MMR Vaccine")
    val dateAdministered: String = "",  // Date when the vaccine was administered
    val administeredBy: String = "",  // Who administered the vaccine (e.g., "Dr. Smith")
    val nextDueDate: String = ""  // Next due date for a follow-up shot, if any
)

