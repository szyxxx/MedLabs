package com.tubes.medlab.schedule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tubes.medlab.component.BottomNavBar

@Composable
fun ScheduleScreen(navController: NavController) {
    val viewModel: ScheduleViewModel = viewModel()
    val schedules by viewModel.schedules.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Your Medication Schedule",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(schedules) { schedule ->
                ScheduleCard(navController = navController, schedule = schedule)
            }
        }

        Button(
            onClick = {
                navController.navigate("add_schedule") // Navigasi ke AddScheduleScreen
            },
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
        ) {
            Text(text = "Add Schedule")
        }

        BottomNavBar(navController = navController)
    }
}

@Composable
fun ScheduleCard(navController: NavController, schedule: Schedule) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigate("edit_schedule/${schedule.scheduleId}")
            },
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = schedule.scheduleName,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Medicine: ${schedule.medicineName} (${schedule.medicineType})",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Strength: ${schedule.strength}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Dose: ${schedule.doseQuantity}x${schedule.doseRepetition}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Status: ${schedule.statusSchedule}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
