package com.example.careconnect.dataclass

import com.google.firebase.firestore.DocumentId

data class Prescription(
    @DocumentId val id: String = "", // Firestore document ID (optional)
    val patientId: String = "", // Firebase UID of the patient

    val doctorId: String = "", // Firebase UID of the doctor

    val medicationName: String = "", // Name of the prescribed medication
    val dosage: String = "", // Dosage instruction (e.g., "1 tablet 3 times a day")
    val refills: Int = 0, // Number of refills allowed
    val instructions: String = "", // Additional instructions for the patient

    val issueDate: com.google.firebase.Timestamp? = null, // Prescription date
    val validUntil: com.google.firebase.Timestamp? = null, // Expiry date for prescription
    val prescriptionPdfUrl: String? = null // Optional: URL to the generated PDF for the prescription
)