package com.example.capstoneprojectmd.ui.signup

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.capstoneprojectmd.R
import com.example.capstoneprojectmd.databinding.ActivitySignupBinding
import com.example.capstoneprojectmd.ui.signin.SignInActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val signupViewModel: SignupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActions()

        signupViewModel.signupStatus.observe(this, Observer { status ->
            when (status) {
                is SignupStatus.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.signupButton.isEnabled = false
                }
                is SignupStatus.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.signupButton.isEnabled = true
                    navigateToMainActivity(status.user)
                }
                is SignupStatus.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.signupButton.isEnabled = true
                    showDialog("Pendaftaran Gagal", status.message, true)
                }
            }
        })

        setupPasswordVisibilityToggle()
        setupTabNavigation()
    }

    private fun setupActions() {
        binding.signupButton.setOnClickListener {
            val fullName = binding.fullNameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()
            if (fullName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                signupViewModel.registerWithEmailPassword(fullName, email, password)
            } else {
                showDialog("Input Tidak Valid", "Silakan lengkapi semua kolom.", true)
            }
        }

        binding.loginTab.setOnClickListener {
            finish()
        }
    }

    private fun setupTabNavigation() {
        binding.registerTab.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }

        binding.loginTab.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }

    private fun setupPasswordVisibilityToggle() {
        val passwordInput = binding.passwordInput
        val passwordVisibilityToggle = binding.passwordVisibilityToggle

        passwordVisibilityToggle.setOnClickListener {
            if (passwordInput.transformationMethod is PasswordTransformationMethod) {
                passwordInput.transformationMethod = HideReturnsTransformationMethod.getInstance()
                passwordVisibilityToggle.setImageResource(R.drawable.ic_visibility_off)
            } else {
                passwordInput.transformationMethod = PasswordTransformationMethod.getInstance()
                passwordVisibilityToggle.setImageResource(R.drawable.ic_visibility)
            }

            passwordInput.setSelection(passwordInput.text.length)
        }
    }

    private fun navigateToMainActivity(user: FirebaseUser) {
        val fullName = binding.fullNameInput.text.toString()

        saveFullNameToFirebase(fullName)

        AlertDialog.Builder(this).apply {
            setTitle("Selamat!")
            setMessage("Pendaftaran Berhasil! Email: ${user.email}")
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()

                val email = user.email
                val intent = Intent(this@SignupActivity, SignInActivity::class.java)
                intent.putExtra("EMAIL", email)
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }

    private fun saveFullNameToFirebase(fullName: String) {
        val user = FirebaseAuth.getInstance().currentUser

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(fullName)
            .build()

        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                } else {
                }
            }
    }

    private fun showDialog(title: String, message: String, isError: Boolean) {
        AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            create()
            show()
        }
    }
}
