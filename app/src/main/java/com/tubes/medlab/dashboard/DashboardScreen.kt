package com.tubes.medlab.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tubes.medlab.component.BottomNavBar
import com.tubes.medlab.schedule.ScheduleViewModel
import com.tubes.medlab.schedule.Schedule
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(navController: NavController) {
    val viewModel: ScheduleViewModel = viewModel()
    val schedulesWithTime by viewModel.schedulesWithTime.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(16.dp) // Tambahkan margin di sini
        ) {
            LazyColumn(
                contentPadding = innerPadding,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            UserSection(userName = "John Doe")
                            Spacer(modifier = Modifier.height(16.dp)) // Spasi antara bagian
                            CalendarSection()
                        }
                    }
                }
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(vertical = 16.dp, horizontal = 16.dp)
                    ) {
                        Text(
                            text = "Jadwal",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        ScheduleSection(scheduleList = schedulesWithTime)
                    }
                }
            }
        }
    }
}

@Composable
fun UserSection(userName: String) {
    Text(
        text = "Welcome, $userName",
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
        style = MaterialTheme.typography.headlineLarge,
        color = Color.Black
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Ambil tanggal hari ini
        val today = LocalDate.now()

        // Hari-hari dalam seminggu
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            (0..6).forEach { index ->
                val day = today.plusDays(index.toLong())
                Text(text = day.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()))
            }
        }

        // Tanggal dalam seminggu
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            (0..6).forEach { index ->
                val day = today.plusDays(index.toLong())
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = day.dayOfMonth.toString(),
                        textAlign = TextAlign.Center
                    )
                    if (day.dayOfMonth == today.dayOfMonth) {
                        Spacer(modifier = Modifier.height(4.dp)) // Add spacing between the text and the circle
                        CircleIndicator(day = day)
                    }
                    Spacer(modifier = Modifier.height(4.dp)) // Add spacing between the text and the next item
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CircleIndicator(day: LocalDate) {
    Box(
        modifier = Modifier.size(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = day.dayOfMonth.toString())
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color = Color.Blue, shape = CircleShape)
        )
    }
}

@Composable
fun ScheduleSection(scheduleList: List<Pair<Schedule, String>>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (scheduleList.isNotEmpty()) {
            scheduleList.forEach { (schedule, time) ->
                ScheduleCard(schedule = schedule, time = time)
            }
        } else {
            Card(
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text(
                    text = "Istirahat dulu aja ya",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun ScheduleCard(schedule: Schedule, time: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "${schedule.scheduleName}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "${schedule.medicineName}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Tipe Obat: ${schedule.medicineType}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Waktu: $time", style = MaterialTheme.typography.bodyMedium)
            // Tambahkan elemen lain dari schedule yang ingin Anda tampilkan di sini
        }
    }
}
