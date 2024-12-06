package com.example.capstoneprojectmd

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.capstoneprojectmd.databinding.ActivityMainBinding
import com.example.capstoneprojectmd.ui.beranda.BerandaFragment
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

        navView.setupWithNavController(navController)

        // Retrieve fullName from Firebase Authentication
        val user = FirebaseAuth.getInstance().currentUser
        val fullName = user?.displayName ?: "Guest"  // Get full name from Firebase profile, or "Guest" if null

        // Ensure full name is updated correctly
        if (fullName == "Guest") {
            Log.d("MainActivity", "User's display name is still 'Guest' - Check profile update.")
        }

        // Pass fullName to BerandaFragment using a Bundle
        val bundle = Bundle()
        bundle.putString("USER_NAME", fullName)  // Pass fullName to BerandaFragment
        navController.navigate(R.id.navigation_beranda, bundle)
    }
}
