package com.example.capstoneprojectmd.ui.password

import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.capstoneprojectmd.R
import com.example.capstoneprojectmd.databinding.ActivityChangePasswordBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private var isOldPasswordVisible = false
    private var isNewPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSavePassword.setOnClickListener {
            val oldPassword = binding.etOldPassword.text.toString()
            val newPassword = binding.etNewPassword.text.toString()

            if (validatePassword(newPassword)) {
                updatePassword(oldPassword, newPassword)
            }
        }

        setupPasswordVisibility()
    }

    private fun validatePassword(newPassword: String): Boolean {
        if (newPassword.isEmpty()) {
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        if (newPassword.length < 6) {
            Toast.makeText(this, "Password too short", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun updatePassword(oldPassword: String, newPassword: String) {
        val user = auth.currentUser
        val email = user?.email ?: ""

        if (user != null && email.isNotEmpty()) {
            // Re-authenticate the user with the old password
            val credential = EmailAuthProvider.getCredential(email, oldPassword)

            user.reauthenticate(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Update to the new password
                    user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this, "Password update failed: ${updateTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Re-authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupPasswordVisibility() {
        // Toggle visibility for old password
        binding.passwordVisibilityToggleOld.setOnClickListener {
            isOldPasswordVisible = !isOldPasswordVisible
            binding.etOldPassword.inputType = if (isOldPasswordVisible) {
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            binding.etOldPassword.setSelection(binding.etOldPassword.text.length)
            binding.passwordVisibilityToggleOld.setImageResource(
                if (isOldPasswordVisible) R.drawable.ic_visibility else R.drawable.ic_visibility_off
            )
        }

        // Toggle visibility for new password
        binding.passwordVisibilityToggleNew.setOnClickListener {
            isNewPasswordVisible = !isNewPasswordVisible
            binding.etNewPassword.inputType = if (isNewPasswordVisible) {
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            binding.etNewPassword.setSelection(binding.etNewPassword.text.length)
            binding.passwordVisibilityToggleNew.setImageResource(
                if (isNewPasswordVisible) R.drawable.ic_visibility else R.drawable.ic_visibility_off
            )
        }
    }
}
