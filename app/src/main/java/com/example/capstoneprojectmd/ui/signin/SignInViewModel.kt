package com.example.capstoneprojectmd.ui.signin

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.capstoneprojectmd.model.LoginStatus
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class SignInViewModel : ViewModel() {

    private val _loginStatus = MutableLiveData<LoginStatus>()
    val loginStatus: LiveData<LoginStatus> = _loginStatus

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Email/Password login
    fun loginWithEmailPassword(email: String, password: String) {
        _loginStatus.value = LoginStatus.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        _loginStatus.value = LoginStatus.Success(user)
                    } else {
                        _loginStatus.value = LoginStatus.Error("Login failed, user not found.")
                    }
                } else {
                    val exception = task.exception
                    when (exception) {
                        // Handle specific Firebase exceptions
                        is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> {
                            _loginStatus.value = LoginStatus.Error("Invalid credentials.")
                        }
                        is com.google.firebase.auth.FirebaseAuthUserCollisionException -> {
                            _loginStatus.value = LoginStatus.Error("User already exists.")
                        }
                        else -> {
                            _loginStatus.value = LoginStatus.Error(exception?.message ?: "An error occurred.")
                        }
                    }
                }
            }
    }

    // Google Sign-In
    fun loginWithGoogle(account: GoogleSignInAccount) {
        _loginStatus.value = LoginStatus.Loading

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        _loginStatus.value = LoginStatus.Success(user)
                    } else {
                        _loginStatus.value = LoginStatus.Error("Google Sign-In failed, user not found.")
                    }
                } else {
                    val exception = task.exception
                    _loginStatus.value = LoginStatus.Error(exception?.message ?: "An error occurred.")
                }
            }
    }
    fun signOut() {
        auth.signOut()
        _loginStatus.value = LoginStatus.Error("User signed out.")
    }
}
