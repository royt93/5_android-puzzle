package com.helpmepls.slidepuzzle.util

/**
 * Bang mau neon dang Int (ARGB) cho ve canvas (GameBoard - Wave 3) va glow runtime.
 *
 * Day la ban sao Kotlin cua cac token `neon_*` trong res/values/colors.xml.
 * Rang buoc 2 ben khop nhau duoc kiem o NeonGlowWidgetTest (androidTest) qua
 * ContextCompat.getColor, va gia tri ARGB duoc kiem o NeonPaletteTest (unit JVM).
 *
 * Thuan Kotlin (khong import android) => unit-test duoc tren JVM.
 */
object NeonPalette {
    // Nen toi (opaque)
    val BG_DEEP = 0xFF0E1230.toInt()
    val BG_SURFACE = 0xFF161B3D.toInt()
    val BG_ELEVATED = 0xFF232A63.toInt()

    // Accent (opaque)
    val CYAN = 0xFF38F9E4.toInt()
    val MAGENTA = 0xFFFF5DCB.toInt()
    val LIME = 0xFFC6FF4E.toInt()
    val VIOLET = 0xFF8A6CFF.toInt()

    // Glow (40% alpha = 0x66, cung hue voi accent)
    val CYAN_GLOW = 0x6638F9E4
    val MAGENTA_GLOW = 0x66FF5DCB
    val LIME_GLOW = 0x66C6FF4E

    // Text
    val TEXT_PRIMARY = 0xFFEAF2FF.toInt()
    val TEXT_SECONDARY = 0xFF9FB0D9.toInt()
}
