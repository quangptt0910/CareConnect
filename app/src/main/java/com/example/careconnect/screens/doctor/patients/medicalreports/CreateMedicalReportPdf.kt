package com.example.careconnect.screens.doctor.patients.medicalreports

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.MedicalReport
import com.example.careconnect.dataclass.Patient
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale


fun CreateMedicalReportPdf(
    context: Context,
    patient: Patient,
    doctor: Doctor,
    medicalReport: MedicalReport
): File {
    val document = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = document.startPage(pageInfo)
    val canvas: Canvas = page.canvas
    val paint = Paint()

    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val reportDate = medicalReport.reportDate?.toDate()?.let { dateFormat.format(it) } ?: "Unknown Date"

    var y = 40
    fun writeLine(text: String) {
        canvas.drawText(text, 40f, y.toFloat(), paint)
        y += 25
    }

    paint.textSize = 14f

    writeLine("Medical Report")
    writeLine("--------------")
    writeLine("Doctor: Dr. ${doctor.name} ${doctor.surname}")
    writeLine("Patient: ${patient.name} ${patient.surname}")
    writeLine("DOB: ${patient.dateOfBirth} | Gender: ${patient.gender}")
    writeLine("Report Date: $reportDate")
    writeLine("")
    writeLine("Symptoms: ${medicalReport.symptoms.joinToString(", ")}")
    writeLine("Diagnosis: ${medicalReport.diagnosis}")
    writeLine("Prognosis: ${medicalReport.prognosis}")
    writeLine("Treatment: ${medicalReport.treatment}")
    writeLine("Recommendations: ${medicalReport.recommendations}")
    writeLine("Plan of Care: ${medicalReport.plan}")

    document.finishPage(page)

    val file = File(context.filesDir, "MedicalReport_${System.currentTimeMillis()}.pdf")
    document.writeTo(FileOutputStream(file))
    document.close()

    return file
}
