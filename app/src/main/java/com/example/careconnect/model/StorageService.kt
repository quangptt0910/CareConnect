package com.example.careconnect.model

import com.example.careconnect.dataclass.Appointment
import com.example.careconnect.dataclass.MedicalReport
import com.example.careconnect.dataclass.MedicalHistory
import com.example.careconnect.dataclass.Prescription
import java.io.File

/*
 * Interface for storage service: Medical report, prescription
 *
 */
interface StorageService {
    // Medical report storage
    suspend fun uploadMedicalReport(userId: String, report: MedicalReport): Result<String>
    suspend fun downloadMedicalReport(documentId: String): Result<File>
    suspend fun getMedicalReport(userId: String): Result<List<MedicalReport>>
    suspend fun deleteDocument(documentId: String): Result<Unit>

    // Medical records
    suspend fun getPatientMedicalHistory(patientId: String): Result<List<MedicalHistory>>
    suspend fun getDoctorConsultationHistory(doctorId: String): Result<List<Appointment>>
    suspend fun addMedicalRecord(patientId: String, record: MedicalHistory): Result<String>
    suspend fun getPrescriptionsByPatient(patientId: String): Result<List<Prescription>>
    suspend fun createPrescription(prescription: Prescription): Result<String>
}