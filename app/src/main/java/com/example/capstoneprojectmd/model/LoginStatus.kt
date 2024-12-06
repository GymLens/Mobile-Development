package com.example.capstoneprojectmd.model

import com.google.firebase.auth.FirebaseUser

sealed class LoginStatus {
    object Loading : LoginStatus()
    data class Success(val user: FirebaseUser) : LoginStatus()
    data class Error(val message: String) : LoginStatus()
}
