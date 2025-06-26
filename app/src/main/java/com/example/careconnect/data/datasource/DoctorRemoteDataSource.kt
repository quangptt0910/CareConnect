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
import java.util.Calendar
import java.util.Date
import javax.inject.Inject


/**
 * Remote data source for managing doctor-related data using Firebase Firestore and Firebase Functions.
 *
 * This class handles operations such as creating doctor accounts, managing doctor schedules,
 * handling patient lists, managing available time slots, and working with tasks associated with doctors.
 *
 * @property firestore Firestore database instance
 * @property functions Firebase Functions instance
 */
class DoctorRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val functions: FirebaseFunctions
) {
    /**
     * Creates a new doctor using a Firebase Cloud Function.
     *
     * @param email Doctor's email address
     * @param password Initial password
     * @param doctorData Additional profile data
     * @return A pair of (message, doctorId)
     */
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

    suspend fun deleteDoctor(doctor: Doctor) {
        firestore.collection(DOCTORS_COLLECTION).document(doctor.id).delete().await()
    }

    /**
     * Retrieves the list of doctors scheduled to work today.
     *
     * @return List of doctors working today
     */
    suspend fun getDoctorsWorkingToday(): List<Doctor> {
        val todayString = LocalDate.now().toDateString()
        val allDoctors = getAllDoctors()
        val availableDoctors = mutableListOf<Doctor>()

        for (doctor in allDoctors) {
            val scheduleSnap = firestore
                .collection("doctors")
                .document(doctor.id)
                .collection("schedules")
                .document(todayString)
                .get()
                .await()

            val schedule = scheduleSnap.toObject(DoctorSchedule::class.java)
            if (schedule != null && schedule.isWorkingDay) {
                availableDoctors.add(doctor)
            }
        }

        return availableDoctors
    }

    /**
     * Fetches doctors added in the last [daysAgo] days.
     *
     * @param daysAgo Number of days to look back (default is 7)
     * @return List of recently added doctors
     */
    suspend fun getRecentlyAddedDoctors(daysAgo: Long = 7): List<Doctor> {
        val now = Date()
        val calendar = Calendar.getInstance()
        calendar.time = now
        calendar.add(Calendar.DAY_OF_YEAR, (-daysAgo).toInt())
        val startDate = calendar.time

        val snapshot = firestore.collection("doctors")
            .whereGreaterThanOrEqualTo("createdAt", startDate)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()

        return snapshot.toObjects(Doctor::class.java)
    }

    /**
     * Updates doctor information in Firestore.
     *
     * @param doctor The doctor to update
     */
    suspend fun updateDoctor(doctor: Doctor) {
        firestore.collection(DOCTORS_COLLECTION).document(doctor.id).set(doctor).await()
    }

    /**
     * Returns a list of all doctors in the system.
     *
     * @return List of all doctors
     */
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

    /**
     * Provides a real-time stream of all doctors.
     *
     * @return Flow of doctor list
     */
    fun getAllDoctorsFlow(): Flow<List<Doctor>> {
        return firestore.collection(DOCTORS_COLLECTION)
            .orderBy("name", Query.Direction.ASCENDING)
            .dataObjects()
    }

    /**
     * Gets doctor details by their ID.
     *
     * @param doctorId Doctor's ID
     * @return The doctor if found, otherwise null
     */
    suspend fun getDoctorById(doctorId: String): Doctor? {
        return firestore.collection(DOCTORS_COLLECTION).document(doctorId).get().await().toObject(Doctor::class.java)
    }

    /**
     * Saves selected working days with default time slots for a doctor.
     *
     * @param doctorId ID of the doctor
     * @param selectedDates Set of selected working dates
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

    /**
     * Retrieves schedule time slots for a given doctor and date.
     *
     * @param doctorId ID of the doctor
     * @param date Date to retrieve schedule for
     * @return List of available time slots
     */
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


    suspend fun deleteWorkingDay(doctorId: String, date: LocalDate) {
        firestore.collection(DOCTORS_COLLECTION).document(doctorId).collection(SCHEDULES_COLLECTION).document(date.toDateString()).delete().await()
    }
    /**
     * https://firebase.google.com/docs/firestore/query-data/listen#kotlin
     */
    /**
     * Observes the set of working days for a doctor.
     *
     * @param doctorId ID of the doctor
     * @return Flow emitting sets of working dates
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

    /**
     * Adds a patient to a doctor's list.
     *
     * @param doctorId Doctor's ID
     * @param patientId Patient's ID
     */
    fun addPatient(doctorId: String, patientId: String) {
        Log.d("DoctorRemoteDataSource", "DEBUG Adding patient with ID $patientId to doctor with ID $doctorId")
        val patientRef = mapOf("addedAt" to FieldValue.serverTimestamp())
        firestore.collection(DOCTORS_COLLECTION).document(doctorId).collection(PATIENTS_LIST_COLLECTION).document(patientId).set(patientRef, SetOptions.merge())
    }

    // Get full patient details for a doctor
    /**
     * Provides a real-time stream of full patient details for a doctor.
     *
     * @param doctorId Flow of doctor ID
     * @return Flow emitting list of patients
     */
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

    /**
     * Updates the availability of a specific time slot.
     *
     * @param doctorId Doctor's ID
     * @param date Date string
     * @param targetTimeSlot Time slot to update
     * @param newAvailability New availability status
     * @return True if update was successful
     */
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
     * Adds new time slots to a schedule for a specific date.
     *
     * @param doctorId Doctor's ID
     * @param date Date to add slots to
     * @param newTimeSlots List of time slots to add
     * @return True if successful
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

    /**
     * Deletes a specific time slot from a doctor's schedule.
     *
     * @param doctorId Doctor's ID
     * @param date Date of the slot
     * @param slot Time slot to delete
     */
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

    /**
     * Deletes all time slots within a given time range.
     *
     * @param doctorId Doctor's ID
     * @param date Date of the schedule
     * @param startTime Start of the range
     * @param endTime End of the range
     */
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

    /**
     * Saves or updates a single time slot in the schedule.
     *
     * @param doctorId Doctor's ID
     * @param date Date to update
     * @param slot Time slot to save
     */
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
    /**
     * Clears the cached schedules for a specific doctor or all if null.
     *
     * @param doctorId Optional doctor ID to clear specific cache
     */
    fun clearCache(doctorId: String? = null) {
        if (doctorId != null) {
            cachedSchedules.remove(doctorId)
        } else {
            cachedSchedules.clear()
        }
    }

    /**
     * Retrieves available time slots for a doctor on a specific date.
     *
     * @param doctorId Doctor's ID
     * @param date Date to check
     * @return List of available time slots
     */
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

    /**
     * Provides a stream of available time slots for a doctor on a specific date.
     *
     * @param doctorId Doctor's ID
     * @param date Date to observe
     * @return Flow emitting list of available slots
     */
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

    /**
     * Provides a stream of tasks assigned to the doctor.
     *
     * @param doctorIdFlow Flow of doctor ID
     * @return Flow emitting list of tasks
     */
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

    /**
     * Gets all tasks assigned to a doctor.
     *
     * @param doctorId Doctor's ID
     * @return List of tasks
     */
    suspend fun getTasks(doctorId: String): List<Task> {
        return firestore.collection(DOCTORS_COLLECTION)
            .document(doctorId)
            .collection(TASKS_COLLECTION)
            .get()
            .await()
            .toObjects(Task::class.java)
    }

    /**
     * Adds a new task to a doctor's task list.
     *
     * @param doctorId Doctor's ID
     * @param task Task to add
     * @return The ID of the created task
     */
    suspend fun addTask(doctorId: String, task: Task): String {
        return firestore.collection(DOCTORS_COLLECTION)
            .document(doctorId)
            .collection(TASKS_COLLECTION)
            .add(task)
            .await()
            .id
    }

    /**
     * Deletes a task from a doctor's task list.
     *
     * @param doctorId Doctor's ID
     * @param task Task to delete
     */
    fun deleteTask(doctorId: String, task: Task) {
        firestore.collection(DOCTORS_COLLECTION).document(doctorId).collection(TASKS_COLLECTION).document(task.id).delete()
    }

    /**
     * Updates a task in a doctor's task list.
     *
     * @param doctorId Doctor's ID
     * @param task Task to update
     */
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