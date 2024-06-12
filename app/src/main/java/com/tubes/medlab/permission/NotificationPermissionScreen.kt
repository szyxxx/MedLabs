package com.tubes.medlab.permission

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tubes.medlab.auth.SharedPreferenceUtil

@Composable
fun NotificationPermissionScreen(navController: NavController) {
    val context = LocalContext.current
    val isNotificationPermissionGranted = SharedPreferenceUtil.hasNotificationPermission(context)

    if (!isNotificationPermissionGranted) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Allow Notification Permission") },
            text = { Text("Please allow notification permission to receive important updates.") },
            confirmButton = {
                Button(
                    onClick = {
                        // Set notification permission to true when user clicks Allow
                        SharedPreferenceUtil.setNotificationPermission(context, true)
                        navController.popBackStack() // Go back to previous screen
                    }
                ) {
                    Text("Allow")
                }
            }
        )
    } else {
        // If notification permission is already granted, navigate back to previous screen
        navController.navigate("dashboard")
    }
}
