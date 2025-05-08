package com.example.careconnect.data.datasource

import android.util.Log
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.dataclass.PatientRef
import com.example.careconnect.dataclass.TimeSlot
import com.example.careconnect.dataclass.toDateString
import com.example.careconnect.dataclass.toLocalDate
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.dataObjects
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject

class DoctorRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val functions: FirebaseFunctions
) {
    suspend fun createDoctor(email: String, password: String, doctorData: Map<String, Any>): Pair<String, String> {
        val data = mapOf(
            "email" to email,
            "password" to password,
            "doctorData" to doctorData
        )

        val result = functions.getHttpsCallable("createDoctor")
            .call(data)
            .await()

        @Suppress("UNCHECKED_CAST")
        val success = result.data as Map<String, Any>

        val message = success["message"] as String
        val doctorId = success["doctorId"] as String

        return message to doctorId
    }

    suspend fun updateDoctor(doctor: Doctor) {
        firestore.collection(DOCTORS_COLLECTION).document(doctor.id).set(doctor).await()
    }

    suspend fun getAllDoctors(): List<Doctor> {
        val snapshot = firestore.collection(DOCTORS_COLLECTION)
            .orderBy("name", Query.Direction.ASCENDING)
            .get().await()
        return snapshot.documents.mapNotNull { document ->
            try {
                val doctor = document.toObject(Doctor::class.java)
                doctor?.copy(id = document.id)
            } catch (e: Exception) {
                Log.e("DoctorRemoteDataSource", "Error converting document to Doctor", e)
                null
            }
        }
    }

    fun getAllDoctorsFlow(): Flow<List<Doctor>> {
        return firestore.collection(DOCTORS_COLLECTION)
            .orderBy("name", Query.Direction.ASCENDING)
            .dataObjects()
    }

    suspend fun getDoctorById(doctorId: String): Doctor? {
        return firestore.collection(DOCTORS_COLLECTION).document(doctorId).get().await().toObject(Doctor::class.java)
    }

    suspend fun saveWorkingDays(doctorId: String, selectedDate: Set<LocalDate>) {
        val db = firestore.collection(DOCTORS_COLLECTION).document(doctorId)
        val workingDays = selectedDate.associate { date ->
            date.toDateString() to listOf(
                TimeSlot("09:00", "12:00"),
                TimeSlot("14:00", "18:00")
            )
        }

        val schedule = hashMapOf("workingDays" to workingDays)

        db.update("schedule", schedule).await()
    }

    /**
     * https://firebase.google.com/docs/firestore/query-data/listen#kotlin
     */
    fun getWorkingDays(doctorId: String): Flow<Set<LocalDate>> = callbackFlow {
        val db = firestore.collection(DOCTORS_COLLECTION).document(doctorId)

        val registration = db.addSnapshotListener { snapshot, e ->
            if (e != null) {
                trySend(emptySet())
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val doctor = snapshot.toObject(Doctor::class.java)
                val workingDays = doctor?.schedule?.workingDays ?: emptyMap()
                val dates = workingDays.keys.map { dateString ->
                    dateString.toLocalDate()
                }.toSet()

                trySend(dates)
            } else {
                trySend(emptySet())
            }
        }

        awaitClose { registration.remove() }
    }

    fun addPatient(doctorId: String, patientId: String) {
        Log.d("DoctorRemoteDataSource", "Adding patient with ID $patientId to doctor with ID $doctorId")
        val patientRef = mapOf("addedAt" to FieldValue.serverTimestamp())
        firestore.collection(DOCTORS_COLLECTION).document(doctorId).collection(PATIENTS_LIST_COLLECTION).document(patientId).set(patientRef)
    }

    // Get full patient details for a doctor
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getPatientsList(doctorId: Flow<String?>): Flow<List<Patient>> {
        Log.d("DoctorRemoteDataSource", "Getting patients list for doctor with ID $doctorId")
        println("DEBUG: Getting patients list for doctor with ID $doctorId")
        return doctorId
            .filterNotNull()
            .flatMapLatest { ownerId ->
                println("DEBUG: Fetching patients list for doctor with ID $ownerId")
                firestore.collection(DOCTORS_COLLECTION)
                    .document(ownerId)
                    .collection(PATIENTS_LIST_COLLECTION)
                    .orderBy("addedAt", Query.Direction.DESCENDING)
                    .dataObjects<PatientRef>()
                    .map { patientRefs ->
                        println("DEBUG: 🔵 Found ${patientRefs.size} patient references")
                        patientRefs.forEach { ref ->
                            println("DEBUG:📋 Patient reference: patientId=${ref.id}")
                        }
                        patientRefs.mapNotNull { patientRef ->
                            val patientDocument = firestore.collection(PATIENTS_COLLECTION)
                                .document(patientRef.id)
                                .get()
                                .await()

                            if (!patientDocument.exists()) {
                                println("DEBUG: ❌ Patient document does not exist for ID: ${patientRef.id}")
                                return@mapNotNull null
                            }

                            val patients = patientDocument.toObject(Patient::class.java)
                            println("DEBUG: 🟢 Found patient: $patients")
                            patients
                        }
                    }
        }
    }

    // Get a single patient by ID
    suspend fun getPatientById(patientId: String): Patient? {
        return try {
            firestore.collection(PATIENTS_COLLECTION)
                .document(patientId)
                .get()
                .await()
                .toObject(Patient::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteSlot(doctorId: String, date: LocalDate, slot: TimeSlot) {
        if (doctorId.isEmpty()) return

        val dateKey = date.toDateString()
        val doctorRef = firestore.collection(DOCTORS_COLLECTION).document(doctorId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(doctorRef)
            val doctor = snapshot.toObject(Doctor::class.java) ?: throw IllegalStateException("Doctor not found")

            val currentSlots = doctor.schedule.workingDays[dateKey]?.toMutableList() ?: return@runTransaction

            // Find and remove the specific slot
            val removedIndex = currentSlots.indexOfFirst {
                it.startTime == slot.startTime && it.endTime == slot.endTime
            }

            if (removedIndex >= 0) {
                currentSlots.removeAt(removedIndex)

                // Update Firestore with the new list
                transaction.update(doctorRef, "schedule.workingDays.$dateKey", currentSlots)

                // Update cache
                cachedSchedules[doctorId]?.let { doctorCache ->
                    val cachedSlots = doctorCache[dateKey]?.toMutableList()
                    cachedSlots?.let {
                        it.removeAll { cachedSlot ->
                            cachedSlot.startTime == slot.startTime && cachedSlot.endTime == slot.endTime
                        }
                        doctorCache[dateKey] = it
                    }
                }
            }
        }.await()
    }

    suspend fun saveSlot(doctorId: String, date: LocalDate, slot: TimeSlot) {
        if (doctorId.isEmpty()) return

        val dateKey = date.toDateString()
        val doctorRef = firestore.collection(DOCTORS_COLLECTION).document(doctorId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(doctorRef)
            val doctor = snapshot.toObject(Doctor::class.java) ?: throw IllegalStateException("Doctor not found")

            val currentSlots = doctor.schedule.workingDays[dateKey]?.toMutableList() ?: mutableListOf()

            // Find existing slot with same time (more efficient than removeAll)
            val existingIndex = currentSlots.indexOfFirst {
                it.startTime == slot.startTime && it.endTime == slot.endTime
            }

            if (existingIndex >= 0) {
                currentSlots[existingIndex] = slot // Replace existing
            } else {
                currentSlots.add(slot) // Add new
            }

            // Sort slots by start time for better UI experience
            currentSlots.sortBy { it.startTime }

            // Update Firestore
            val updatedSchedule = doctor.schedule.workingDays.toMutableMap()
            updatedSchedule[dateKey] = currentSlots

            transaction.update(doctorRef, "schedule.workingDays.$dateKey", currentSlots)
        }.await()

        // Update cache
        val doctorCache = cachedSchedules.getOrPut(doctorId) { mutableMapOf() }
        val currentCachedSlots = doctorCache[dateKey]?.toMutableList() ?: mutableListOf()

        val existingIndex = currentCachedSlots.indexOfFirst {
            it.startTime == slot.startTime && it.endTime == slot.endTime
        }

        if (existingIndex >= 0) {
            currentCachedSlots[existingIndex] = slot
        } else {
            currentCachedSlots.add(slot)
        }

        currentCachedSlots.sortBy { it.startTime }
        doctorCache[dateKey] = currentCachedSlots
    }


    suspend fun getScheduleForDate(doctorId: String, date: LocalDate): List<TimeSlot> {
        if (doctorId.isEmpty()) return emptyList()
        val dateKey = date.toDateString()

        // Use cache if available
        cachedSchedules[doctorId]?.let { doctorCache ->
            doctorCache[dateKey]?.let { slots ->
                return slots
            }
        }

        return try {
            val snapshot = firestore.collection(DOCTORS_COLLECTION).document(doctorId).get().await()
            val doctor = snapshot.toObject(Doctor::class.java)
            val slots = doctor?.schedule?.workingDays?.get(dateKey) ?: emptyList()

            // Update cache
            val doctorCache = cachedSchedules.getOrPut(doctorId) { mutableMapOf() }
            doctorCache[dateKey] = slots

            slots
        } catch (e: Exception) {
            Log.e("DoctorRemoteDataSource", "Error getting doctor", e)
            emptyList()
        }
    }

    private val cachedSchedules = mutableMapOf<String, MutableMap<String, List<TimeSlot>>>()

    // Add clearing cache function
    fun clearCache(doctorId: String? = null) {
        if (doctorId != null) {
            cachedSchedules.remove(doctorId)
        } else {
            cachedSchedules.clear()
        }
    }

    companion object {
        private const val DOCTORS_COLLECTION = "doctors"
        private const val PATIENTS_LIST_COLLECTION = "patients_list"
        private const val PATIENTS_COLLECTION = "patients"
    }
}