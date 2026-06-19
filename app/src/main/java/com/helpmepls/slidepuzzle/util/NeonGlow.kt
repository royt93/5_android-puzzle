package com.helpmepls.slidepuzzle.util

import android.os.Build
import android.view.View
import androidx.annotation.ColorInt

/**
 * Tien ich glow tai dung (Wave 1 F5).
 *
 * To mau bong cua elevation (outline spot/ambient shadow) bang mau neon de tao
 * cam giac "phat sang" quanh view co elevation. Chi co tu API 28; duoi 28 la no-op
 * (man hinh van hoat dong, glow lay tu drawable stroke thay the).
 */
object NeonGlow {

    /**
     * Set mau bong neon cho [view]. Yeu cau view co `elevation > 0` thi bong moi hien.
     * An toan goi o moi API level.
     */
    fun apply(view: View, @ColorInt color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            view.outlineSpotShadowColor = color
            view.outlineAmbientShadowColor = color
        }
        // API < 28: khong tint duoc bong he thong -> dua vao stroke glow cua drawable.
    }
}
