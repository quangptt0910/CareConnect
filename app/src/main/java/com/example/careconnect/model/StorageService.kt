package com.example.careconnect.model

import com.example.careconnect.dataclass.Prescription
import java.io.File

/*
 * Interface for storage service: Medical report, prescription
 *
 */
interface StorageService {
    // Document storage
    suspend fun uploadDocument(userId: String, document: Document, file: File): Result<String>
    suspend fun downloadDocument(documentId: String): Result<File>
    suspend fun getUserDocuments(userId: String): Result<List<Document>>
    suspend fun deleteDocument(documentId: String): Result<Unit>

    // Medical records
    suspend fun getPatientMedicalHistory(patientId: String): Result<List<MedicalRecord>>
    suspend fun getDoctorConsultationHistory(doctorId: String): Result<List<Consultation>>
    suspend fun addMedicalRecord(patientId: String, record: MedicalRecord): Result<String>
    suspend fun getPrescriptionsByPatient(patientId: String): Result<List<Prescription>>
    suspend fun createPrescription(prescription: Prescription): Result<String>
}