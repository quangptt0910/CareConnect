package com.example.careconnect.dataclass

import com.google.firebase.firestore.DocumentId

/**
 * Represents a medical prescription issued by a doctor to a patient.
 *
 * @property id Unique identifier for the prescription (Firestore document ID).
 * @property patientId Firebase UID of the patient.
 * @property doctorId Firebase UID of the doctor.
 * @property medicationName Name of the prescribed medication.
 * @property dosage Dosage instructions (e.g., "1 tablet 3 times a day").
 * @property refills Number of allowed refills.
 * @property instructions Additional usage instructions for the patient.
 * @property issueDate Timestamp of when the prescription was issued.
 * @property validUntil Expiration date for the prescription.
 * @property prescriptionPdfUrl Optional URL to a PDF version of the prescription.
 */
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