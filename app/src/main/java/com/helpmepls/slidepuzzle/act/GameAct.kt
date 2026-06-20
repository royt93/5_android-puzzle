package com.helpmepls.slidepuzzle.act

import android.os.Bundle
import android.graphics.BitmapFactory
import android.util.Size
import android.view.MotionEvent
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.activity.OnBackPressedCallback
import java.util.Locale

import com.helpmepls.slidepuzzle.R
import com.helpmepls.slidepuzzle.DialogUtils
import com.helpmepls.slidepuzzle.game.GameBoard
import com.helpmepls.slidepuzzle.util.ShareUtils
import com.helpmepls.slidepuzzle.model.BoardTitledSize
import com.helpmepls.slidepuzzle.util.NeonBlur
import com.helpmepls.slidepuzzle.util.NeonGlow
import com.helpmepls.slidepuzzle.util.Prefs
import com.helpmepls.slidepuzzle.v.NeonBorderView
import com.helpmepls.slidepuzzle.v.NeonParticleView
import com.helpmepls.slidepuzzle.vm.BoardOptionsVm
import android.animation.ValueAnimator
import android.view.animation.DecelerateInterpolator
import android.view.LayoutInflater
import android.graphics.Color
import android.content.res.ColorStateList
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

class GameAct : BaseActivity() {
    companion object Companion {
        const val EXTRA_IMAGE_RES_ID = "com.helpmepls.slidepuzzle.EXTRA_IMAGE_RES_ID"
        const val EXTRA_BOARD_WIDTH = "com.helpmepls.slidepuzzle.EXTRA_BOARD_WIDTH"
        const val EXTRA_BOARD_HEIGHT = "com.helpmepls.slidepuzzle.EXTRA_BOARD_HEIGHT"
        const val EXTRA_CUSTOM_IMAGE_PATH = "com.helpmepls.slidepuzzle.EXTRA_CUSTOM_IMAGE_PATH"
        // Task 26: -1 = Classic (no budget), >0 = Move Challenge budget.
        const val EXTRA_MOVE_BUDGET = "com.helpmepls.slidepuzzle.EXTRA_MOVE_BUDGET"
        // Task 23: -1 = Classic (count-up), >0 = Time Attack countdown limit in seconds.
        const val EXTRA_TIME_LIMIT_SECONDS = "com.helpmepls.slidepuzzle.EXTRA_TIME_LIMIT_SECONDS"

        // Cache bitmap render tu VectorDrawable (anh neon) theo resId. Vector immutable nen
        // cache an toan + KHONG recycle (chi recycle anh raster lon). Toi da ~6 anh x 512^2.
        private const val VECTOR_RENDER_SIZE = 512
        private val vectorCache = HashMap<Int, android.graphics.Bitmap>()
    }

