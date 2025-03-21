package com.example.careconnect.dataclass

import com.google.firebase.firestore.DocumentId


/*
 * Data class for medical report
 * Medical report for the consultation of the patient
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
