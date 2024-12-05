package com.example.capstoneprojectmd

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.capstoneprojectmd.databinding.ActivityMainBinding
import com.example.capstoneprojectmd.ui.signin.SignInActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            // Setup BottomNavigationView and NavController
            val navView: BottomNavigationView = binding.navView

            // Inisialisasi NavController
            val navController = findNavController(R.id.nav_host_fragment_activity_main)

            // Setup AppBarConfiguration
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_beranda,
                    R.id.navigation_history,
                    R.id.navigation_scan,
                    R.id.navigation_run,
                    R.id.navigation_profile
                )
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)

            // Observe the user login status
            viewModel.isUserLoggedIn.observe(this) { isLoggedIn ->
                if (isLoggedIn) {
                    val user = FirebaseAuth.getInstance().currentUser
                    showWelcomeMessage(user?.email)
                    // Navigasi ke fragment "Beranda" setelah login berhasil
                    navController.navigate(R.id.navigation_beranda)  // Pastikan ID fragment sesuai dengan nav_graph.xml
                } else {
                    navigateToLogin()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Menampilkan pesan sambutan kepada pengguna
    private fun showWelcomeMessage(email: String?) {
        try {
            binding.welcomeMessage.text = "Welcome, $email!"
        } catch (e: Exception) {
            // Menangani kesalahan UI jika ada
            Toast.makeText(this, "Error displaying welcome message: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Navigasi ke activity login jika pengguna belum login
    private fun navigateToLogin() {
        try {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()  // Menutup MainActivity agar pengguna tidak bisa kembali ke halaman utama
        } catch (e: Exception) {
            // Menangani kesalahan navigasi
            Toast.makeText(this, "Error navigating to login: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
