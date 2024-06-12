package com.tubes.medlab.auth

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tubes.medlab.dashboard.DashboardScreen
import com.tubes.medlab.news.NewsScreen
import com.tubes.medlab.permission.NotificationPermissionScreen
import com.tubes.medlab.profile.ProfileScreen
import com.tubes.medlab.profile.EditProfileScreen
import com.tubes.medlab.schedule.AddScheduleScreen
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
        composable("profile") { ProfileScreen(navController = navController)}
        composable("edit_profile") { EditProfileScreen(navController = navController)}
    }
}