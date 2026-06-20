package com.helpmepls.slidepuzzle.act

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import com.helpmepls.slidepuzzle.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    override val useAccentTheme: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Removed UIUtils.setupEdgeToEdge1 - causes transparent status bar
        setContentView(R.layout.activity_splash)
        // Removed UIUtils.setupEdgeToEdge2 - using theme's status bar color instead

        // Logo: pulse glow dju (breathing) cho cam giac neon.
        val logo = findViewById<ImageView>(R.id.logo)
        val logoAnimation = AnimationUtils.loadAnimation(this, R.anim.neon_pulse)
        logo.startAnimation(logoAnimation)

        goToMain()
    }

    private fun goToMain() {
        lifecycleScope.launch {
            delay(1000)
            val intent = Intent(this@SplashActivity, BoardOptionsAct::class.java)
            startActivity(intent)
            applyOpenTransition()
            window.decorView.postDelayed({ finish() }, 300)
        }
    }

    @Suppress("DEPRECATION")
    private fun applyOpenTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(
                Activity.OVERRIDE_TRANSITION_OPEN,
                R.anim.smooth_slide_in_right,
                R.anim.elegant_fade_out
            )
        } else {
            overridePendingTransition(R.anim.smooth_slide_in_right, R.anim.elegant_fade_out)
        }
    }
}
