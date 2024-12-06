package com.example.capstoneprojectmd.ui.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.capstoneprojectmd.model.LoginStatus
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class SignInViewModel : ViewModel() {

    private val _loginStatus = MutableLiveData<LoginStatus>()
    val loginStatus: LiveData<LoginStatus> = _loginStatus

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun loginWithEmailPassword(email: String, password: String) {
        _loginStatus.value = LoginStatus.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        _loginStatus.value = LoginStatus.Success(user)
                    } else {
                        _loginStatus.value = LoginStatus.Error("Login gagal, pengguna tidak ditemukan.")
                    }
                } else {
                    val exception = task.exception
                    when (exception) {
                        // Handle specific Firebase exceptions
                        is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> {
                            _loginStatus.value = LoginStatus.Error("Kredensial tidak valid.")
                        }
                        is com.google.firebase.auth.FirebaseAuthUserCollisionException -> {
                            _loginStatus.value = LoginStatus.Error("Pengguna sudah ada.")
                        }
                        else -> {
                            _loginStatus.value = LoginStatus.Error(exception?.message ?: "Terjadi kesalahan.")
                        }
                    }
                }
            }
    }

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
                        _loginStatus.value = LoginStatus.Error("Google Sign-In gagal, pengguna tidak ditemukan.")
                    }
                } else {
                    val exception = task.exception
                    _loginStatus.value = LoginStatus.Error(exception?.message ?: "Terjadi kesalahan.")
                }
            }
    }

    fun signOut() {
        auth.signOut()
        _loginStatus.value = LoginStatus.Error("Pengguna telah keluar.")
    }
}
