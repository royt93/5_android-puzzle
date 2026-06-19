package com.helpmepls.slidepuzzle.util

import org.junit.Assert.assertEquals
import org.junit.Test

/** Unit test (JVM) cho bang mau neon - khong phu thuoc android. */
class NeonPaletteTest {

    private fun alpha(c: Int) = (c ushr 24) and 0xFF
    private fun rgb(c: Int) = c and 0xFFFFFF

    @Test
    fun accentColorsAreFullyOpaqueWithExpectedRgb() {
        assertEquals(0xFF, alpha(NeonPalette.CYAN))
        assertEquals(0x38F9E4, rgb(NeonPalette.CYAN))
        assertEquals(0xFF, alpha(NeonPalette.MAGENTA))
        assertEquals(0xFF5DCB, rgb(NeonPalette.MAGENTA))
        assertEquals(0xFF, alpha(NeonPalette.LIME))
        assertEquals(0xC6FF4E, rgb(NeonPalette.LIME))
    }

    @Test
    fun glowVariantsAre40PercentAlphaOfSameHue() {
        assertEquals(0x66, alpha(NeonPalette.CYAN_GLOW))
        assertEquals(rgb(NeonPalette.CYAN), rgb(NeonPalette.CYAN_GLOW))
        assertEquals(0x66, alpha(NeonPalette.MAGENTA_GLOW))
        assertEquals(rgb(NeonPalette.MAGENTA), rgb(NeonPalette.MAGENTA_GLOW))
        assertEquals(0x66, alpha(NeonPalette.LIME_GLOW))
        assertEquals(rgb(NeonPalette.LIME), rgb(NeonPalette.LIME_GLOW))
    }

    @Test
    fun darkBaseIsOpaqueDeepNavy() {
        assertEquals(0xFF, alpha(NeonPalette.BG_DEEP))
        assertEquals(0x0E1230, rgb(NeonPalette.BG_DEEP))
    }
}
