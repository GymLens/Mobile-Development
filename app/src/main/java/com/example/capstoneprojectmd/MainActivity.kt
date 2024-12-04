package com.example.capstoneprojectmd

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.capstoneprojectmd.databinding.ActivityMainBinding
import com.example.capstoneprojectmd.ui.main.MainViewModel
import com.example.capstoneprojectmd.ui.signin.SignInActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.bottomnavigation.BottomNavigationView
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the Bottom Navigation
        val bottomNavigation: BottomNavigationView = binding.navView

        // Find the NavHostFragment and get the NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mobile_navigation) as NavHostFragment
        navController = navHostFragment.navController

        // Set up the Navigation Controller with Bottom Navigation
        NavigationUI.setupWithNavController(bottomNavigation, navController)

        // Observe the user login status
        viewModel.isUserLoggedIn.observe(this) { isLoggedIn ->
            if (isLoggedIn) {
                val user = FirebaseAuth.getInstance().currentUser
                showWelcomeMessage(user?.email)
            } else {
                navigateToLogin()
            }
        }

        // Handle Logout
        binding.logoutButton.setOnClickListener {
            viewModel.logout()
            navigateToLogin()
        }

        // Handle navigation when bottom navigation item is selected
        bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_chatbot -> {
                    navController.navigate(R.id.navigation_chatbot)
                    true
                }
                // Handle other menu items here
                else -> false
            }
        }
    }

    private fun showWelcomeMessage(email: String?) {
        binding.welcomeMessage.text = "Welcome, $email!"
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }
}
