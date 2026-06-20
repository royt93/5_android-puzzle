package com.helpmepls.slidepuzzle.act

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.helpmepls.slidepuzzle.R
import com.helpmepls.slidepuzzle.util.Prefs

class SettingsActivity : BaseActivity() {

    private val swatches = listOf(
        Prefs.ACCENT_CYAN to R.id.swatchCyan,
        Prefs.ACCENT_MAGENTA to R.id.swatchMagenta,
        Prefs.ACCENT_LIME to R.id.swatchLime,
        Prefs.ACCENT_VIOLET to R.id.swatchViolet
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_settings)

        setSupportActionBar(findViewById(R.id.tbSettings))
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.settings)
        }

        androidx.core.view.WindowCompat.getInsetsController(window, window.decorView)
            ?.isAppearanceLightStatusBars = false

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { finish() }
        })

        bindToggles()
        bindSwatches()
    }

    private fun bindToggles() {
        val p = Prefs.get(this)

        bindSwitch(R.id.swShowNumbers, p.getBoolean(Prefs.SHOW_NUMBERS, true)) { v ->
            p.edit().putBoolean(Prefs.SHOW_NUMBERS, v).apply()
        }
        bindSwitch(R.id.swSound, p.getBoolean(Prefs.SOUND_ENABLED, true)) { v ->
            p.edit().putBoolean(Prefs.SOUND_ENABLED, v).apply()
        }
        bindSwitch(R.id.swHaptic, p.getBoolean(Prefs.HAPTIC_ENABLED, true)) { v ->
            p.edit().putBoolean(Prefs.HAPTIC_ENABLED, v).apply()
        }

        val isHigh = p.getString(Prefs.FX_QUALITY, Prefs.FX_QUALITY_HIGH) == Prefs.FX_QUALITY_HIGH
        bindSwitch(R.id.swFxQuality, isHigh) { v ->
            p.edit().putString(Prefs.FX_QUALITY, if (v) Prefs.FX_QUALITY_HIGH else Prefs.FX_QUALITY_LOW).apply()
        }

        val swBlur = findViewById<MaterialSwitch>(R.id.swFxBlur)
        val rowBlur = findViewById<View>(R.id.rowFxBlur)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            rowBlur?.alpha = 0.4f
            swBlur?.isEnabled = false
            swBlur?.isChecked = false
        } else {
            swBlur?.isChecked = p.getBoolean(Prefs.FX_BLUR, true)
            swBlur?.setOnCheckedChangeListener { _, v ->
                p.edit().putBoolean(Prefs.FX_BLUR, v).apply()
            }
        }

        bindSwitch(R.id.swReduceMotion, p.getBoolean(Prefs.FX_REDUCE_MOTION, false)) { v ->
            p.edit().putBoolean(Prefs.FX_REDUCE_MOTION, v).apply()
        }
    }

    private fun bindSwatches() {
        val currentAccent = Prefs.get(this).getString(Prefs.ACCENT_THEME, Prefs.DEFAULT_ACCENT) ?: Prefs.DEFAULT_ACCENT
        updateSwatchSelection(currentAccent)

        swatches.forEach { (accent, id) ->
            findViewById<MaterialButton>(id)?.setOnClickListener {
                Prefs.get(this).edit().putString(Prefs.ACCENT_THEME, accent).apply()
                recreate()
            }
        }
    }

    private fun updateSwatchSelection(selectedAccent: String) {
        swatches.forEach { (accent, id) ->
            val btn = findViewById<MaterialButton>(id) ?: return@forEach
            val isSelected = accent == selectedAccent
            btn.strokeColor = ColorStateList.valueOf(if (isSelected) Color.WHITE else Color.TRANSPARENT)
            btn.strokeWidth = if (isSelected) 3 else 0
            btn.elevation = if (isSelected) 16f else 2f
            btn.animate()
                .scaleX(if (isSelected) 1.18f else 1.0f)
                .scaleY(if (isSelected) 1.18f else 1.0f)
                .setDuration(150)
                .start()
        }
    }

    private fun bindSwitch(id: Int, checked: Boolean, onChange: (Boolean) -> Unit) {
        val sw = findViewById<MaterialSwitch>(id) ?: return
        sw.isChecked = checked
        sw.setOnCheckedChangeListener { _, v -> onChange(v) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
