package com.example.capstoneprojectmd

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class MainViewModel : ViewModel() {

    private val _isUserLoggedIn = MutableLiveData<Boolean>()
    val isUserLoggedIn: LiveData<Boolean> get() = _isUserLoggedIn

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        try {
            val user = FirebaseAuth.getInstance().currentUser
            _isUserLoggedIn.value = user != null
        } catch (e: Exception) {
            _isUserLoggedIn.value = false
        }
    }

    fun logout() {
        try {
            FirebaseAuth.getInstance().signOut()
            _isUserLoggedIn.value = false
        } catch (e: Exception) {

        }
    }

    fun refreshLoginStatus() {
        try {
            val user = FirebaseAuth.getInstance().currentUser
            _isUserLoggedIn.value = user != null
        } catch (e: Exception) {
            _isUserLoggedIn.value = false
        }
    }
}
