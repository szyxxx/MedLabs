package com.tubes.medlab.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AddScheduleScreen(navController: NavController) {
    var scheduleName by remember { mutableStateOf("") }
    var medicineName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(MedicineType.PILL) }
    var strength by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var repetition by remember { mutableStateOf("") }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    val viewModel: ScheduleViewModel = viewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = scheduleName,
            onValueChange = { scheduleName = it },
            label = { Text("Schedule Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = medicineName,
            onValueChange = { medicineName = it },
            label = { Text("Medicine Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Dropdown button for Medicine Type
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = { isDropdownExpanded = !isDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(selectedType.displayName)
            }
            DropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                MedicineType.entries.forEach { type ->
                    DropdownMenuItem(onClick = {
                        selectedType = type
                        isDropdownExpanded = false
                    }, text = {
                        Text(type.displayName)
                    })
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = strength,
            onValueChange = { strength = it },
            label = { Text("Strength") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = quantity,
            onValueChange = { quantity = it },
            label = { Text("Quantity") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = repetition,
            onValueChange = { repetition = it },
            label = { Text("Repetition") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val newSchedule = Schedule(
                    scheduleName = scheduleName,
                    medicineName = medicineName,
                    medicineType = selectedType.displayName,
                    strength = strength,
                    doseQuantity = quantity.toIntOrNull() ?: 0,
                    doseRepetition = repetition.toIntOrNull() ?: 0,
                    statusSchedule = "Not Yet" // Default status
                )
                viewModel.addSchedule(newSchedule)
                navController.navigateUp()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Add Schedule")
        }
    }
}
