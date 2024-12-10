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

        // Mendapatkan email yang digunakan untuk mendaftar (dikirim melalui Intent)
        val email = intent.getStringExtra("EMAIL") ?: ""

        // Log untuk memeriksa apakah email diterima
        println("EMAIL intent received: $email")

        binding.btnSubmit.setOnClickListener {
            val oldPassword = binding.etOldPassword.text.toString().trim()
            val newPassword = binding.etNewPassword.text.toString().trim()

            // Log nilai input untuk debug
            println("OldPassword: $oldPassword, NewPassword: $newPassword, Email: $email")

            if (email.isNotEmpty() && oldPassword.isNotEmpty() && newPassword.isNotEmpty()) {
                // Lanjutkan proses pengubahan password
                changePassword(email, oldPassword, newPassword)
            } else {
                Toast.makeText(this, "Semua kolom harus diisi.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun changePassword(email: String, oldPassword: String, newPassword: String) {
        // Autentikasi pengguna menggunakan email dan password lama
        auth.signInWithEmailAndPassword(email, oldPassword).addOnCompleteListener { signInTask ->
            if (signInTask.isSuccessful) {
                // Jika login berhasil, perbarui password dengan password baru
                val user = auth.currentUser
                user?.updatePassword(newPassword)?.addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful) {
                        Toast.makeText(this, "Password berhasil diubah.", Toast.LENGTH_SHORT).show()
                        navigateToLogin() // Pindah ke halaman login
                    } else {
                        Toast.makeText(this, "Gagal mengubah password: ${updateTask.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Password lama salah: ${signInTask.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish() // Menutup aktivitas saat ini
    }
}
