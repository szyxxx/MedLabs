package com.tubes.medlab.auth

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.tubes.medlab.dashboard.DashboardScreen
import com.tubes.medlab.news.NewsScreen
import com.tubes.medlab.permission.NotificationPermissionScreen
import com.tubes.medlab.profile.ProfileScreen
import com.tubes.medlab.profile.EditProfileScreen
import com.tubes.medlab.schedule.AddScheduleScreen
import com.tubes.medlab.schedule.EditScheduleScreen
import com.tubes.medlab.schedule.ScheduleScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(navController: NavHostController, startDestination: String) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") { LoginScreen(navController = navController) }
        composable("signup") { SignUpScreen(navController = navController) }
        composable("notification_permission") { NotificationPermissionScreen(navController = navController) }
        composable("dashboard") { DashboardScreen(navController = navController) }
        composable("schedule") { ScheduleScreen(navController = navController) }
        composable("add_schedule") { AddScheduleScreen(navController = navController) }
        composable("news") { NewsScreen(navController = navController) }
        composable("profile") { ProfileScreen(navController = navController) }
        composable("edit_schedule/{scheduleId}") { backStackEntry ->
            val scheduleId = backStackEntry.arguments?.getString("scheduleId") ?: return@composable
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            Log.d("NavHost", "Navigating to EditScheduleScreen with scheduleId: $scheduleId and userId: $userId")
            EditScheduleScreen(navController = navController, scheduleId = scheduleId, userId = userId)
        }
    }
}
