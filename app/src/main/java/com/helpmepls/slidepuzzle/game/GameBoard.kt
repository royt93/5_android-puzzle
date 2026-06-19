package com.helpmepls.slidepuzzle.game

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Size
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import com.helpmepls.slidepuzzle.game.state.PuzzleGrid
import com.helpmepls.slidepuzzle.game.state.Direction
import com.helpmepls.slidepuzzle.util.NeonPalette
import kotlin.math.ceil

@SuppressLint("ClickableViewAccessibility")
class GameBoard(
    context: Context,
    attrs: AttributeSet,
) : View(context, attrs) {
    // Neon: tile-active va halo board phat sang cyan (Wave 3).
    private val highlightColor = NeonPalette.CYAN
    private val paint = Paint()
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val tmpRectF = RectF()
    private val boardRectF = RectF()
    // Glow gia bang nhieu lop stroke (ngoai mo -> trong sang); chay tren hardware layer, khong can software.
    private val glowLayerScale = floatArrayOf(3.0f, 2.0f, 1.0f)
    private val glowLayerAlpha = intArrayOf(0x22, 0x55, 0xCC)
    private var animator: ValueAnimator? = null
    private val tileSpacing = 3
    private var tileSize = Rect(0, 0, 0, 0)
    private var renderOffset = Rect(0, 0, 0, 0)
    private val srcRect = Rect(0, 0, 0, 0)
    private var animOffset = PointF(0.0f, 0.0f)

    private lateinit var activeSlide: Point
    private val moveStack = java.util.Stack<Point>()
    var onMoveListener: ((Int, Boolean) -> Unit)? = null // count, isSolved
    private var moveCount = 0

    // Tuy chon hien thi so thu tu tren tung manh ghep.
    var showNumbers: Boolean = true
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }


    private var grid = PuzzleGrid(
        sourceImage = null,
        size = Size(/* width = */ 4, /* height = */ 4)
    )

    init {
        paint.isAntiAlias = true
        paint.typeface = Typeface.DEFAULT_BOLD

        setOnTouchListener { view, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_UP -> {
                    view.performClick()
                    onSlide(
                        getSlideCoordinates(
                            p = PointF(
                                /* x = */ event.x,
                                /* y = */ event.y
                            )
                        )
                    )
                }
            }
            true
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    private fun getSlideCoordinates(p: PointF): Point {
        return Point(
            /* x = */ (p.x / width * grid.size.width).toInt(),
            /* y = */ (p.y / height * grid.size.height).toInt()
        )
    }

    fun undo(): Boolean {
        if (moveStack.isEmpty() || animator != null) return false
        val originalTilePos = moveStack.pop()
        
        // Stack stores the ORIGINAL position of the tile that was moved.
        // To undo, move the tile from its current position back to originalTilePos.
        // However, moveSlide() moves a tile INTO the empty space.
        // So to reverse, we need to move the tile that is now where empty WAS.
        // In short: the empty is now at originalTilePos (where tile came from),
        // and the tile is at some adjacent position. We call moveSlide on the tile's NEW position
        // to move it back into the empty (which is at originalTilePos).
        // But actually since we stored original pos and empty moved there,
        // calling moveSlide(originalTilePos) won't work because that's where empty is now.
        // We need to find where the tile ended up (adjacent to originalTilePos) and call moveSlide on it.
        
        // Simpler fix: Store the position where tile ENDED UP (newCoordinates), then to undo,
        // call moveSlide on that position again to swap it back.
        // Actually current code already does this but comment was confusing. Let's verify:
        // Push: moveStack.push(newCoordinates) - stores where tile moved TO
        // Pop: lastMovePos = newCoordinates = where tile is NOW
        // moveSlide(lastMovePos) tries to move tile at lastMovePos into empty
        // This works IF empty is adjacent to lastMovePos - which it should be!
        
        grid.moveSlide(originalTilePos)?.let {
             moveCount--
             invalidate()
             val solved = grid.isSolved()
             onMoveListener?.invoke(moveCount, solved) 
             return true
        }
        return false
    }


    fun resize(
        size: Size,
        image: Bitmap? = null,
        shuffle: Boolean = true,
    ) {
        grid.regenerate(newSize = size, newImage = image, shuffle = shuffle)
        moveStack.clear()
        moveCount = 0
        onMoveListener?.invoke(0, false)
        requestLayout()
    }

    fun shuffle(reset: Boolean = false) {
        grid.shuffle(reset)
        moveStack.clear()
        moveCount = 0
        onMoveListener?.invoke(0, false)
        invalidate()
    }

    fun shuffleWithAnimation() {
        // Enhanced shuffle với animation effects
        val shuffleAnimator = ValueAnimator.ofFloat(0.0f, 1.0f).apply {
            duration = 500
            interpolator = AccelerateDecelerateInterpolator()

            addUpdateListener {
                // Tạo shake effect cho toàn bộ board
                val progress = it.animatedValue as Float
                val shakeAmount = (1.0f - progress) * 10.0f
                translationX = (Math.random() * shakeAmount - shakeAmount / 2).toFloat()
                translationY = (Math.random() * shakeAmount - shakeAmount / 2).toFloat()
            }

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    translationX = 0f
                    translationY = 0f
                    grid.shuffle(false)
                    
                    // Critical Fix: State reset after shuffle
                    moveStack.clear()
                    moveCount = 0
                    onMoveListener?.invoke(0, false)
                    
                    invalidate()
                }
            })

            start()
        }
    }

    private fun onSlide(p: Point) {
        if (p.x !in 0 until grid.size.width || p.y !in 0 until grid.size.height)
            return

        val direction = grid.checkSlideMoveDirection(p)
        if (animator != null || direction == null)
            return

        // reset offset
        animOffset.set(0.0f, 0.0f)
        activeSlide = p

        // start enhanced animation
        animator = ValueAnimator.ofFloat(0.0f, 1.0f).apply {
            duration = 300 // Tăng duration để mượt hơn
            interpolator = OvershootInterpolator(0.5f) // Spring effect

            addUpdateListener {
                val progress = it.animatedValue as Float
                animOffset.set(
                    direction.offsetX * tileSize.width() * progress,
                    direction.offsetY * tileSize.height() * progress
                )
                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    animator = null
                    grid.moveSlide(p)?.let { newCoordinates ->
                        activeSlide = newCoordinates
                        animOffset.set(0.0f, 0.0f)
                        
                        // Move success
                        moveStack.push(newCoordinates)
                        moveCount++
                        performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
                        
                        val solved = grid.isSolved()
                        onMoveListener?.invoke(moveCount, solved)
                        
                        invalidate()
                    }
                }
            })

            start()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        
        if (!grid.isConfigured()) return

        grid.let {
            tileSize.set(
                /* left = */ 0,
                /* top = */ 0,
                /* right = */ ceil(MeasureSpec.getSize(widthMeasureSpec).toDouble() / it.size.width).toInt(),
                /* bottom = */ ceil(MeasureSpec.getSize(heightMeasureSpec).toDouble() / it.size.height)
                    .toInt()
            )
        }
    }

    private fun drawSlideTitle(
        canvas: Canvas,
        offset: Rect,
        text: String,
    ) {
        // So thu tu: text trang lanh + glow cyan (shadowLayer ho tro tot tu API 28; duoi do van hien ro chu).
        paint.style = Paint.Style.FILL
        paint.textSize = (offset.height() * 0.2f).coerceIn(16.0f, 56.0f)
        paint.color = NeonPalette.TEXT_PRIMARY
        paint.setShadowLayer(8.0f, 0.0f, 0.0f, NeonPalette.CYAN_GLOW)
        canvas.drawText(
            /* text = */ text,
            /* x = */ offset.left + 8.0f,
            /* y = */ offset.top + paint.textSize + 4.0f,
            /* paint = */ paint
        )
        paint.clearShadowLayer()
    }

    // Ve vien glow (nhieu lop stroke) cho 1 round-rect. Tai dung cho tile-active va halo board.
    private fun drawGlowRoundRect(
        canvas: Canvas,
        rect: RectF,
        radius: Float,
        baseColor: Int,
        coreWidth: Float,
    ) {
        glowPaint.style = Paint.Style.STROKE
        for (k in glowLayerScale.indices) {
            glowPaint.strokeWidth = coreWidth * glowLayerScale[k]
            glowPaint.color = (baseColor and 0x00FFFFFF) or (glowLayerAlpha[k] shl 24)
            canvas.drawRoundRect(rect, radius, radius, glowPaint)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        if (!grid.isConfigured()) return
        val image = grid.image ?: return

        // Kich thuoc 1 manh tren anh nguon (pixel goc), dung de cat src Rect.
        val srcTileW = image.width / grid.size.width
        val srcTileH = image.height / grid.size.height

        // Halo cyan quanh khung board (ve 1 lan/frame, ngoai vong lap tile).
        boardRectF.set(3.0f, 3.0f, width - 3.0f, height - 3.0f)
        drawGlowRoundRect(canvas, boardRectF, 16.0f, highlightColor, 2.0f)

        for (j in 0 until grid.size.height) {
            for (i in 0 until grid.size.width) {
                val puzzle = grid.puzzles[j][i]
                puzzle?.let {
                    val active =
                        ::activeSlide.isInitialized && activeSlide.x == i && activeSlide.y == j

                    val x = i * tileSize.width()
                    val y = j * tileSize.height()

                    renderOffset.set(
                        /* left = */ x + tileSpacing / 2,
                        /* top = */ y + tileSpacing / 2,
                        /* right = */ x + tileSize.width() - tileSpacing,
                        /* bottom = */ y + tileSize.height() - tileSpacing
                    )

                    if (active) {
                        renderOffset.left += animOffset.x.toInt()
                        renderOffset.top += animOffset.y.toInt()
                        renderOffset.right += animOffset.x.toInt()
                        renderOffset.bottom += animOffset.y.toInt()
                    }

                    // src Rect cua manh nay tren anh nguon, theo vi tri dung (index).
                    val sx = (puzzle.index % grid.size.width) * srcTileW
                    val sy = (puzzle.index / grid.size.width) * srcTileH
                    srcRect.set(sx, sy, sx + srcTileW, sy + srcTileH)

                    canvas.drawBitmap(
                        /* bitmap = */ image,
                        /* src = */ srcRect,
                        /* dst = */ renderOffset,
                        /* paint = */ null
                    )

                    // fill
                    if (showNumbers) {
                        drawSlideTitle(
                            canvas = canvas,
                            offset = renderOffset,
                            text = (puzzle.index + 1).toString()
                        )
                    }

                    // Vien glow cyan quanh tile dang truot (chi 1 tile/frame).
                    if (active) {
                        tmpRectF.set(renderOffset)
                        drawGlowRoundRect(canvas, tmpRectF, 8.0f, highlightColor, 4.0f)
                    }
                }
            }
        }
    }
}
