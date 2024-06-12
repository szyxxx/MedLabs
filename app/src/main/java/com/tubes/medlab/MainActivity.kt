package com.tubes.medlab

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.tubes.medlab.auth.NavGraph
import com.tubes.medlab.auth.SharedPreferenceUtil
import com.tubes.medlab.ui.theme.MedLabTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @ExperimentalFoundationApi
    @ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            MedLabTheme {
                val navController = rememberNavController()
                MedLabApp(navController = navController, context = this)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MedLabApp(navController: androidx.navigation.NavHostController, context: android.content.Context) {
    // Check if the user is logged in and navigate to the appropriate screen
    val startDestination = if (SharedPreferenceUtil.isLoggedIn(context)) "dashboard" else "login"
    NavGraph(navController = navController, startDestination = startDestination)
}
