package com.example.capstoneprojectmd.model

import com.google.firebase.auth.FirebaseUser

sealed class LoginStatus {
    object Loading : LoginStatus() // Login in progress
    data class Success(val user: FirebaseUser) : LoginStatus() // Successful login with FirebaseUser
    data class Error(val message: String) : LoginStatus() // Login error with a message
}
