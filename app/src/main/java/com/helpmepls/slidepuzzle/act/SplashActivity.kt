package com.helpmepls.slidepuzzle.act

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.helpmepls.sdkadbmob.UIUtils
import com.helpmepls.slidepuzzle.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Removed UIUtils.setupEdgeToEdge1 - causes transparent status bar
        setContentView(R.layout.activity_splash)
        // Removed UIUtils.setupEdgeToEdge2 - using theme's status bar color instead

        // Add logo animation
        val logo = findViewById<ImageView>(R.id.logo)
        val logoAnimation = AnimationUtils.loadAnimation(this, R.anim.elegant_splash_logo)
        logo.startAnimation(logoAnimation)

        goToMain()
    }

    private fun goToMain() {
        lifecycleScope.launch {
            delay(1000)
            val intent = Intent(this@SplashActivity, BoardOptionsAct::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.smooth_slide_in_right, R.anim.elegant_fade_out)
            window.decorView.postDelayed({ finish() }, 300)
        }
    }
}
