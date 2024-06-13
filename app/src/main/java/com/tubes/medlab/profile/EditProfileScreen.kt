package com.tubes.medlab.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EditProfileScreen(navController: NavController) {
    val viewModel: ProfileViewModel = viewModel()
    var newWeight by remember { mutableStateOf("") }
    var newHeight by remember { mutableStateOf("") }
    var newBloodType by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Form fields for editing weight, height, and blood type
            EditableField("Berat Badan", "Masukkan berat badan baru", value = newWeight, onValueChange = { newWeight = it })
            EditableField("Tinggi Badan", "Masukkan tinggi badan baru", value = newHeight, onValueChange = { newHeight = it })
            EditableField("Golongan Darah", "Masukkan golongan darah baru", value = newBloodType, onValueChange = { newBloodType = it })

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (newWeight.isNotEmpty()) viewModel.updateWeight(newWeight)
                    if (newHeight.isNotEmpty()) viewModel.updateHeight(newHeight)
                    if (newBloodType.isNotEmpty()) viewModel.updateBloodType(newBloodType)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simpan")
            }
        }
    }
}

@Composable
fun EditableField(title: String, hint: String, value: String, onValueChange: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(hint) },
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}
