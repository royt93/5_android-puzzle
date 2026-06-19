package com.helpmepls.slidepuzzle

import android.content.Context
import android.os.Build
import android.view.View
import androidx.core.content.ContextCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.helpmepls.slidepuzzle.util.NeonGlow
import com.helpmepls.slidepuzzle.util.NeonPalette
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

/** Widget test cho glow helper + rang buoc token XML khop NeonPalette + primitive drawables inflate. */
@RunWith(AndroidJUnit4::class)
class NeonGlowWidgetTest {

    private val ctx: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun applyGlowSetsOutlineShadowColorsOnApi28Plus() {
        val view = View(ctx)
        NeonGlow.apply(view, NeonPalette.CYAN)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            assertEquals(NeonPalette.CYAN, view.outlineSpotShadowColor)
            assertEquals(NeonPalette.CYAN, view.outlineAmbientShadowColor)
        }
        // API < 28: chi can goi khong crash (no-op).
    }

    @Test
    fun colorResourcesMatchKotlinPalette() {
        assertEquals(NeonPalette.CYAN, ContextCompat.getColor(ctx, R.color.neon_cyan))
        assertEquals(NeonPalette.MAGENTA, ContextCompat.getColor(ctx, R.color.neon_magenta))
        assertEquals(NeonPalette.LIME, ContextCompat.getColor(ctx, R.color.neon_lime))
        assertEquals(NeonPalette.VIOLET, ContextCompat.getColor(ctx, R.color.neon_violet))
        assertEquals(NeonPalette.BG_DEEP, ContextCompat.getColor(ctx, R.color.neon_bg_deep))
        assertEquals(NeonPalette.CYAN_GLOW, ContextCompat.getColor(ctx, R.color.neon_cyan_glow))
        assertEquals(NeonPalette.TEXT_PRIMARY, ContextCompat.getColor(ctx, R.color.neon_text_primary))
    }

    @Test
    fun neonPrimitiveDrawablesInflate() {
        val ids = intArrayOf(
            R.drawable.neon_bg_ambient,
            R.drawable.neon_glass_panel,
            R.drawable.neon_glass_card,
            R.drawable.neon_btn_primary,
            R.drawable.neon_btn_danger,
            R.drawable.neon_ring,
        )
        for (id in ids) {
            assertNotNull("drawable id=$id phai inflate duoc", ContextCompat.getDrawable(ctx, id))
        }
    }
}
