package com.helpmepls.slidepuzzle.v

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.helpmepls.slidepuzzle.util.NeonPalette
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

/**
 * Confetti neon burst khi win (Wave 11).
 * Pool cố định 80 hạt — không alloc trong onDraw.
 * Vật lý đơn giản: velocity + gravity, alpha fade theo progress.
 * clickable=false + GONE mặc định; gọi burst() để kích hoạt.
 */
class NeonParticleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private data class Particle(
        var ox: Float = 0f, var oy: Float = 0f,
        var vx: Float = 0f, var vy: Float = 0f,
        var size: Float = 0f,
        var color: Int = 0
    )

    private val PARTICLE_COUNT = 80
    private val GRAVITY = 480f          // px/s²
    private val DURATION_MS = 1300L

    private val particles = Array(PARTICLE_COUNT) { Particle() }
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val COLORS = intArrayOf(
        NeonPalette.CYAN, NeonPalette.MAGENTA, NeonPalette.LIME,
        NeonPalette.VIOLET, NeonPalette.CYAN, NeonPalette.LIME
    )

    private var animProgress = 0f

    private val animator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = DURATION_MS
        interpolator = LinearInterpolator()
        addUpdateListener { animProgress = it.animatedValue as Float; invalidate() }
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(a: Animator) { visibility = GONE }
        })
    }

    init {
        isClickable = false
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
    }

    fun burst(cx: Float, cy: Float) {
        particles.forEachIndexed { i, p ->
            val angle = Random.nextFloat() * 2f * PI.toFloat()
            val speed = 200f + Random.nextFloat() * 450f
            p.ox = cx
            p.oy = cy
            p.vx = cos(angle) * speed
            p.vy = sin(angle) * speed - 150f   // upward bias
            p.size = 3f + Random.nextFloat() * 6f
            p.color = COLORS[i % COLORS.size]
        }
        animProgress = 0f
        visibility = VISIBLE
        animator.cancel()
        animator.start()
    }

    override fun onDraw(canvas: Canvas) {
        val t = animProgress * (DURATION_MS / 1000f)
        for (p in particles) {
            val px = p.ox + p.vx * t
            val py = p.oy + p.vy * t + 0.5f * GRAVITY * t * t
            val fade = (1f - animProgress.pow(1.2f)).coerceIn(0f, 1f)
            paint.color = (p.color and 0x00FFFFFF) or ((fade * 255).toInt() shl 24)
            canvas.drawCircle(px, py, p.size, paint)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator.cancel()
    }
}
