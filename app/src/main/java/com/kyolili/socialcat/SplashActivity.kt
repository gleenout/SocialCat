package com.lilikyo.socialcat

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.kyolili.socialcat.R
import com.kyolili.socialcat.databinding.ActivitySplashBinding
import com.kyolili.socialcat.ui.auth.AuthActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Esconde a barra de status
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        startAnimations()
        navigateToMainDelayed()
    }

    private fun startAnimations() {
        // Animação do logo
        binding.splashLogo.alpha = 0f
        binding.splashLogo.animate()
            .alpha(1f)
            .setDuration(1000)
            .setInterpolator(DecelerateInterpolator())
            .start()

        // Animação do subtítulo
        binding.splashSubtitle.alpha = 0f
        binding.splashSubtitle.translationY = 50f
        binding.splashSubtitle.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(1000)
            .setStartDelay(700)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    private fun navigateToMainDelayed() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, AuthActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 2500)
    }
}
