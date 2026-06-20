package com.helpmepls.slidepuzzle.act

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.helpmepls.slidepuzzle.R
import com.helpmepls.slidepuzzle.util.Prefs

abstract class BaseActivity : AppCompatActivity() {

    /** Override false ở SplashActivity để không ghi đè SplashTheme. */
    open val useAccentTheme: Boolean = true

    private var appliedAccent: String = Prefs.DEFAULT_ACCENT

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase.withFixedFontScale())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (useAccentTheme) {
            appliedAccent = Prefs.get(this).getString(Prefs.ACCENT_THEME, Prefs.DEFAULT_ACCENT) ?: Prefs.DEFAULT_ACCENT
            when (appliedAccent) {
                Prefs.ACCENT_MAGENTA -> setTheme(R.style.AppTheme_Magenta)
                Prefs.ACCENT_LIME -> setTheme(R.style.AppTheme_Lime)
                Prefs.ACCENT_VIOLET -> setTheme(R.style.AppTheme_Violet)
                else -> { /* AppTheme (cyan) đã là default trong manifest */ }
            }
        }
        super.onCreate(savedInstanceState)
        applyHighestRefreshRate()
    }

    override fun onResume() {
        super.onResume()
        if (useAccentTheme) {
            val current = Prefs.get(this).getString(Prefs.ACCENT_THEME, Prefs.DEFAULT_ACCENT) ?: Prefs.DEFAULT_ACCENT
            if (current != appliedAccent) recreate()
        }
    }

    private fun Context.withFixedFontScale(): Context {
        val configuration = Configuration(resources.configuration)
        configuration.fontScale = 1.0f
        return createConfigurationContext(configuration)
    }

    private fun applyHighestRefreshRate() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
        @Suppress("DEPRECATION")
        val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) display else windowManager.defaultDisplay
        display ?: return

        val current = display.mode ?: return
        val best = display.supportedModes
            .filter {
                it.physicalWidth == current.physicalWidth &&
                    it.physicalHeight == current.physicalHeight
            }
            .maxByOrNull { it.refreshRate } ?: return

        if (best.modeId != current.modeId && best.refreshRate > current.refreshRate) {
            window.attributes = window.attributes.apply { preferredDisplayModeId = best.modeId }
        }
    }
}
