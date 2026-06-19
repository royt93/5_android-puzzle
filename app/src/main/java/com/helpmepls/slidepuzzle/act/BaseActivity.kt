package com.helpmepls.slidepuzzle.act

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase.withFixedFontScale())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyHighestRefreshRate()
    }

    private fun Context.withFixedFontScale(): Context {
        val configuration = Configuration(resources.configuration)
        configuration.fontScale = 1.0f
        return createConfigurationContext(configuration)
    }

    /**
     * Yeu cau he thong dung che do hien thi co refresh rate cao nhat (90/120Hz...) cung
     * do phan giai hien tai => slide muot hon. Fallback no-op tren thiet bi chi co 60Hz
     * hoac API < 23. `preferredDisplayModeId` co tu API 23 (minSdk 23).
     */
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
