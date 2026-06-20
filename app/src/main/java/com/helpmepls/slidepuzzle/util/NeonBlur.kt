package com.helpmepls.slidepuzzle.util

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.view.View

/**
 * Blur nền dialog dùng RenderEffect (API 31+). Dưới API 31 là no-op hoàn toàn an toàn —
 * caller không cần guard; scrim drawable đảm bảo fallback visual.
 */
object NeonBlur {

    fun applyBlur(view: View, radiusPx: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            view.setRenderEffect(
                RenderEffect.createBlurEffect(radiusPx, radiusPx, Shader.TileMode.CLAMP)
            )
        }
    }

    fun clearBlur(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            view.setRenderEffect(null)
        }
    }
}
