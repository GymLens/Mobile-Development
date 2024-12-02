package com.example.capstoneprojectmd.ui.splashscreen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.capstoneprojectmd.MainActivity
import com.example.capstoneprojectmd.R // Pastikan ini adalah R yang benar untuk referensi layout
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        // Menggunakan Coroutine untuk delay selama 3 detik
        lifecycleScope.launch {
            delay(3000) // Menunggu selama 3 detik
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish() // Menutup aktivitas Splash Screen
        }
    }
}
