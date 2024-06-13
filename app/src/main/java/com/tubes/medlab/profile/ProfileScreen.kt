package com.tubes.medlab.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tubes.medlab.component.BottomNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val viewModel: ProfileViewModel = viewModel()
    val userName by viewModel.userName.observeAsState("Unknown User")
    val weight by viewModel.weight.observeAsState("Not Set")
    val height by viewModel.height.observeAsState("Not Set")
    val bloodType by viewModel.bloodType.observeAsState("Not Set")
    val notificationsEnabled by viewModel.notificationsEnabled.observeAsState(false)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") }
            )
        }, bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = userName, style = MaterialTheme.typography.headlineMedium)
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                navController.navigate("edit_profile")
                            }
                    )
                }
            }

            DisplayCard(title = "Berat Badan", value = weight)
            DisplayCard(title = "Tinggi Badan", value = height)
            DisplayCard(title = "Golongan Darah", value = bloodType)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Enable Notifications", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { viewModel.toggleNotifications() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.logout()
                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }
        }
    }
}

@Composable
fun DisplayCard(title: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "$title: $value", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
