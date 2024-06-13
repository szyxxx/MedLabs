package com.tubes.medlab.auth

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val activity = context as Activity
    val auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoginButtonEnabled by remember { mutableStateOf(true) }

    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("464885114288-gjo48kprb59qnk7bl3n54q7j2kmmj0ln.apps.googleusercontent.com")
            .requestEmail()
            .build()
        GoogleSignIn.getClient(activity, gso)
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.result
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential).addOnCompleteListener { authResult ->
                if (authResult.isSuccessful) {
                    val user = auth.currentUser
                    val uid = user?.uid
                    val nama = user?.displayName
                    val email = user?.email

                    if (uid != null) {
                        val nama = user.displayName
                        val email = user.email

                        val userData = hashMapOf(
                            "UID" to uid,
                            "Nama" to nama,
                            "Email" to email,
                            "profileImage" to "",
                            "beratBadan" to "",
                            "tinggiBadan" to "",
                            "golonganDarah" to "",
                            "kadarGula" to ""
                        )

                        val db = Firebase.firestore
                        db.collection("medlabs-$uid").document("userData").set(userData)
                            .addOnSuccessListener {
                                Log.d("Firebase", "Data pengguna berhasil disimpan di Firestore")
                            }
                            .addOnFailureListener { e ->
                                Log.w("Firebase", "Gagal menyimpan data pengguna di Firestore", e)
                            }

                        // Set the logged-in status to true
                        SharedPreferenceUtil.setLoggedIn(context, true)
                        navController.navigate("notification_permission")
                    } else {
                        // Handle the case where UID is null
                        Log.e("Login", "UID is null")
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                if (isLoginButtonEnabled) performLogin(auth, email, password, context, navController) { isEnabled -> isLoginButtonEnabled = isEnabled }
            },
            enabled = isLoginButtonEnabled
        ) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { navController.navigate("signup") }) {
            Text("Sign Up")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                googleSignInLauncher.launch(googleSignInClient.signInIntent)
            }
        ) {
            Text("Continue with Google")
        }
    }
}

private fun performLogin(
    auth: FirebaseAuth,
    email: String,
    password: String,
    context: Context, // Tambahkan parameter context di sini
    navController: NavController,
    enableButton: (Boolean) -> Unit
) {
    // Disable the login button to prevent multiple login requests
    enableButton(false)
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            enableButton(true)
            if (task.isSuccessful) {
                // Set the logged-in status to true
                SharedPreferenceUtil.setLoggedIn(context, true)
                navController.navigate("notification_permission")
            } else {
                // Handle login failed
                Log.e("Login", "Email sign in failed", task.exception)
            }
        }
}


object SharedPreferenceUtil {
    private const val PREF_NAME = "medlab_prefs"
    private const val KEY_LOGGED_IN = "logged_in"
    private const val KEY_UID = "uid"
    private const val KEY_NOTIFICATION_PERMISSION = "notification_permission"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun isLoggedIn(context: Context): Boolean {
        val prefs = getSharedPreferences(context)
        return prefs.getBoolean(KEY_LOGGED_IN, false)
    }

    fun getUid(context: Context): String? {
        val prefs = getSharedPreferences(context)
        return prefs.getString(KEY_UID, null)
    }

    fun setUid(context: Context, uid: String) {
        val prefs = getSharedPreferences(context)
        prefs.edit().putString(KEY_UID, uid).apply()
    }

    fun setLoggedIn(context: Context, loggedIn: Boolean) {
        val prefs = getSharedPreferences(context)
        prefs.edit().putBoolean(KEY_LOGGED_IN, loggedIn).apply()
    }

    fun hasNotificationPermission(context: Context): Boolean {
        val prefs = getSharedPreferences(context)
        return prefs.getBoolean(KEY_NOTIFICATION_PERMISSION, false)
    }

    fun setNotificationPermission(context: Context, hasPermission: Boolean) {
        val prefs = getSharedPreferences(context)
        prefs.edit().putBoolean(KEY_NOTIFICATION_PERMISSION, hasPermission).apply()
    }
}