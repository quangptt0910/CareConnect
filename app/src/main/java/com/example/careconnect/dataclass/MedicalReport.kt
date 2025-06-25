package com.example.careconnect.dataclass

import com.google.firebase.firestore.DocumentId


/**
 * Represents a detailed medical report written by a doctor following a consultation.
 *
 * @property id Unique identifier for the report (Firestore document ID).
 * @property patientId Firebase UID of the patient.
 * @property doctorId Firebase UID of the doctor.
 * @property symptoms List of symptoms the patient is experiencing.
 * @property diagnosis Doctor's diagnosis based on the consultation.
 * @property prognosis Expected outcome or forecast of the condition.
 * @property treatment Description of the treatment plan.
 * @property recommendations Any recommendations made by the doctor.
 * @property plan Follow-up actions such as appointments or additional tests.
 * @property reportDate Timestamp when the report was generated.
 * @property reportPdfUrl Optional URL linking to a PDF version of the report.
 */
data class MedicalReport(
    @DocumentId val id: String = "",  // Firestore document ID (optional)
    val patientId: String = "",  // Firebase UID of the patient

    val doctorId: String = "",  // Firebase UID of the doctor
//    val doctorName: String = "",
//    val doctorSurname: String = "",
//    val doctorPhone: String = "",
//    val doctorSpecialty: String = "",

//    val height: Double = 0.0,  // Patient's height in cm (optional)
//    val weight: Double = 0.0,  // Patient's weight in kg (optional)
//    val medicalHistory: String = "",  // Patient's medical history or conditions
//    val allergies: String = "",  // Allergies (can be a list or just a string)

    val symptoms: List<String> = listOf(),  // List of symptoms the patient is experiencing
    val diagnosis: String = "",  // Diagnosis given by the doctor
    val prognosis: String = "",  // Doctor’s prognosis for the condition
    val treatment: String = "",  // Treatment plan or prescribed treatment
    val recommendations: String = "",  // Any additional recommendations by the doctor
    val plan: String = "",  // Future action plan (follow-up appointments, tests, etc.)

    val reportDate: com.google.firebase.Timestamp? = null,  // Date of the report
    val reportPdfUrl: String? = null  // Optional: URL to the generated PDF report
)
