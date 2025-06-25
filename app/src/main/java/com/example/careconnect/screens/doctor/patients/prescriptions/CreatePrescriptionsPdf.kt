package com.example.careconnect.screens.doctor.patients.prescriptions

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.dataclass.Prescription
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Main screen for creating a prescription for a specific patient.
 *
 * - Loads patient details using the given [patientId].
 * - Observes ViewModel states like [patient] and [prescriptionCreated].
 * - Triggers UI feedback upon successful prescription submission.
 *
 * @param patientId ID of the patient for whom the prescription is being created.
 * @param viewModel ViewModel that manages the prescription creation logic.
 * @param navController Navigation controller used to navigate back after submission.
 */
fun CreatePrescriptionPdf(
    context: Context,
    patient: Patient,
    doctor: Doctor,
    prescription: Prescription
): File {
    val document = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = document.startPage(pageInfo)
    val canvas: Canvas = page.canvas
    val paint = Paint()

    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val issueDate = dateFormat.format(prescription.issueDate?.toDate() ?: "Unknown Date")
    val validUntilDate = prescription.validUntil?.toDate()?.let { dateFormat.format(it) } ?: "N/A"

    var y = 40
    fun writeLine(text: String) {
        canvas.drawText(text, 40f, y.toFloat(), paint)
        y += 25
    }

    paint.textSize = 14f

    writeLine("Prescription Document")
    writeLine("----------------------")
    writeLine("Doctor: Dr. ${doctor.name} ${doctor.surname}")
    writeLine("Patient: ${patient.name} ${patient.surname}")
    writeLine("DOB: ${patient.dateOfBirth} | Gender: ${patient.gender}")
    writeLine("Issued On: $issueDate")
    writeLine("Valid Until: $validUntilDate")
    writeLine("")
    writeLine("Medication: ${prescription.medicationName}")
    writeLine("Dosage: ${prescription.dosage}")
    writeLine("Refills Allowed: ${prescription.refills}")
    writeLine("Instructions: ${prescription.instructions}")

    document.finishPage(page)

    val file = File(context.filesDir, "Prescription_${System.currentTimeMillis()}.pdf")
    document.writeTo(FileOutputStream(file))
    document.close()

    return file
}