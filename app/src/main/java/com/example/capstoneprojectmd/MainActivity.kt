package com.example.capstoneprojectmd

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
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

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_beranda,
                R.id.navigation_chatbot,
                R.id.navigation_scan,
                R.id.navigation_run,
                R.id.navigation_profile
            )
        )

        // If you don't want an ActionBar, you can skip this line
        // setupActionBarWithNavController(navController, appBarConfiguration)

        // Set up the Bottom Navigation with NavController
        navView.setupWithNavController(navController)
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, SignInActivity::class.java))
        finish()  // Close MainActivity to prevent the user from returning to it
    }
}
