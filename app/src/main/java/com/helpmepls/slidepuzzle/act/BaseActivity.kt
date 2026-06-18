package com.helpmepls.slidepuzzle.act

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase.withFixedFontScale())
    }

    private fun Context.withFixedFontScale(): Context {
        val configuration = Configuration(resources.configuration)
        configuration.fontScale = 1.0f
        return createConfigurationContext(configuration)
    }
}
