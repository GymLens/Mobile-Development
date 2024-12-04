package com.example.capstoneprojectmd.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.firebase.auth.FirebaseAuth

class MainViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // LiveData to observe user login status
    val isUserLoggedIn: LiveData<Boolean> = liveData {
        val user = auth.currentUser
        emit(user != null)  // Emit true if user is logged in, false otherwise
    }

    // Logout user
    fun logout() {
        auth.signOut()
    }
}
