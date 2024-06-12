package com.tubes.medlab.schedule

import com.google.firebase.firestore.DocumentId

data class Schedule(
    @DocumentId val scheduleId: String = "",
    val scheduleName: String = "",
    val medicineName: String = "",
    val medicineType: String = "",
    val strength: String = "",
    val doseQuantity: Int = 0,
    val doseRepetition: Int = 0,
    val statusSchedule: String = "",
    val dateStart: String = "",
    val timeSchedule: List<String> = emptyList()
)

enum class MedicineType(val displayName: String) {
    PILL("Pill"),
    CAPSULE("Capsule"),
    POWDER("Powder"),
    CREAM("Cream"),
    INJECTION("Injection")
}

enum class StatusSchedule(val displayName: String) {
    DONE("Done"),
    NOTYET("Not Yet"),
}
