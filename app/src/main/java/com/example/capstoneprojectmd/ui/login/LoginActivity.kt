package com.example.capstoneprojectmd.ui.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.capstoneprojectmd.MainActivity
import com.example.capstoneprojectmd.data.pref.UserModel
import com.example.capstoneprojectmd.databinding.ActivityLoginBinding
import com.example.capstoneprojectmd.ui.ViewModelFactory


class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupActions()
    }

    private fun setupView() {
        // Hide system bars
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupActions() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            // Validate email
            if (!isValidEmail(email)) {
                binding.emailInput.error = "Email tidak valid"
                return@setOnClickListener
            } else {
                binding.emailInput.error = null
            }

            // Validate password
            if (password.isEmpty()) {
                binding.passwordInput.error = "Password tidak boleh kosong"
                return@setOnClickListener
            } else if (password.length < 8) {
                binding.passwordInput.error = "Password harus lebih dari 8 karakter"
                return@setOnClickListener
            } else {
                binding.passwordInput.error = null
            }

            performLogin(email, password)
        }

        binding.googleButton.setOnClickListener {
            Toast.makeText(this, "Login dengan Google coming soon", Toast.LENGTH_SHORT).show()
        }

        binding.forgotPassword.setOnClickListener {
            Toast.makeText(this, "Fitur lupa password coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun performLogin(email: String, password: String) {
        // Simulate login success and save session
        viewModel.saveSession(UserModel(email, "sample_token"))
        AlertDialog.Builder(this).apply {
            setTitle("Berhasil Login")
            setMessage("Selamat datang! Anda berhasil login.")
            setPositiveButton("Lanjutkan") { _, _ ->
                navigateToMainActivity()
            }
            create()
            show()
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
