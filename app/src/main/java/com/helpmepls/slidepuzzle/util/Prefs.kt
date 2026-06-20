package com.helpmepls.slidepuzzle.util

import android.content.Context
import android.content.SharedPreferences

object Prefs {
    const val NAME = "puzzle_prefs"

    const val SHOW_NUMBERS = "show_numbers"
    const val SOUND_ENABLED = "sound_enabled"
    const val HAPTIC_ENABLED = "haptic_enabled"
    const val FX_QUALITY = "fx_quality"
    const val FX_BLUR = "fx_blur"
    const val FX_REDUCE_MOTION = "fx_reduce_motion"
    const val ACCENT_THEME = "accent_theme"

    const val FX_QUALITY_HIGH = "high"
    const val FX_QUALITY_LOW = "low"

    const val ACCENT_CYAN = "cyan"
    const val ACCENT_MAGENTA = "magenta"
    const val ACCENT_LIME = "lime"
    const val ACCENT_VIOLET = "violet"
    const val DEFAULT_ACCENT = ACCENT_CYAN

    fun get(context: Context): SharedPreferences =
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    fun resolveAccentColor(context: Context): Int = when (
        get(context).getString(ACCENT_THEME, DEFAULT_ACCENT)
    ) {
        ACCENT_MAGENTA -> NeonPalette.MAGENTA
        ACCENT_LIME -> NeonPalette.LIME
        ACCENT_VIOLET -> NeonPalette.VIOLET
        else -> NeonPalette.CYAN
    }

    fun resolveAccentGlowColor(context: Context): Int = when (
        get(context).getString(ACCENT_THEME, DEFAULT_ACCENT)
    ) {
        ACCENT_MAGENTA -> NeonPalette.MAGENTA_GLOW
        ACCENT_LIME -> NeonPalette.LIME_GLOW
        ACCENT_VIOLET -> 0x668A6CFF
        else -> NeonPalette.CYAN_GLOW
    }
}
