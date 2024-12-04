package com.example.capstoneprojectmd.ui.welcome

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.capstoneprojectmd.databinding.ActivityWelcomeBinding
import com.example.capstoneprojectmd.ui.signin.SignInActivity
import com.example.capstoneprojectmd.ui.signup.SignupActivity


class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        setupAnimation()
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

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        binding.signupButton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun setupAnimation() {
        val logoFadeIn = ObjectAnimator.ofFloat(binding.imageView, "alpha", 0f, 1f).setDuration(500)
        val logoSlideDown =
            ObjectAnimator.ofFloat(binding.imageView, "translationY", -200f, 0f).setDuration(1000)

        val titleFadeIn =
            ObjectAnimator.ofFloat(binding.titleTextView, "alpha", 0f, 1f).setDuration(500)
        val titleSlideUp =
            ObjectAnimator.ofFloat(binding.titleTextView, "translationY", 200f, 0f).setDuration(500)

        val descFadeIn =
            ObjectAnimator.ofFloat(binding.descTextView, "alpha", 0f, 1f).setDuration(500)
        val descSlideUp =
            ObjectAnimator.ofFloat(binding.descTextView, "translationY", 200f, 0f).setDuration(500)

        val loginButtonFadeIn =
            ObjectAnimator.ofFloat(binding.loginButton, "alpha", 0f, 1f).setDuration(500)
        val loginButtonSlideUp =
            ObjectAnimator.ofFloat(binding.loginButton, "translationY", 200f, 0f).setDuration(500)

        val signupButtonFadeIn =
            ObjectAnimator.ofFloat(binding.signupButton, "alpha", 0f, 1f).setDuration(500)
        val signupButtonSlideUp =
            ObjectAnimator.ofFloat(binding.signupButton, "translationY", 200f, 0f).setDuration(500)

        AnimatorSet().apply {
            play(logoFadeIn).with(logoSlideDown)
            play(titleFadeIn).with(titleSlideUp).after(logoFadeIn)
            play(descFadeIn).with(descSlideUp).after(titleFadeIn)
            play(loginButtonFadeIn).with(loginButtonSlideUp).with(signupButtonFadeIn).with(signupButtonSlideUp).after(descFadeIn)
            start()

        }
    }
}