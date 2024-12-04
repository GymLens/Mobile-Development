package com.example.capstoneprojectmd.ui.signin

import android.content.Intent
import android.os.Bundle
import android.text.InputType
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
            // Toggle the inputType between visible and hidden password
            if (passwordInput.inputType == InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                // Show password
                passwordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
                passwordVisibilityToggle.setImageResource(R.drawable.ic_visibility_off) // Change icon to "Hide"
            } else {
                // Hide password
                passwordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                passwordVisibilityToggle.setImageResource(R.drawable.ic_visibility) // Change icon to "Show"
            }

            // Move the cursor to the end of the input
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

    private fun navigateToChatActivity(user: FirebaseUser) {
        AlertDialog.Builder(this).apply {
            setTitle("Welcome!")
            setMessage("Login successful! User: ${user.email}")
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                startActivity(Intent(this@SignInActivity, MainActivity::class.java))
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
