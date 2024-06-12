package com.tubes.medlab.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Customize form fields based on editType
            Text(text = "Edit Profile", style = MaterialTheme.typography.displayLarge)

            // Example form field for editing weight
            EditableField("Berat Badan", "Masukkan berat badan baru", onSave = { newWeight ->
                viewModel.updateWeight(newWeight)
                navController.popBackStack()
            })

            // Example form field for editing height
            EditableField("Tinggi Badan", "Masukkan tinggi badan baru", onSave = { newHeight ->
                viewModel.updateHeight(newHeight)
                navController.popBackStack()
            })

            // Example form field for editing blood type
            EditableField("Golongan Darah", "Masukkan golongan darah baru", onSave = { newBloodType ->
                viewModel.updateBloodType(newBloodType)
                navController.popBackStack()
            })
        }
    }
}

@Composable
fun EditableField(title: String, hint: String, onSave: (String) -> Unit) {
    var value by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = value,
            onValueChange = { value = it },
            label = { Text(hint) },
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Button(
            onClick = { onSave(value) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Simpan")
        }
    }
}
