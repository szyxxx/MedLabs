package com.tubes.medlab.permission

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tubes.medlab.auth.SharedPreferenceUtil

@Composable
fun NotificationPermissionScreen(navController: NavController) {
    val context = LocalContext.current
    val isNotificationPermissionGranted = SharedPreferenceUtil.hasNotificationPermission(context)

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted
            SharedPreferenceUtil.setNotificationPermission(context, true)
            navController.popBackStack()
        } else {
            // Permission denied
            // Handle accordingly, you can show a message or take other actions
        }
    }

    if (!isNotificationPermissionGranted) {
        LaunchedEffect(Unit) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_NOTIFICATION_POLICY)
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    } else {
        navController.navigate("dashboard")
    }
}
