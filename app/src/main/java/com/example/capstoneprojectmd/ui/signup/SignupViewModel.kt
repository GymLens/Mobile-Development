package com.example.capstoneprojectmd.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

sealed class SignupStatus {
    object Loading : SignupStatus()
    data class Success(val user: FirebaseUser) : SignupStatus()
    data class Error(val message: String) : SignupStatus()
}

class SignupViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _signupStatus = MutableLiveData<SignupStatus>()
    val signupStatus: LiveData<SignupStatus> get() = _signupStatus

    fun registerWithEmailPassword(fullName: String, email: String, password: String) {
        _signupStatus.value = SignupStatus.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    _signupStatus.value = SignupStatus.Success(user!!)
                } else {
                    _signupStatus.value = SignupStatus.Error(task.exception?.message ?: "Pendaftaran gagal.")
                }
            }
    }

    fun registerWithGoogle(account: GoogleSignInAccount) {
        _signupStatus.value = SignupStatus.Loading
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    _signupStatus.value = SignupStatus.Success(user!!)
                } else {
                    _signupStatus.value = SignupStatus.Error(task.exception?.message ?: "Pendaftaran Google gagal.")
                }
            }
    }
}
