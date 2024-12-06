package com.example.capstoneprojectmd.ui.password

import android.os.Bundle
import android.widget.Toast
import android.app.Activity
import com.example.capstoneprojectmd.databinding.ActivityChangePasswordBinding

class ChangePasswordActivity : Activity() {

    private lateinit var binding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSavePassword.setOnClickListener {
            val oldPassword = binding.etOldPassword.text.toString()
            val newPassword = binding.etNewPassword.text.toString()

            if (validatePassword(oldPassword, newPassword)) {
                updatePassword(newPassword)
            }
        }
    }

    private fun validatePassword(oldPassword: String, newPassword: String): Boolean {
        if (oldPassword.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        if (newPassword.length < 6) {
            Toast.makeText(this, "Password too short", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun updatePassword(newPassword: String) {
        // Dummy logic: replace with Firebase/Auth API
        Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()
        finish() // Close the activity after the update
    }
}
