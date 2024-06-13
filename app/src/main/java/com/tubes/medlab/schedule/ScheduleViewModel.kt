package com.tubes.medlab.schedule

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ScheduleViewModel : ViewModel() {
    private val _schedules = MutableStateFlow<List<Schedule>>(emptyList())
    val schedules: StateFlow<List<Schedule>> = _schedules
    private val _schedulesWithTime = MutableStateFlow<List<Pair<Schedule, String>>>(emptyList())
    val schedulesWithTime: StateFlow<List<Pair<Schedule, String>>> get() = _schedulesWithTime

    private val _nearestSchedule = MutableStateFlow<Schedule?>(null)
    val nearestSchedule: StateFlow<Schedule?> = _nearestSchedule

    private val userId: String? = FirebaseAuth.getInstance().currentUser?.uid

    private var scheduleListenerRegistration: ListenerRegistration? = null

    init {
        userId?.let { uid ->
            loadSchedulesFromFirestore(uid)
            scheduleListenerRegistration = FirebaseFirestoreUtil.listenToSchedules(uid) { schedules ->
                _schedules.value = schedules
                updateNearestSchedule(schedules)
                updateSchedulesWithTime(schedules)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        scheduleListenerRegistration?.remove()
    }

    private fun loadSchedulesFromFirestore(userId: String) {
        viewModelScope.launch {
            try {
                val schedules = FirebaseFirestoreUtil.getSchedules(userId)
                _schedules.value = schedules
                if (schedules.isNotEmpty()) {
                    updateNearestSchedule(schedules)
                    updateSchedulesWithTime(schedules)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun updateNearestSchedule(schedules: List<Schedule>) {
        val nearest = findNearestSchedule(schedules)
        _nearestSchedule.value = nearest
    }

    private fun updateSchedulesWithTime(schedules: List<Schedule>) {
        val activeSchedules = schedules.filter { it.statusSchedule != "Done" }
        val schedulesWithTime = activeSchedules.flatMap { schedule ->
            schedule.timeSchedule.map { time -> schedule to time }
        }
        _schedulesWithTime.value = schedulesWithTime
    }

    private fun findNearestSchedule(schedules: List<Schedule>): Schedule? {
        val currentDate = Calendar.getInstance().time
        return schedules.filter { it.dateStart.isNotEmpty() }.minByOrNull {
            val scheduleDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.dateStart)
            scheduleDate?.time ?: (Long.MAX_VALUE - currentDate.time)
        }
    }

    fun addSchedule(schedule: Schedule) {
        viewModelScope.launch {
            try {
                userId?.let { uid ->
                    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
                    val timeSchedule = generateTimeSchedule(Calendar.getInstance(), schedule.doseRepetition)
                    val newSchedule = schedule.copy(dateStart = currentDate, timeSchedule = timeSchedule)
                    FirebaseFirestoreUtil.addSchedule(uid, newSchedule)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteSchedule(scheduleId: String) {
        viewModelScope.launch {
            userId?.let { uid ->
                try {
                    FirebaseFirestoreUtil.deleteSchedule(uid, scheduleId)
                } catch (e: Exception) {
                    // Handle error
                }
            }
        }
    }

    private fun generateTimeSchedule(startTime: Calendar, repetition: Int): List<String> {
        val times = mutableListOf<String>()
        var currentTime = startTime

        for (i in 0 until repetition) {
            // Add current time to list
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
//            times.add(timeFormat.format(currentTime.time))

            // Increment time by 5 hours
            currentTime.add(Calendar.HOUR_OF_DAY, 8)

            // Check if time falls between 22:00 and 06:59 and adjust accordingly
            val hour = currentTime.get(Calendar.HOUR_OF_DAY)
            if (hour in 22..23 || hour in 0..6) {
                currentTime.set(Calendar.HOUR_OF_DAY, 7)
                currentTime.set(Calendar.MINUTE, 0)
            }
        }

        return times
    }
}

object FirebaseFirestoreUtil {
    @SuppressLint("StaticFieldLeak")
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun getUserScheduleCollection(userId: String): CollectionReference {
        return firestore.collection("medlabs-$userId")
    }

    fun listenToSchedules(userId: String, onSchedulesChanged: (List<Schedule>) -> Unit): ListenerRegistration {
        val userScheduleCollection = getUserScheduleCollection(userId)
        return userScheduleCollection.addSnapshotListener { snapshot, e ->
            if (e != null || snapshot == null) {
                return@addSnapshotListener
            }

            val schedules = snapshot.documents
                .filter { it.id != "userData" }
                .mapNotNull { it.toObject(Schedule::class.java) }
            onSchedulesChanged(schedules)
        }
    }

    suspend fun addSchedule(userId: String, schedule: Schedule) {
        try {
            val userScheduleCollection = getUserScheduleCollection(userId)
            userScheduleCollection.add(schedule).await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updateSchedule(userId: String, schedule: Schedule) {
        val scheduleId = schedule.scheduleId
        try {
            val userScheduleCollection = getUserScheduleCollection(userId)
            userScheduleCollection.document(scheduleId).set(schedule).await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun deleteSchedule(userId: String, scheduleId: String) {
        try {
            val userScheduleCollection = getUserScheduleCollection(userId)
            userScheduleCollection.document(scheduleId).delete().await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getSchedules(userId: String): List<Schedule> {
        return try {
            val querySnapshot = getUserScheduleCollection(userId).get().await()
            querySnapshot.documents
                .filter { it.id != "userData" }
                .mapNotNull { it.toObject(Schedule::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun updateScheduleAfterNotificationClick(scheduleId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userScheduleCollection = getUserScheduleCollection(userId)

        userScheduleCollection.document(scheduleId).get().addOnSuccessListener { document ->
            val schedule = document.toObject(Schedule::class.java)
            schedule?.let {
                val updatedTimeSchedule = it.timeSchedule.drop(1) // Hapus timeSchedule pertama
                val updatedDoseQuantity = it.doseQuantity - 1

                val updatedSchedule = it.copy(
                    timeSchedule = updatedTimeSchedule,
                    doseQuantity = updatedDoseQuantity
                )

                userScheduleCollection.document(scheduleId).set(updatedSchedule)
            }
        }
    }
}
class ScheduleRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getSchedule(scheduleId: String, userId: String, onResult: (Schedule?) -> Unit) {
        val docRef = db.collection("medlabs-$userId").document(scheduleId)
        docRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val schedule = document.toObject(Schedule::class.java)
                Log.d("ScheduleRepository", "Fetched schedule for id $scheduleId: $schedule")
                onResult(schedule)
            } else {
                Log.d("ScheduleRepository", "No such document for id $scheduleId")
                onResult(null)
            }
        }.addOnFailureListener { exception ->
            Log.d("ScheduleRepository", "Failed to fetch document: $exception")
            onResult(null)
        }
    }

    fun updateSchedule(schedule: Schedule, userId: String) {
        val docRef = db.collection("medlabs-$userId").document(schedule.scheduleId)
        docRef.set(schedule)
            .addOnSuccessListener { Log.d("ScheduleRepository", "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w("ScheduleRepository", "Error updating document", e) }
    }

    fun deleteSchedule(scheduleId: String, userId: String) {
        val docRef = db.collection("medlabs-$userId").document(scheduleId)
        docRef.delete()
            .addOnSuccessListener { Log.d("ScheduleRepository", "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w("ScheduleRepository", "Error deleting document", e) }
    }
}

