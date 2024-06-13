package com.tubes.medlab.profile

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore = Firebase.firestore
    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> get() = _userName

    private val _weight = MutableLiveData<String>()
    val weight: LiveData<String> get() = _weight

    private val _height = MutableLiveData<String>()
    val height: LiveData<String> get() = _height

    private val _bloodType = MutableLiveData<String>()
    val bloodType: LiveData<String> get() = _bloodType

    private val _notificationsEnabled = MutableLiveData<Boolean>()
    val notificationsEnabled: LiveData<Boolean> get() = _notificationsEnabled

    init {
        loadUserData()
    }

    private fun loadUserData() {
        // Load user data from SharedPreferences or Firestore if needed
        _userName.value = auth.currentUser?.displayName ?: "Unknown User"
        _weight.value = sharedPreferences.getString("weight", "Not Set")
        _height.value = sharedPreferences.getString("height", "Not Set")
        _bloodType.value = sharedPreferences.getString("blood_type", "Not Set")
        _notificationsEnabled.value = sharedPreferences.getBoolean("notifications_enabled", false)
    }

    fun toggleNotifications() {
        val newStatus = !(_notificationsEnabled.value ?: false)
        _notificationsEnabled.value = newStatus
        sharedPreferences.edit().putBoolean("notifications_enabled", newStatus).apply()
    }


    fun logout() {
        auth.signOut()
    }

    fun updateWeight(newWeight: String) {
        _weight.value = newWeight
        sharedPreferences.edit().putString("weight", newWeight).apply()
        // Update the weight in Firestore
        updateUserProfileData(mapOf("weight" to newWeight))
    }

    fun updateHeight(newHeight: String) {
        _height.value = newHeight
        sharedPreferences.edit().putString("height", newHeight).apply()
        // Update the height in Firestore
        updateUserProfileData(mapOf("height" to newHeight))
    }

    fun updateBloodType(newBloodType: String) {
        _bloodType.value = newBloodType
        sharedPreferences.edit().putString("blood_type", newBloodType).apply()
        // Update the blood type in Firestore
        updateUserProfileData(mapOf("blood_type" to newBloodType))
    }

    private fun updateUserProfileData(updatedData: Map<String, Any>) {
        auth.currentUser?.uid?.let { uid ->
            firestore.collection("medlabs-$uid")
                .document("userData")
                .update(updatedData)
                .addOnSuccessListener {
                    // Update successful
                }
                .addOnFailureListener { e ->
                    // Handle failure
                }
        }
    }
}
