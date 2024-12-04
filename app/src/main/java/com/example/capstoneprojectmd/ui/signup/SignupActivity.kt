package com.example.capstoneprojectmd.ui.signup

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.capstoneprojectmd.MainActivity
import com.example.capstoneprojectmd.R
import com.example.capstoneprojectmd.databinding.ActivitySignupBinding
import com.example.capstoneprojectmd.ui.signin.SignInActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val signupViewModel: SignupViewModel by viewModels()
    private val RC_SIGN_IN = 9001 // Request code for Google Sign-In

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActions()

        // Observing ViewModel
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
                    showDialog("Signup Failed", status.message, true)
                }
            }
        })

        // Set up password visibility toggle
        setupPasswordVisibilityToggle()
    }

    private fun setupActions() {
        binding.signupButton.setOnClickListener {
            val fullName = binding.fullNameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()
            if (fullName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                signupViewModel.registerWithEmailPassword(fullName, email, password)
            } else {
                showDialog("Invalid Input", "Please fill out all fields.", true)
            }
        }

        binding.loginTab.setOnClickListener {
            finish() // Close SignupActivity to return to the SignInActivity
        }
    }

    private fun setupPasswordVisibilityToggle() {
        val passwordInput = binding.passwordInput
        val passwordVisibilityToggle = binding.passwordVisibilityToggle

        passwordVisibilityToggle.setOnClickListener {
            // Mengecek apakah password sedang disembunyikan atau tidak
            if (passwordInput.transformationMethod is PasswordTransformationMethod) {
                // Ubah ke teks biasa (show password)
                passwordInput.transformationMethod = HideReturnsTransformationMethod.getInstance()
                passwordVisibilityToggle.setImageResource(R.drawable.ic_visibility_off) // Ganti ikon ke "Hide"
            } else {
                // Ubah ke password (hide password)
                passwordInput.transformationMethod = PasswordTransformationMethod.getInstance()
                passwordVisibilityToggle.setImageResource(R.drawable.ic_visibility) // Ganti ikon ke "Show"
            }

            // Update kursor agar tetap berada di akhir teks
            passwordInput.setSelection(passwordInput.text.length)
        }
    }

    private fun signUpWithGoogle() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    signupViewModel.registerWithGoogle(account)
                }
            } catch (e: ApiException) {
                showDialog("Google Sign-Up Failed", "Error: ${e.localizedMessage}", true)
            }
        }
    }

    private fun navigateToMainActivity(user: FirebaseUser) {
        AlertDialog.Builder(this).apply {
            setTitle("Welcome!")
            setMessage("Registration successful! User: ${user.email}")
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                startActivity(Intent(this@SignupActivity, SignInActivity::class.java))
                finish()
            }
            create()
            show()
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
