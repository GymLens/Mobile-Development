package com.example.capstoneprojectmd

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.capstoneprojectmd.databinding.ActivityMainBinding
import com.example.capstoneprojectmd.ui.main.MainViewModel
import com.example.capstoneprojectmd.ui.signin.SignInActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Observe the user login status
        viewModel.isUserLoggedIn.observe(this) { isLoggedIn ->
            if (isLoggedIn) {
                val user = FirebaseAuth.getInstance().currentUser
                showWelcomeMessage(user?.email)
            } else {
                navigateToLogin()
            }
        }

        // Logout button
        binding.logoutButton.setOnClickListener {
            viewModel.logout()
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            navigateToLogin()
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