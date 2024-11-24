package com.example.capstoneprojectmd.ui.signup

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.capstoneprojectmd.R
import com.example.capstoneprojectmd.databinding.ActivityRegisterBinding

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupActions()
    }

    private fun setupView() {
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
        // Tab switch logic
        binding.registerTab.setOnClickListener {
            binding.registerTab.setBackgroundResource(R.drawable.tab_selected_background)
            binding.loginTab.setBackgroundResource(R.drawable.tab_unselected_background)
        }

        binding.loginTab.setOnClickListener {
            binding.registerTab.setBackgroundResource(R.drawable.tab_unselected_background)
            binding.loginTab.setBackgroundResource(R.drawable.tab_selected_background)
        }

        // Validate and register user
        binding.loginButton.setOnClickListener {
            val fullName = binding.fullNameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if (TextUtils.isEmpty(fullName)) {
                showError(binding.fullNameInput, "Nama tidak boleh kosong")
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showError(binding.emailInput, "Email tidak valid")
                return@setOnClickListener
            }

            if (password.length < 8) {
                showError(binding.passwordInput, "Password minimal 8 karakter")
                return@setOnClickListener
            }

            // Show success message
            AlertDialog.Builder(this).apply {
                setTitle("Pendaftaran Berhasil")
                setMessage("Selamat, $fullName! Akun Anda dengan email $email berhasil dibuat.")
                setPositiveButton("OK") { _, _ ->
                    finish() // Close activity or navigate back
                }
                create()
                show()
            }
        }

        // Forgot password action
        binding.forgotPassword.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("Lupa Password")
                setMessage("Fitur ini akan segera tersedia.")
                setPositiveButton("OK", null)
                create()
                show()
            }
        }
    }

    private fun showError(inputField: EditText, message: String) {
        inputField.error = message
        inputField.requestFocus()
    }
}
