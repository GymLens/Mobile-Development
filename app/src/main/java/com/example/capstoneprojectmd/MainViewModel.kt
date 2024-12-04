package com.example.capstoneprojectmd

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class MainViewModel : ViewModel() {

    // LiveData untuk status login pengguna
    private val _isUserLoggedIn = MutableLiveData<Boolean>()
    val isUserLoggedIn: LiveData<Boolean> get() = _isUserLoggedIn

    init {
        checkLoginStatus()
    }

    // Memeriksa status login pengguna
    private fun checkLoginStatus() {
        try {
            val user = FirebaseAuth.getInstance().currentUser
            _isUserLoggedIn.value = user != null
        } catch (e: Exception) {
            // Menangani kesalahan jika terjadi
            // Bisa menggunakan log untuk mencatat kesalahan atau menampilkan toast error (Jika perlu)
            _isUserLoggedIn.value = false
        }
    }

    // Fungsi untuk logout pengguna
    fun logout() {
        try {
            FirebaseAuth.getInstance().signOut()
            _isUserLoggedIn.value = false
        } catch (e: Exception) {
            // Menangani kesalahan jika terjadi saat logout
            // Log atau beri tahu kesalahan kepada pengguna jika diperlukan
        }
    }

    // Fungsi untuk login ulang dan memeriksa statusnya
    fun refreshLoginStatus() {
        try {
            val user = FirebaseAuth.getInstance().currentUser
            _isUserLoggedIn.value = user != null
        } catch (e: Exception) {
            // Menangani kesalahan saat memeriksa status login
            _isUserLoggedIn.value = false
        }
    }
}
