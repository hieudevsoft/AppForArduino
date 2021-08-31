package com.devapp.appforarduino.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.devapp.appforarduino.R
import com.devapp.appforarduino.databinding.ActivityStartedScreenBinding

class StartedScreen : AppCompatActivity() {
    private lateinit var binding: ActivityStartedScreenBinding
    private lateinit var handler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartedScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        handler = Handler(mainLooper)


    }

    override fun onStart() {
        setupInit()
        super.onStart()
    }

    override fun onResume() {
        setupAnimation()
        handler.postDelayed({
            Intent(this, HomeActivity::class.java).also { startActivity(it) }
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right)
            finish()
        }, 3200)
        super.onResume()
    }

    private fun setupInit() {
        binding.tvNameTeam.translationY = -500f
        binding.tvSlogan.translationX = -1000f
        binding.lottieAnimation.alpha = 0f
        binding.tvCopyRight.alpha = 0f
        binding.tvDescription.translationX = 1000f
    }

    fun setupAnimation() {
        binding.tvNameTeam.animate().apply {
            duration = 1500
            translationY(0f)
            startDelay = 500L
            interpolator = AccelerateDecelerateInterpolator()
        }.start()

        binding.tvSlogan.animate().apply {
            duration = 1500
            translationX(0f)
            startDelay = 500L
            interpolator = AccelerateDecelerateInterpolator()
        }.start()

        binding.tvDescription.animate().apply {
            duration = 1500
            translationX(0f)
            startDelay = 500L
            interpolator = AccelerateDecelerateInterpolator()
        }.start()

        binding.tvCopyRight.animate().apply {
            duration = 1500
            alpha(1f)
            startDelay = 500L
        }.start()


        binding.lottieAnimation.animate().apply {
            duration = 200
            startDelay = 100
            alpha(1f)
        }.start()
    }
}