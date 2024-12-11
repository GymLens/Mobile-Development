package com.example.capstoneprojectmd.ui.password

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.capstoneprojectmd.databinding.ActivityForgotPasswordBinding
import com.example.capstoneprojectmd.ui.signin.SignInActivity
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSubmit.setOnClickListener {
            val inputEmail = binding.etEmail.text.toString().trim()

            if (isInputValid(inputEmail)) {
                sendPasswordResetEmail(inputEmail)
            }
        }
    }

    private fun isInputValid(email: String): Boolean {
        when {
            email.isBlank() -> {
                Toast.makeText(this, "Email tidak boleh kosong.", Toast.LENGTH_SHORT).show()
                return false
            }
            else -> return true
        }
    }

    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Email untuk mereset password sudah dikirim.", Toast.LENGTH_SHORT).show()
                navigateToSignIn()
            } else {
                Toast.makeText(this, "Gagal mengirim email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToSignIn() {
        val intent = Intent(this, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
