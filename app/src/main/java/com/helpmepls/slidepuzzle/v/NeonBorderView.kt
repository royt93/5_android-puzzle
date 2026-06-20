package com.helpmepls.slidepuzzle.v

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.helpmepls.slidepuzzle.util.NeonPalette
import com.helpmepls.slidepuzzle.util.Prefs

/**
 * Viền gradient chạy vòng quanh board (Wave 10 G3).
 * SweepGradient xoay liên tục qua ValueAnimator trên hardware layer — không alloc trong onDraw.
 * clickable=false + importantForAccessibility=no → không chặn touch/a11y của GameBoard bên dưới.
 */
class NeonBorderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Wave 12: màu chủ đạo theo accent; rebuild trong onSizeChanged (gọi lại sau recreate()).
    private fun buildBorderColors(): IntArray {
        val accent = Prefs.resolveAccentColor(context)
        return intArrayOf(accent, NeonPalette.MAGENTA, NeonPalette.LIME, NeonPalette.VIOLET, accent)
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }
    private val matrix = Matrix()
    private var sweepShader: SweepGradient? = null
    private var angle = 0f
    private val cornerRadius = 20f

    private val animator = ValueAnimator.ofFloat(0f, 360f).apply {
        duration = 2400L
        repeatCount = ValueAnimator.INFINITE
        repeatMode = ValueAnimator.RESTART
        interpolator = LinearInterpolator()
        addUpdateListener {
            angle = it.animatedValue as Float
            invalidate()
        }
    }

    var showBorder: Boolean = true
        set(value) {
            if (field == value) return
            field = value
            if (value) {
                setLayerType(LAYER_TYPE_HARDWARE, null)
                if (!animator.isRunning) animator.start()
            } else {
                animator.pause()
                setLayerType(LAYER_TYPE_NONE, null)
                invalidate()
            }
        }

    init {
        isClickable = false
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        sweepShader = SweepGradient(w / 2f, h / 2f, buildBorderColors(), null)
    }

    override fun onDraw(canvas: Canvas) {
        if (!showBorder) return
        val shader = sweepShader ?: return
        matrix.reset()
        matrix.postRotate(angle, width / 2f, height / 2f)
        shader.setLocalMatrix(matrix)
        paint.shader = shader
        val inset = paint.strokeWidth / 2f
        canvas.drawRoundRect(inset, inset, width - inset, height - inset, cornerRadius, cornerRadius, paint)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (showBorder) animator.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator.cancel()
    }
}
