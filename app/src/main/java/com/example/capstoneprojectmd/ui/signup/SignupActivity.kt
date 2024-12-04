package com.example.capstoneprojectmd.ui.signup

import android.content.Intent
import android.os.Bundle
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
                    binding.googleButton.isEnabled = false
                }
                is SignupStatus.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.signupButton.isEnabled = true
                    binding.googleButton.isEnabled = true
                    navigateToMainActivity(status.user)
                }
                is SignupStatus.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.signupButton.isEnabled = true
                    binding.googleButton.isEnabled = true
                    showDialog("Signup Failed", status.message, true)
                }
            }
        })
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

        binding.googleButton.setOnClickListener {
            signUpWithGoogle()
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
