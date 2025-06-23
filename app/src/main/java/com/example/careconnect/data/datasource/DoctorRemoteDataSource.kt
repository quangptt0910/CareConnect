package com.example.careconnect.data.datasource

import android.util.Log
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.DoctorSchedule
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.dataclass.PatientRef
import com.example.careconnect.dataclass.Task
import com.example.careconnect.dataclass.TimeSlot
import com.example.careconnect.dataclass.toDateString
import com.example.careconnect.dataclass.toLocalDate
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.dataObjects
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emptyFlow
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

    /**
     * Save working days for a doctor with default time slots
     */
    suspend fun saveWorkingDays(doctorId: String, selectedDates: Set<LocalDate>) {
        try {
            val batch = firestore.batch()
            val doctorScheduleRef = firestore
                .collection(DOCTORS_COLLECTION)
                .document(doctorId)
                .collection(SCHEDULES_COLLECTION)

            selectedDates.forEach { date ->
                val dateString = date.toDateString()
                val scheduleRef = doctorScheduleRef.document(dateString)

                val defaultTimeSlots = listOf(
                    TimeSlot("09:00", "09:30", appointmentMinutes = 30),
                    TimeSlot("10:00", "10:30", appointmentMinutes = 30),
                    TimeSlot("11:00", "11:30", appointmentMinutes = 30),
                    TimeSlot("12:00", "12:30", appointmentMinutes = 30),
                    TimeSlot("14:00", "14:30", appointmentMinutes = 30),
                    TimeSlot("15:00", "15:30", appointmentMinutes = 30),
                    TimeSlot("16:00", "16:30", appointmentMinutes = 30)
                )

                val schedule = DoctorSchedule(
                    id = dateString,
                    date = dateString,
                    timeSlots = defaultTimeSlots,
                    isWorkingDay = true
                )

                batch.set(scheduleRef, schedule)
            }

            batch.commit().await()
        } catch (e: Exception) {
            Log.e("DoctorScheduleRepository", "Failed to save working days", e)
            throw e
        }
    }

    suspend fun getScheduleForDate(doctorId: String, date: LocalDate): List<TimeSlot> {
        if (doctorId.isBlank()) return emptyList()
        val dateKey = date.toDateString()      // e.g. "2025-06-22"
        val docRef = firestore.collection(DOCTORS_COLLECTION).document(doctorId).collection(SCHEDULES_COLLECTION).document(dateKey)
        val snap = docRef.get().await()
        val schedule = snap.toObject(DoctorSchedule::class.java)
        return if (schedule != null && schedule.isWorkingDay) {
            schedule.timeSlots
        } else {
            emptyList()
        }
    }

    /**
     * https://firebase.google.com/docs/firestore/query-data/listen#kotlin
     */
    fun getWorkingDays(doctorId: String): Flow<Set<LocalDate>> = callbackFlow {
        val db = firestore.collection(DOCTORS_COLLECTION).document(doctorId).collection(SCHEDULES_COLLECTION)

        val registration = db.addSnapshotListener { snapshot, e ->
            if (e != null) {
                trySend(emptySet())
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val dates = snapshot.documents.mapNotNull { doc ->
                    try {
                        val schedule = doc.toObject(DoctorSchedule::class.java)
                        if (schedule?.isWorkingDay == true) {
                            schedule.date.toLocalDate()
                        } else null
                    } catch (e: Exception) {
                        Log.e("DoctorScheduleRepository", "Error parsing schedule", e)
                        null
                    }
                }.toSet()
                trySend(dates)
            } else {
                trySend(emptySet())
            }
        }

        awaitClose { registration.remove() }
    }

    fun addPatient(doctorId: String, patientId: String) {
        Log.d("DoctorRemoteDataSource", "DEBUG Adding patient with ID $patientId to doctor with ID $doctorId")
        val patientRef = mapOf("addedAt" to FieldValue.serverTimestamp())
        firestore.collection(DOCTORS_COLLECTION).document(doctorId).collection(PATIENTS_LIST_COLLECTION).document(patientId).set(patientRef, SetOptions.merge())
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

    suspend fun updateTimeSlotAvailability(
        doctorId: String,
        date: String,
        targetTimeSlot: TimeSlot,
        newAvailability: Boolean
    ): Boolean {
        return try {
            val scheduleRef = firestore
                .collection(DOCTORS_COLLECTION)
                .document(doctorId)
                .collection(SCHEDULES_COLLECTION)
                .document(date)

            firestore.runTransaction { transaction ->
                val scheduleSnapshot = transaction.get(scheduleRef)

                if (!scheduleSnapshot.exists()) {
                    throw Exception("Schedule not found for date: $date")
                }

                val schedule = scheduleSnapshot.toObject(DoctorSchedule::class.java)
                    ?: throw Exception("Failed to parse schedule data")

                val timeSlots = schedule.timeSlots.toMutableList()
                var slotFound = false

                for (i in timeSlots.indices) {
                    val slot = timeSlots[i]
                    if (slot.startTime == targetTimeSlot.startTime &&
                        slot.endTime == targetTimeSlot.endTime &&
                        slot.appointmentMinutes == targetTimeSlot.appointmentMinutes &&
                        slot.slotType == targetTimeSlot.slotType) {

                        timeSlots[i] = slot.copy(available = newAvailability)
                        slotFound = true
                        break
                    }
                }

                if (!slotFound) {
                    throw Exception("Time slot not found")
                }

                val updatedSchedule = schedule.copy(timeSlots = timeSlots)
                transaction.set(scheduleRef, updatedSchedule)
            }.await()

            true
        } catch (e: Exception) {
            Log.e("DoctorScheduleRepository", "Failed to update time slot availability", e)
            false
        }
    }



    /**
     * Add time slots to a specific date
     */
    suspend fun addTimeSlots(
        doctorId: String,
        date: LocalDate,
        newTimeSlots: List<TimeSlot>
    ): Boolean {
        return try {
            val dateString = date.toDateString()
            val scheduleRef = firestore
                .collection(DOCTORS_COLLECTION)
                .document(doctorId)
                .collection(SCHEDULES_COLLECTION)
                .document(dateString)

            firestore.runTransaction { transaction ->
                val scheduleSnapshot = transaction.get(scheduleRef)

                val schedule = if (scheduleSnapshot.exists()) {
                    scheduleSnapshot.toObject(DoctorSchedule::class.java)
                        ?: throw Exception("Failed to parse schedule data")
                } else {
                    // Create new schedule if it doesn't exist
                    DoctorSchedule(
                        id = dateString,
                        date = dateString,
                        timeSlots = emptyList(),
                        isWorkingDay = true
                    )
                }

                val updatedTimeSlots = schedule.timeSlots + newTimeSlots
                val updatedSchedule = schedule.copy(timeSlots = updatedTimeSlots)

                transaction.set(scheduleRef, updatedSchedule)
            }.await()

            true
        } catch (e: Exception) {
            Log.e("DoctorScheduleRepository", "Failed to add time slots", e)
            false
        }
    }

    suspend fun deleteSlot(doctorId: String, date: LocalDate, slot: TimeSlot) {
        if (doctorId.isEmpty()) return

        val dateKey = date.toDateString()
        val doctorRef = firestore.collection(DOCTORS_COLLECTION).document(doctorId)
        val scheduleRef = doctorRef.collection(SCHEDULES_COLLECTION).document(dateKey)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(scheduleRef)
            val schedule = snapshot.toObject(DoctorSchedule::class.java) ?: return@runTransaction

            val filtered = schedule.timeSlots.filterNot { it.startTime == slot.startTime && it.endTime == slot.endTime }
            val updated = schedule.copy(timeSlots = filtered)
            transaction.set(scheduleRef, updated, SetOptions.merge())
        }.await()
    }

    suspend fun deleteSlotInRange(doctorId: String, date: LocalDate, startTime: String, endTime: String) {
        if (doctorId.isEmpty()) return

        val dateKey = date.toDateString()
        val doctorRef = firestore.collection(DOCTORS_COLLECTION).document(doctorId)
        val scheduleRef = doctorRef.collection(SCHEDULES_COLLECTION).document(dateKey)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(scheduleRef)
            val schedule = snapshot.toObject(DoctorSchedule::class.java) ?: return@runTransaction

            val filtered = schedule.timeSlots.filterNot { it.startTime >= startTime && it.endTime <= endTime }
            val updated = schedule.copy(timeSlots = filtered)
            transaction.set(scheduleRef, updated, SetOptions.merge())
        }.await()
    }

    suspend fun saveSlot(doctorId: String, date: LocalDate, slot: TimeSlot) {
        if (doctorId.isEmpty()) return
        val dateKey = date.toDateString()
        val scheduleRef = firestore.collection(DOCTORS_COLLECTION).document(doctorId).collection(SCHEDULES_COLLECTION).document(dateKey)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(scheduleRef)
            val schedule = snapshot.toObject(DoctorSchedule::class.java) ?: DoctorSchedule(id = dateKey, date = dateKey, timeSlots = emptyList(), isWorkingDay = true)
            val updatedTimeSlots = schedule.timeSlots.toMutableList()

            val idx = updatedTimeSlots.indexOfFirst { it.startTime == slot.startTime && it.endTime == slot.endTime }
            if (idx >= 0) updatedTimeSlots[idx] = slot
            else updatedTimeSlots += slot

            updatedTimeSlots.sortBy { it.startTime }
            val newSchedule = schedule.copy(
                id = dateKey,
                date = dateKey,
                isWorkingDay = true,
                timeSlots = updatedTimeSlots
            )
            transaction.set(scheduleRef, newSchedule, SetOptions.merge())
        }.await()
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

    suspend fun getAvailableSlots(doctorId: String, date: LocalDate): List<TimeSlot> {
        return try {
            val dateString = date.toDateString()
            val scheduleDoc = firestore
                .collection(DOCTORS_COLLECTION)
                .document(doctorId)
                .collection(SCHEDULES_COLLECTION)
                .document(dateString)
                .get()
                .await()

            if (scheduleDoc.exists()) {
                val schedule = scheduleDoc.toObject(DoctorSchedule::class.java)
                //schedule?.timeSlots?.filter { it.available } ?: emptyList()
                schedule?.timeSlots ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("DoctorScheduleRepository", "Failed to get available slots", e)
            emptyList()
        }
    }

    fun getAvailableSlotsFlow(doctorId: String, date: LocalDate): Flow<List<TimeSlot>> = callbackFlow {
        val dateString = date.toDateString()
        val scheduleRef = firestore
            .collection(DOCTORS_COLLECTION)
            .document(doctorId)
            .collection(SCHEDULES_COLLECTION)
            .document(dateString)

        val registration = scheduleRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("DoctorScheduleRepository", "Listen failed.", e)
                trySend(emptyList())
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                try {
                    val schedule = snapshot.toObject(DoctorSchedule::class.java)
                    val availableSlots = schedule?.timeSlots?.filter { it.available } ?: emptyList()
                    trySend(availableSlots)
                } catch (e: Exception) {
                    Log.e("DoctorScheduleRepository", "Error parsing schedule", e)
                    trySend(emptyList())
                }
            } else {
                trySend(emptyList())
            }
        }

        awaitClose { registration.remove() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getTasksFlow(doctorIdFlow: Flow<String?>): Flow<List<Task>> {
        return doctorIdFlow.flatMapLatest { doctorId ->
            if (doctorId == null) return@flatMapLatest emptyFlow()
            firestore.collection(DOCTORS_COLLECTION)
                .document(doctorId)
                .collection(TASKS_COLLECTION)
                .dataObjects()
        }
    }

    suspend fun getTasks(doctorId: String): List<Task> {
        return firestore.collection(DOCTORS_COLLECTION)
            .document(doctorId)
            .collection(TASKS_COLLECTION)
            .get()
            .await()
            .toObjects(Task::class.java)
    }

    suspend fun addTask(doctorId: String, task: Task): String {
        return firestore.collection(DOCTORS_COLLECTION)
            .document(doctorId)
            .collection(TASKS_COLLECTION)
            .add(task)
            .await()
            .id
    }

    fun deleteTask(doctorId: String, task: Task) {
        firestore.collection(DOCTORS_COLLECTION).document(doctorId).collection(TASKS_COLLECTION).document(task.id).delete()
    }

    fun updateTask(doctorId: String, task: Task) {
        firestore.collection(DOCTORS_COLLECTION).document(doctorId).collection(TASKS_COLLECTION).document(task.id).set(task)
    }

    companion object {
        private const val DOCTORS_COLLECTION = "doctors"
        private const val PATIENTS_LIST_COLLECTION = "patients_list"
        private const val PATIENTS_COLLECTION = "patients"
        private const val TASKS_COLLECTION = "tasks"
        private const val SCHEDULES_COLLECTION = "schedules"
    }
}