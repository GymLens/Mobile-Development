package com.example.capstoneprojectmd.ui.signin

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.capstoneprojectmd.MainActivity
import com.example.capstoneprojectmd.R
import com.example.capstoneprojectmd.databinding.ActivitySigninBinding
import com.example.capstoneprojectmd.model.LoginStatus
import com.example.capstoneprojectmd.ui.chatbot.ChatFragment
import com.example.capstoneprojectmd.ui.signup.SignupActivity
import com.google.firebase.auth.FirebaseUser
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding
    private val signInViewModel: SignInViewModel by viewModels()

    private val RC_SIGN_IN = 9001 // Request code for Google Sign-In

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()

        // Observasi status login dari ViewModel
        signInViewModel.loginStatus.observe(this, Observer { status ->
            when (status) {
                is LoginStatus.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.loginButton.isEnabled = false
                }
                is LoginStatus.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.loginButton.isEnabled = true
                    navigateToChatActivity(status.user)
                }
                is LoginStatus.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.loginButton.isEnabled = true
                    showDialog("Login Failed", status.message, true)
                }
            }
        })

        // Set up password visibility toggle
        setupPasswordVisibilityToggle()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                signInViewModel.loginWithEmailPassword(email, password)
            } else {
                showDialog("Invalid Input", "Please fill out all fields.", true)
            }
        }

        binding.registerTab.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }

        binding.forgotPassword.setOnClickListener {
            showDialog("Forgot Password", "Reset link has been sent to your email.", false)
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

    private fun signInWithGoogle() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // ID dari google-services.json
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
                    // Sign in with the Google account
                    signInViewModel.loginWithGoogle(account)
                }
            } catch (e: ApiException) {
                showDialog("Google Sign-In Failed", "Error: ${e.localizedMessage}", true)
            }
        }
    }

    // SignInActivity.kt

    // Di dalam method navigateToChatActivity (atau bagian yang menavigasi ke MainActivity atau BerandaFragment)
    private fun navigateToChatActivity(user: FirebaseUser) {
        AlertDialog.Builder(this).apply {
            setTitle("Welcome!")
            val displayName = user.displayName ?: "User" // Mendapatkan nama pengguna
            setMessage("Login successful! Welcome $displayName!")
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                // Mengirim nama pengguna ke fragment atau activity lain
                val intent = Intent(this@SignInActivity, MainActivity::class.java)
                intent.putExtra("USER_NAME", displayName) // Kirim nama pengguna ke MainActivity
                startActivity(intent)
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
