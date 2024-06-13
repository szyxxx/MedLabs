package com.tubes.medlab.schedule

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun EditScheduleScreen(navController: NavController, scheduleId: String, userId: String) {
    Log.d("EditScheduleScreen", "Received scheduleId: $scheduleId and userId: $userId")
    val viewModel: EditScheduleViewModel = viewModel()
    val schedule by viewModel.schedule.collectAsState()

    LaunchedEffect(scheduleId) {
        viewModel.loadSchedule(scheduleId, userId)
    }

    LaunchedEffect(schedule) {
        Log.d("EditScheduleScreen", "Loaded schedule: $schedule")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Edit Schedule",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        schedule?.let { sched ->
            Log.d("EditScheduleScreen", "Schedule data: $sched")

            // Form fields for editing the schedule
            var scheduleName by remember { mutableStateOf(sched.scheduleName) }
            var medicineName by remember { mutableStateOf(sched.medicineName) }
            var medicineType by remember { mutableStateOf(sched.medicineType) }
            var strength by remember { mutableStateOf(sched.strength) }
            var doseQuantity by remember { mutableStateOf(sched.doseQuantity.toString()) }
            var doseRepetition by remember { mutableStateOf(sched.doseRepetition.toString()) }
            var statusSchedule by remember { mutableStateOf(sched.statusSchedule) }
            var dateStart by remember { mutableStateOf(sched.dateStart) }

            TextField(
                value = scheduleName,
                onValueChange = { scheduleName = it },
                label = { Text("Schedule Name") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            TextField(
                value = medicineName,
                onValueChange = { medicineName = it },
                label = { Text("Medicine Name") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            TextField(
                value = medicineType,
                onValueChange = { medicineType = it },
                label = { Text("Medicine Type") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            TextField(
                value = strength,
                onValueChange = { strength = it },
                label = { Text("Strength") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            TextField(
                value = doseQuantity,
                onValueChange = { doseQuantity = it },
                label = { Text("Dose Quantity") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            TextField(
                value = doseRepetition,
                onValueChange = { doseRepetition = it },
                label = { Text("Dose Repetition") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            TextField(
                value = statusSchedule,
                onValueChange = { statusSchedule = it },
                label = { Text("Status Schedule") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            TextField(
                value = dateStart,
                onValueChange = { dateStart = it },
                label = { Text("Date Start") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            Button(
                onClick = {
                    val updatedSchedule = sched.copy(
                        scheduleName = scheduleName,
                        medicineName = medicineName,
                        medicineType = medicineType,
                        strength = strength,
                        doseQuantity = doseQuantity.toIntOrNull() ?: 0,
                        doseRepetition = doseRepetition.toIntOrNull() ?: 0,
                        statusSchedule = statusSchedule,
                        dateStart = dateStart,
                        timeSchedule = sched.timeSchedule // retain existing time schedule
                    )
                    viewModel.updateSchedule(updatedSchedule, userId)
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Save Changes")
            }

            OutlinedButton(
                onClick = {
                    viewModel.deleteSchedule(scheduleId, userId)
                    navController.popBackStack()
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Delete Schedule")
            }
        } ?: run {
            // Log jika schedule masih null
            Log.d("EditScheduleScreen", "Schedule is null")
        }
    }
}

class EditScheduleViewModel : ViewModel() {
    private val _schedule = MutableStateFlow<Schedule?>(null)
    val schedule: StateFlow<Schedule?> get() = _schedule

    private val scheduleRepository = ScheduleRepository()

    fun loadSchedule(scheduleId: String, userId: String) {
        viewModelScope.launch {
            scheduleRepository.getSchedule(scheduleId, userId) { schedule ->
                Log.d("EditScheduleViewModel", "Fetched schedule for id $scheduleId: $schedule")
                _schedule.value = schedule
            }
        }
    }

    fun updateSchedule(schedule: Schedule, userId: String) {
        viewModelScope.launch {
            scheduleRepository.updateSchedule(schedule, userId)
        }
    }

    fun deleteSchedule(scheduleId: String, userId: String) {
        viewModelScope.launch {
            scheduleRepository.deleteSchedule(scheduleId, userId)
        }
    }
}

