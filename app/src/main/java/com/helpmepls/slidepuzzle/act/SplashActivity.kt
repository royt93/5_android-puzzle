package com.helpmepls.slidepuzzle.act

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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
        UIUtils.setupEdgeToEdge1(window)
        setContentView(R.layout.activity_splash)
        UIUtils.setupEdgeToEdge2(findViewById(R.id.layoutRoot))
        goToMain()
    }

    private fun goToMain() {
        lifecycleScope.launch {
            delay(1000)
            val intent = Intent(this@SplashActivity, BoardOptionsAct::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            window.decorView.postDelayed({ finish() }, 300)
        }
    }
}