    private fun View.addSpringClickAnimation() {
        this.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    val pressAnimation = AnimationUtils.loadAnimation(context, R.anim.spring_button_press)
                    view.startAnimation(pressAnimation)
                }
                MotionEvent.ACTION_UP -> {
                    val releaseAnimation = AnimationUtils.loadAnimation(context, R.anim.spring_button_release)
                    view.startAnimation(releaseAnimation)
                    if (motionEvent.x in 0f..view.width.toFloat() && motionEvent.y in 0f..view.height.toFloat()) {
                        view.performClick()
                    }
                }
                MotionEvent.ACTION_CANCEL -> {
                    val releaseAnimation = AnimationUtils.loadAnimation(context, R.anim.spring_button_release)
                    view.startAnimation(releaseAnimation)
                }
            }
            true
        }
    }

    // imageResId hiện tại — dùng làm phần key per-puzzle score. 0 khi ảnh từ gallery.
    private var currentImageResId: Int = 0

    private var timerSeconds = 0
    private var timerHandler = android.os.Handler(android.os.Looper.getMainLooper())
    private var timerRunnable: Runnable? = null
    private var isGameRunning = false
    private var undoCount = 3
    private var showNumbers = true
    // Task 25: penalty moves khi dùng hint; reset khi shuffle/reset/new game.
    private var hintPenaltyMoves = 0
    private var hintCooldownActive = false
    // Task 27: lưu moves lúc thắng để dùng khi share.
    private var lastWinMoves = 0
    // Task 26: -1 = Classic, >0 = Move Challenge budget; reset cùng shuffle/reset.
    private var moveBudget = -1
    private var budgetExceededShown = false
    // Task 23: -1 = Classic (count-up), >0 = Time Attack countdown limit seconds.
    private var timeAttackLimit = -1
    private var timeUpShown = false
    private var soundEnabled = true
    private val soundManager: com.helpmepls.slidepuzzle.util.SoundManager by lazy {
        com.helpmepls.slidepuzzle.util.SoundManager(this)
    }

    private fun loadShowNumbers() = Prefs.get(this).getBoolean(Prefs.SHOW_NUMBERS, true)
    private fun saveShowNumbers(v: Boolean) = Prefs.get(this).edit().putBoolean(Prefs.SHOW_NUMBERS, v).apply()
    private fun loadSoundEnabled() = Prefs.get(this).getBoolean(Prefs.SOUND_ENABLED, true)
    private fun saveSoundEnabled(v: Boolean) = Prefs.get(this).edit().putBoolean(Prefs.SOUND_ENABLED, v).apply()

    private val isFxHigh get() = Prefs.get(this).getString(Prefs.FX_QUALITY, Prefs.FX_QUALITY_HIGH) == Prefs.FX_QUALITY_HIGH
    private val isBlurEnabled get() = isFxHigh && Prefs.get(this).getBoolean(Prefs.FX_BLUR, true)
    private val isHapticEnabled get() = Prefs.get(this).getBoolean(Prefs.HAPTIC_ENABLED, true)

    private fun blurBg() { if (isBlurEnabled) NeonBlur.applyBlur(findViewById(R.id.layoutRoot), 16f) }
    private fun unblurBg() { NeonBlur.clearBlur(findViewById(R.id.layoutRoot)) }
    
    private fun startTimer() {
        if (isGameRunning) return
        isGameRunning = true
        timerRunnable = object : Runnable {
            override fun run() {
                if (isFinishing || isDestroyed) {
                    stopTimer()
                    return
                }
                timerSeconds++
                updateTimerUI()
                timerHandler.postDelayed(this, 1000)
            }
        }
        timerHandler.post(timerRunnable!!)
    }

    private fun stopTimer() {
        isGameRunning = false
        timerRunnable?.let { timerHandler.removeCallbacks(it) }
    }
    
    private fun resetTimer() {
        stopTimer()
        timerSeconds = 0
        updateTimerUI()
    }

    private fun updateTimerUI() {
        val tv = findViewById<android.widget.TextView>(R.id.tvTimer) ?: return
        if (timeAttackLimit > 0) {
            val remaining = (timeAttackLimit - timerSeconds).coerceAtLeast(0)
            val mins = remaining / 60
            val secs = remaining % 60
            tv.text = String.format(Locale.US, "⏱ %02d:%02d", mins, secs)
            val warnColor = ContextCompat.getColor(this, R.color.neon_magenta)
            val normalColor = Prefs.resolveAccentColor(this)
            tv.setTextColor(if (remaining <= 30) warnColor else normalColor)
            tv.setShadowLayer(10f, 0f, 0f,
                if (remaining <= 30) ContextCompat.getColor(this, R.color.neon_magenta_glow)
                else normalColor and 0x66FFFFFF.toInt())
            if (remaining <= 0 && isGameRunning && !timeUpShown) {
                timeUpShown = true
                stopTimer()
                showTimeUpDialog()
            }
        } else {
            val mins = timerSeconds / 60
            val secs = timerSeconds % 60
            tv.text = String.format(Locale.US, "⏱ %02d:%02d", mins, secs)
        }
    }
    
    /** Luu best-moves va best-time per-puzzle (imageResId × size) neu lap ky luc. */
    private fun saveHighScore(moves: Int, seconds: Int) {
        val size = viewModel.boardSize.value ?: return
        val prefs = getSharedPreferences(com.helpmepls.slidepuzzle.util.ScoreUtils.PREFS_NAME, android.content.Context.MODE_PRIVATE)
        val mKey = com.helpmepls.slidepuzzle.util.ScoreUtils.movesKey(currentImageResId, size.width, size.height)
        val tKey = com.helpmepls.slidepuzzle.util.ScoreUtils.timeKey(currentImageResId, size.width, size.height)
        val editor = prefs.edit()
        if (com.helpmepls.slidepuzzle.util.ScoreUtils.isNewBest(moves, prefs.getInt(mKey, com.helpmepls.slidepuzzle.util.ScoreUtils.NO_BEST)))
            editor.putInt(mKey, moves)
        if (com.helpmepls.slidepuzzle.util.ScoreUtils.isNewBestTime(seconds, prefs.getInt(tKey, com.helpmepls.slidepuzzle.util.ScoreUtils.NO_BEST_TIME)))
            editor.putInt(tKey, seconds)
        editor.apply()
    }

    private fun updateMoveBudgetUI(moves: Int) {
        val tv = findViewById<android.widget.TextView>(R.id.tvMoveBudget) ?: return
        val divider = findViewById<View>(R.id.dividerBudget)
        if (moveBudget <= 0) {
            tv.visibility = View.GONE
            divider?.visibility = View.GONE
            return
        }
        tv.visibility = View.VISIBLE
        divider?.visibility = View.VISIBLE
        val remaining = moveBudget - moves
        tv.text = "🎯 $remaining"
        val color = if (remaining <= 5)
            ContextCompat.getColor(this, R.color.neon_magenta)
        else
            ContextCompat.getColor(this, R.color.neon_lime)
        tv.setTextColor(color)
        val glowColor = if (remaining <= 5) R.color.neon_magenta_glow else R.color.neon_lime_glow
        tv.setShadowLayer(10f, 0f, 0f, ContextCompat.getColor(this, glowColor))
    }

    private fun showBudgetExceededDialog(boardView: GameBoard) {
        blurBg()
        DialogUtils.showGameDialog(
            context = this,
            title = "OVER BUDGET!",
            message = "You've exceeded the move\nchallenge limit.\nContinue anyway?",
            yesText = "CONTINUE",
            noText = "TRY AGAIN",
            onYes = { unblurBg() },
            onNo = {
                unblurBg()
                performResetWithAnimation(boardView)
                resetTimer()
                undoCount = 3; hintPenaltyMoves = 0; hintCooldownActive = false; budgetExceededShown = false
                updateAlmostThereBanner(0f, false)
                updateMoveBudgetUI(0)
            }
        )
    }

    private fun showTimeUpDialog() {
        val boardView = findViewById<GameBoard>(R.id.boardView) ?: return
        blurBg()
        DialogUtils.showGameDialog(
            context = this,
            title = "TIME'S UP! ⏱",
            message = "You ran out of time.\nTry again?",
            yesText = "TRY AGAIN",
            noText = "BACK",
            onYes = {
                unblurBg()
                performResetWithAnimation(boardView)
                resetTimer()
                undoCount = 3; hintPenaltyMoves = 0; hintCooldownActive = false
                budgetExceededShown = false; timeUpShown = false
                updateAlmostThereBanner(0f, false); updateMoveBudgetUI(0)
                if (timeAttackLimit > 0) startTimer()
            },
            onNo = { unblurBg(); onBackPressedDispatcher.onBackPressed() }
        )
    }

    // Task 32: hiển thị/ẩn banner "Almost there!" và điều chỉnh glow intensity.
    private fun updateAlmostThereBanner(percent: Float, solved: Boolean) {
        val banner = findViewById<android.widget.TextView>(R.id.tvAlmostThere) ?: return
        val boardView = findViewById<GameBoard>(R.id.boardView)
        if (solved || percent < 0.8f) {
            if (banner.visibility != View.GONE) banner.visibility = View.GONE
            boardView?.setGlowIntensity(1.0f)
        } else {
            if (banner.visibility != View.VISIBLE) banner.visibility = View.VISIBLE
            boardView?.setGlowIntensity(2.0f)
        }
    }

    private fun showWinDialog(moves: Int) {
        lastWinMoves = moves
        stopTimer()
        if (soundEnabled) soundManager.playWin()

        val size = viewModel.boardSize.value
        val p = getSharedPreferences(com.helpmepls.slidepuzzle.util.ScoreUtils.PREFS_NAME, android.content.Context.MODE_PRIVATE)
        val prevBest = size?.let {
            p.getInt(com.helpmepls.slidepuzzle.util.ScoreUtils.movesKey(currentImageResId, it.width, it.height), com.helpmepls.slidepuzzle.util.ScoreUtils.NO_BEST)
        } ?: com.helpmepls.slidepuzzle.util.ScoreUtils.NO_BEST
        val prevBestTime = size?.let {
            p.getInt(com.helpmepls.slidepuzzle.util.ScoreUtils.timeKey(currentImageResId, it.width, it.height), com.helpmepls.slidepuzzle.util.ScoreUtils.NO_BEST_TIME)
        } ?: com.helpmepls.slidepuzzle.util.ScoreUtils.NO_BEST_TIME
        val isNewBest = com.helpmepls.slidepuzzle.util.ScoreUtils.isNewBest(moves, prevBest)
        val isNewBestTime = com.helpmepls.slidepuzzle.util.ScoreUtils.isNewBestTime(timerSeconds, prevBestTime)
        saveHighScore(moves, timerSeconds)

        val boardArea = size?.let { it.width * it.height } ?: 9
        val stars = com.helpmepls.slidepuzzle.util.ScoreUtils.starsFor(moves, boardArea)

        val activity = this
        blurBg()
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_win, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()
        dialog.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(Color.TRANSPARENT))

        // Stars
        val litColor = ContextCompat.getColor(this, R.color.neon_lime)
        val dimColor = ContextCompat.getColor(this, R.color.neon_text_secondary)
        listOf(R.id.tvStar1, R.id.tvStar2, R.id.tvStar3).forEachIndexed { i, id ->
            dialogView.findViewById<android.widget.TextView>(id)?.setTextColor(if (i < stars) litColor else dimColor)
        }

        // Best badge + budget badge (Task 26)
        val badge = dialogView.findViewById<android.widget.TextView>(R.id.tvBestBadge)
        if (isNewBest || isNewBestTime) {
            badge?.text = when {
                isNewBest && isNewBestTime -> "🏆 NEW BEST MOVES & TIME!"
                isNewBest -> "🏆 NEW HIGH SCORE!"
                else -> "⏱ NEW BEST TIME!"
            }
            badge?.visibility = View.VISIBLE
        }
        if (moveBudget > 0) {
            val remaining = moveBudget - moves
            if (remaining >= 0) {
                val budgetText = if (remaining == 0) "🎯 Perfect!" else "✅ Under budget!"
                if (badge?.visibility == View.VISIBLE) {
                    badge.text = "${badge.text}\n$budgetText"
                } else {
                    badge?.text = budgetText
                    badge?.visibility = View.VISIBLE
                }
            }
        }
        if (timeAttackLimit > 0) {
            val remaining = timeAttackLimit - timerSeconds
            if (remaining > 0) {
                val timeText = if (remaining >= 60) {
                    val m = remaining / 60; val s = remaining % 60
                    "⚡ ${m}m ${s}s remaining!"
                } else {
                    "⚡ ${remaining}s remaining!"
                }
                if (badge?.visibility == View.VISIBLE) {
                    badge.text = "${badge.text}\n$timeText"
                } else {
                    badge?.text = timeText
                    badge?.visibility = View.VISIBLE
                }
            }
        }

        // Animated counters
        val tvMoves = dialogView.findViewById<android.widget.TextView>(R.id.tvWinMoves)
        val tvTime = dialogView.findViewById<android.widget.TextView>(R.id.tvWinTime)
        val penaltySuffix = if (hintPenaltyMoves > 0) " (+${hintPenaltyMoves}💡)" else ""
        ValueAnimator.ofInt(0, moves).apply {
            duration = 600L; interpolator = DecelerateInterpolator()
            addUpdateListener { tvMoves?.text = "📊 ${it.animatedValue}$penaltySuffix" }
            start()
        }
        ValueAnimator.ofInt(0, timerSeconds).apply {
            duration = 600L; interpolator = DecelerateInterpolator()
            addUpdateListener {
                val v = it.animatedValue as Int
                tvTime?.text = String.format(Locale.US, "⏱ %02d:%02d", v / 60, v % 60)
            }
            start()
        }

        dialogView.findViewById<android.widget.Button>(R.id.btnWinShare)?.setOnClickListener {
            dialog.dismiss(); unblurBg(); activity.shareSuccess()
        }
        dialogView.findViewById<android.widget.Button>(R.id.btnWinClose)?.setOnClickListener {
            dialog.dismiss(); unblurBg()
        }
        dialog.show()
    }

    private fun shareSuccess() {
        val boardView = findViewById<GameBoard>(R.id.boardView)
        if (boardView == null || boardView.width == 0) {
            // Fallback text-only nếu board chưa layout.
            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                putExtra(android.content.Intent.EXTRA_TEXT,
                    "I solved a Neon Puzzle in $lastWinMoves moves (${ShareUtils.formatTime(timerSeconds)})! 🧩✨")
                type = "text/plain"
            }
            startActivity(android.content.Intent.createChooser(intent, "Share Achievement"))
            return
        }
        try {
            val puzzleBmp = boardView.getCompletedBitmap()
            val shareBmp = ShareUtils.createShareBitmap(this, puzzleBmp, lastWinMoves, timerSeconds)
            ShareUtils.shareImage(this, shareBmp, lastWinMoves, timerSeconds)
            puzzleBmp.recycle()
            shareBmp.recycle()
        } catch (_: Exception) {
            // FileProvider không khả dụng (test environment) — fallback text.
            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                putExtra(android.content.Intent.EXTRA_TEXT,
                    "I solved a Neon Puzzle in $lastWinMoves moves! 🧩✨")
                type = "text/plain"
            }
            startActivity(android.content.Intent.createChooser(intent, "Share Achievement"))
        }
    }


    private fun performShuffleWithAnimation(boardView: GameBoard) {
        // Sử dụng built-in shuffle animation của GameBoard
        boardView.shuffleWithAnimation()
    }

    private fun performResetWithAnimation(boardView: GameBoard) {
        // Fade out effect trước khi reset
        val fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.puzzle_reset_effect)

        boardView.startAnimation(fadeOutAnimation)

        // Reset sau khi fade out
        boardView.postDelayed({
            boardView.shuffle(true) // true = reset

            // Fade in với bounce effect
            val restoreAnimation = AnimationUtils.loadAnimation(this, R.anim.puzzle_reset_restore)
            boardView.startAnimation(restoreAnimation)
        }, 300)
    }

    private val viewModel: BoardOptionsVm by lazy {
        ViewModelProvider(this)[BoardOptionsVm::class.java]
    }

    private fun initGameLogic(boardView: GameBoard) {
        boardView.onMoveListener = { moves, solved ->
            if (!isGameRunning && moves > 0 && !solved) {
                startTimer()
            }
            if (!solved && isHapticEnabled) {
                boardView.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
            }
            findViewById<android.widget.TextView>(R.id.tvMoves)?.text = "📊 $moves"

            // Task 26: cập nhật budget countdown và check vượt budget.
            updateMoveBudgetUI(moves)
            if (moveBudget > 0 && !solved && moves > moveBudget + 10 && !budgetExceededShown) {
                budgetExceededShown = true
                showBudgetExceededDialog(boardView)
            }

            // Task 32: cập nhật banner "Almost there!" sau mỗi move.
            val percent = boardView.correctTilePercent()
            updateAlmostThereBanner(percent, solved)

            if (solved) {
                stopTimer()
                updateAlmostThereBanner(1.0f, true)  // Ẩn banner khi win.
                boardView.playWinFeedback()
                // Wave 11: bung confetti ngay khi solved (trước dialog 500ms → full-screen effect rõ hơn).
                if (!Prefs.get(this).getBoolean(Prefs.FX_REDUCE_MOTION, false)) {
                    val cx = boardView.x + boardView.width / 2f
                    val cy = boardView.y + boardView.height / 2f
                    findViewById<NeonParticleView>(R.id.particleView)?.burst(cx, cy)
                }
                boardView.postDelayed({
                    if (!isFinishing && !isDestroyed) showWinDialog(moves)
                }, 500)
            }
        }
    }

    private fun mountBoard() {
        val ivOriginal = findViewById<ImageView>(R.id.ivOriginal)
        val boardView = findViewById<GameBoard>(R.id.boardView)

        showNumbers = loadShowNumbers()
        boardView.showNumbers = showNumbers

        soundEnabled = loadSoundEnabled()
        soundManager.isEnabled = soundEnabled
        boardView.onMoveSound = { soundManager.playMove() }

        initGameLogic(boardView)

        // Wave 10: khởi tạo viền gradient động theo fx_quality.
        findViewById<NeonBorderView>(R.id.neonBorderView)?.showBorder = isFxHigh

        // Glow completeness: elevation shadow tint trên board + stats panel + bottom dock (API 28+).
        val accent = Prefs.resolveAccentColor(this)
        NeonGlow.apply(boardView, accent)
        NeonGlow.apply(findViewById(R.id.statsPanel), accent)
        NeonGlow.apply(findViewById(R.id.layoutBottom), accent)

        viewModel.boardSize.observe(
            /* owner = */ this,
            /* observer = */ Observer {
                it?.let {
                    ivOriginal.setImageBitmap(viewModel.boardImage.value)
                    boardView.resize(
                        size = Size(it.width, it.height),
                        image = viewModel.boardImage.value
                    )
                    resetTimer()
                    undoCount = 3
                    budgetExceededShown = false; timeUpShown = false
                    updateMoveBudgetUI(0)
                    if (timeAttackLimit > 0) startTimer()
                }
            }
        )
        val shuffleButton = findViewById<Button>(R.id.btShuffle)
        val resetButton = findViewById<Button>(R.id.btReset)
        val undoButton = findViewById<android.widget.ImageButton>(R.id.btUndo)

        val hintButton = findViewById<android.widget.ImageButton>(R.id.btnHint)
        shuffleButton.addSpringClickAnimation()
        resetButton.addSpringClickAnimation()
        undoButton.addSpringClickAnimation()
        hintButton?.addSpringClickAnimation()

        hintButton?.setOnClickListener {
            if (hintCooldownActive) {
                android.widget.Toast.makeText(this, "Hint cooling down...", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val bv = findViewById<GameBoard>(R.id.boardView) ?: return@setOnClickListener
            val wrongTiles = bv.getWrongPositionTiles()
            if (wrongTiles.isEmpty()) {
                android.widget.Toast.makeText(this, "All tiles are correct!", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val (gx, gy) = wrongTiles.first()
            bv.flashHintTile(gx, gy)
            hintPenaltyMoves += 10
            hintCooldownActive = true
            hintButton.alpha = 0.4f
            hintButton.postDelayed({
                hintCooldownActive = false
                hintButton.alpha = 1.0f
            }, 3000)
        }
        
        undoButton.setOnClickListener {
            if (undoCount > 0) {
                 if (boardView.undo()) {
                     undoCount--
                     android.widget.Toast.makeText(this, "Undo left: $undoCount", android.widget.Toast.LENGTH_SHORT).show()
                 } else {
                     android.widget.Toast.makeText(this, "Nothing to undo!", android.widget.Toast.LENGTH_SHORT).show()
                 }
            } else {
                 android.widget.Toast.makeText(this, "No undos left!", android.widget.Toast.LENGTH_SHORT).show()
            }
        }

        ivOriginal.setOnClickListener {
             // Toggle Preview? Or just existing behavior.
             // Currently ivOriginal is small preview. 
             // Request was "Hold to preview".
             // We can implement TouchListener on a "Eye" button, OR just make ivOriginal expand on Hold.
        }
        
        // Board Preview Feature: Long press ivOriginal to show full size dialog or expand?
        // Let's implement simple "Hold to preview" on a new button or existing UI.
        // Actually user said "Board Preview: Nút giữ để xem". 
        // I'll make the ivOriginal clickable/holdable.
        ivOriginal.setOnTouchListener { v, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    v.animate().scaleX(3f).scaleY(3f).translationY(300f).setDuration(200).start()
                    v.elevation = 100f
                }
                android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                    v.animate().scaleX(1f).scaleY(1f).translationY(0f).setDuration(200).start()
                    v.elevation = 0f
                }
            }
            true
        }


        shuffleButton.setOnClickListener {
            showShuffleDialog(boardView)
        }
        resetButton.setOnClickListener {
            showResetDialog(boardView)
        }
    }

    // Unified Dialog Function removed, use DialogUtils instead.
    
    private fun showShuffleDialog(boardView: GameBoard) {
        blurBg()
        DialogUtils.showGameDialog(
            context = this,
            title = "SHUFFLE PUZZLE",
            message = "Do you want to shuffle\nthe puzzle pieces?",
            yesText = "YES",
            noText = "NO",
            onYes = {
                unblurBg(); performShuffleWithAnimation(boardView); resetTimer()
                undoCount = 3; hintPenaltyMoves = 0; hintCooldownActive = false
                budgetExceededShown = false; timeUpShown = false
                updateAlmostThereBanner(0f, false); updateMoveBudgetUI(0)
                if (timeAttackLimit > 0) startTimer()
            },
            onNo = { unblurBg() }
        )
    }

    private fun showResetDialog(boardView: GameBoard) {
        blurBg()
        DialogUtils.showGameDialog(
            context = this,
            title = "RESET PUZZLE",
            message = "Do you want to reset\nto original state?",
            yesText = "RESET",
            noText = "CANCEL",
            onYes = {
                unblurBg(); performResetWithAnimation(boardView); resetTimer()
                undoCount = 3; hintPenaltyMoves = 0; hintCooldownActive = false
                budgetExceededShown = false; timeUpShown = false
                updateAlmostThereBanner(0f, false); updateMoveBudgetUI(0)
                if (timeAttackLimit > 0) startTimer()
            },
            onNo = { unblurBg() }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Enable window content transitions
        window.requestFeature(android.view.Window.FEATURE_CONTENT_TRANSITIONS)
        window.enterTransition = android.transition.Fade()
        window.exitTransition = android.transition.Fade()
        super.onCreate(savedInstanceState)

        currentImageResId = intent.getIntExtra(EXTRA_IMAGE_RES_ID, BoardOptionsVm.PREDEFINED_IMAGES.first().first)
        val boardWidth = intent.getIntExtra(EXTRA_BOARD_WIDTH, BoardOptionsVm.PREDEFINED_BOARD_SIZE[1].width)
        val boardHeight = intent.getIntExtra(EXTRA_BOARD_HEIGHT, BoardOptionsVm.PREDEFINED_BOARD_SIZE[1].height)
        moveBudget = intent.getIntExtra(EXTRA_MOVE_BUDGET, -1)
        timeAttackLimit = intent.getIntExtra(EXTRA_TIME_LIMIT_SECONDS, -1)
        viewModel.apply {
            boardSize.value = BoardTitledSize(width = boardWidth, height = boardHeight)
            boardImage.value = decodeBoardBitmap(currentImageResId)
        }
        
        // Postpone enter transition until image is loaded (though we have bitmap in memory)
        supportPostponeEnterTransition()
        
        setContentView(R.layout.act_game)
        
        // Hero Animation: Set transition name on destination view
        val ivOriginal = findViewById<ImageView>(R.id.ivOriginal)
        val transitionName = intent.getStringExtra("TRANSITION_NAME")
        if (transitionName != null && ivOriginal != null) {
            androidx.core.view.ViewCompat.setTransitionName(ivOriginal, transitionName)
        }
        
        // Start transition
        supportStartPostponedEnterTransition()
        
        // Ensure icon color is correct (white)
        // Status bar background color is now handled by the red FrameLayout in XML
        androidx.core.view.WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false 
        }

        setSupportActionBar(findViewById(R.id.tbBoardOptions))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Setup OnBackPressedCallback
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
                // Removed overridePendingTransition to allow ActivityOptions transition to work
            }
        })

        mountBoard()

        // Simple Animation as requested
        val fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        
        ivOriginal.startAnimation(fadeIn)
        
        val boardView = findViewById<GameBoard>(R.id.boardView)
        boardView.startAnimation(fadeIn)
        
        findViewById<android.view.View>(R.id.topInfoBar).visibility = View.VISIBLE
        findViewById<android.view.View>(R.id.layoutBottom).visibility = View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        menuInflater.inflate(R.menu.menu_game, menu)
        menu.findItem(R.id.action_show_numbers)?.isChecked = showNumbers
        menu.findItem(R.id.action_sound)?.isChecked = soundEnabled
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                return true
            }
            R.id.action_show_numbers -> {
                showNumbers = !showNumbers
                item.isChecked = showNumbers
                saveShowNumbers(showNumbers)
                findViewById<GameBoard>(R.id.boardView)?.showNumbers = showNumbers
                return true
            }
            R.id.action_sound -> {
                soundEnabled = !soundEnabled
                item.isChecked = soundEnabled
                saveSoundEnabled(soundEnabled)
                soundManager.isEnabled = soundEnabled
                return true
            }
            R.id.action_settings -> {
                startActivity(android.content.Intent(this, SettingsActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        androidx.core.view.WindowCompat.getInsetsController(window, window.decorView)?.apply {
            isAppearanceLightStatusBars = false
        }
        // Reload settings khi quay lai tu SettingsActivity.
        showNumbers = loadShowNumbers()
        soundEnabled = loadSoundEnabled()
        soundManager.isEnabled = soundEnabled
        findViewById<GameBoard>(R.id.boardView)?.showNumbers = showNumbers
        findViewById<NeonBorderView>(R.id.neonBorderView)?.showBorder = isFxHigh
        // Refresh NeonGlow shadow tint nếu accent thay đổi mà không recreate.
        val accent = Prefs.resolveAccentColor(this)
        NeonGlow.apply(findViewById(R.id.boardView), accent)
        NeonGlow.apply(findViewById(R.id.statsPanel), accent)
        NeonGlow.apply(findViewById(R.id.layoutBottom), accent)
        invalidateOptionsMenu()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
        // soundManager luon duoc khoi tao trong mountBoard (onCreate) -> release an toan.
        soundManager.release()
        // Giai phong bitmap raster lon (~vai MB) khi thoat han. Bitmap vector trong cache
        // duoc giu lai (dung chung qua cac phien) nen khong recycle.
        if (isFinishing) {
            val img = viewModel.boardImage.value
            if (img != null && !img.isRecycled && !vectorCache.containsValue(img)) {
                img.recycle()
            }
        }
    }

    private fun decodeBoardBitmap(imageResId: Int): android.graphics.Bitmap {
        // Ảnh từ gallery — load từ cache file đã lưu.
        val customPath = intent.getStringExtra(EXTRA_CUSTOM_IMAGE_PATH)
        if (customPath != null) {
            val bmp = BitmapFactory.decodeFile(customPath)
            if (bmp != null) return bmp
            // File lỗi/mất — dùng ảnh xám thay vì crash (imageResId có thể là 0).
            return android.graphics.Bitmap.createBitmap(512, 512, android.graphics.Bitmap.Config.ARGB_8888).also {
                android.graphics.Canvas(it).drawColor(0xFF1A1A2E.toInt())
            }
        }

        val maxTextureSize = resources.displayMetrics.widthPixels.coerceAtLeast(1)
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeResource(resources, imageResId, options)

        // outWidth <= 0 => khong phai raster (vd VectorDrawable neon) -> render + cache.
        if (options.outWidth <= 0) {
            return renderVectorCached(imageResId)
        }

        options.inSampleSize = calculateInSampleSize(
            srcWidth = options.outWidth,
            srcHeight = options.outHeight,
            reqWidth = maxTextureSize,
            reqHeight = maxTextureSize,
        )
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeResource(resources, imageResId, options)
            ?: renderVectorCached(imageResId)
    }

    /** Lay bitmap vector tu cache theo resId, render moi neu chua co. Khong recycle (cache dung chung). */
    private fun renderVectorCached(resId: Int): android.graphics.Bitmap {
        vectorCache[resId]?.takeIf { !it.isRecycled }?.let { return it }
        val bitmap = renderDrawableToSquareBitmap(resId, VECTOR_RENDER_SIZE)
        vectorCache[resId] = bitmap
        return bitmap
    }

    /** Render 1 drawable (vd VectorDrawable) thanh bitmap vuong size x size de lam anh ghep. */
    private fun renderDrawableToSquareBitmap(resId: Int, size: Int): android.graphics.Bitmap {
        val drawable = androidx.appcompat.content.res.AppCompatResources.getDrawable(this, resId)
            ?: throw IllegalArgumentException("Khong load duoc drawable id=$resId")
        val side = size.coerceAtLeast(1)
        val bitmap = android.graphics.Bitmap.createBitmap(
            side, side, android.graphics.Bitmap.Config.ARGB_8888
        )
        val canvas = android.graphics.Canvas(bitmap)
        drawable.setBounds(0, 0, side, side)
        drawable.draw(canvas)
        return bitmap
    }

    private fun calculateInSampleSize(
        srcWidth: Int,
        srcHeight: Int,
        reqWidth: Int,
        reqHeight: Int,
    ): Int {
        var inSampleSize = 1
        if (srcHeight > reqHeight || srcWidth > reqWidth) {
            var halfHeight = srcHeight / 2
            var halfWidth = srcWidth / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}
