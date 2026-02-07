package com.helpmepls.slidepuzzle.act

import android.os.Bundle
import android.util.Size
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.activity.OnBackPressedCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.helpmepls.sdkadbmob.UIUtils
import com.helpmepls.slidepuzzle.R
import com.helpmepls.slidepuzzle.game.GameBoard
import com.helpmepls.slidepuzzle.model.BoardActivityParams
import com.helpmepls.slidepuzzle.vm.BoardOptionsVm

class GameAct : AppCompatActivity() {
    companion object Companion {
        lateinit var initialConfig: BoardActivityParams
    }

    private fun View.addSpringClickAnimation() {
        this.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    val pressAnimation = AnimationUtils.loadAnimation(context, R.anim.spring_button_press)
                    view.startAnimation(pressAnimation)
                    view.performClick()
                }
                android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                    val releaseAnimation = AnimationUtils.loadAnimation(context, R.anim.spring_button_release)
                    view.startAnimation(releaseAnimation)
                }
            }
            true
        }
    }

    private var timerSeconds = 0
    private var timerHandler = android.os.Handler(android.os.Looper.getMainLooper())
    private var timerRunnable: Runnable? = null
    private var isGameRunning = false
    private var bestScore = 0
    private var undoCount = 3
    
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
        val mins = timerSeconds / 60
        val secs = timerSeconds % 60
        findViewById<android.widget.TextView>(R.id.tvTimer)?.text = String.format("⏱ %02d:%02d", mins, secs)
    }
    
    private fun saveHighScore(moves: Int, time: Int) {
        val prefs = getSharedPreferences("puzzle_prefs", android.content.Context.MODE_PRIVATE)
        val key = "best_${initialConfig.size.width}x${initialConfig.size.height}"
        val currentBest = prefs.getInt(key, Int.MAX_VALUE)
        if (moves < currentBest) {
             prefs.edit().putInt(key, moves).apply()
             bestScore = moves
        }
    }
    
    private fun loadHighScore() {
        val prefs = getSharedPreferences("puzzle_prefs", android.content.Context.MODE_PRIVATE)
        val key = "best_${initialConfig.size.width}x${initialConfig.size.height}"
        bestScore = prefs.getInt(key, 0)
        if (bestScore == Int.MAX_VALUE) bestScore = 0
    }

    private fun showWinDialog(moves: Int) {
        stopTimer()
        val isNewBest = bestScore > 0 && moves <= bestScore // Simplified check, strictly speaking if best was MAX_VALUE, any score is best.
        
        // Save score
        saveHighScore(moves, timerSeconds)

        val timerText = findViewById<android.widget.TextView>(R.id.tvTimer)?.text ?: "00:00"
        val newBestText = if (isNewBest) "\n\n🏆 NEW HIGH SCORE! 🏆" else ""
        
        MaterialAlertDialogBuilder(this)
            .setTitle("🎉 Congratulations! 🎉")
            .setMessage("You solved the puzzle in $moves moves and $timerText!$newBestText")
            .setPositiveButton("Share") { dialog, _ ->
                dialog.dismiss()
                shareSuccess()
            }
            .setNegativeButton("Close") { dialog, _ ->
                dialog.dismiss()
            }
            .setNeutralButton("Replay") { dialog, _ ->
                dialog.dismiss()
                findViewById<Button>(R.id.btReset).performClick()
            }
            .show()
            
        // Play success sound / vibration here if Audio implemented
    }

    private fun shareSuccess() {
        // Implement simplified share intent
        val shareIntent = android.content.Intent().apply {
            action = android.content.Intent.ACTION_SEND
            putExtra(android.content.Intent.EXTRA_TEXT, "I just solved the Slide Puzzle in $timerSeconds seconds! Can you beat me?")
            type = "text/plain"
        }
        startActivity(android.content.Intent.createChooser(shareIntent, "Share Achievement"))
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
             findViewById<android.widget.TextView>(R.id.tvMoves)?.text = "📊 $moves"
             
             if (solved) {
                 stopTimer()
                 showWinDialog(moves)
             }
        }
        
        loadHighScore()
    }

    private fun mountBoard() {
        val ivOriginal = findViewById<ImageView>(R.id.ivOriginal)
        val boardView = findViewById<GameBoard>(R.id.boardView)
        
        initGameLogic(boardView)

        viewModel.boardSize.observe(
            /* owner = */ this,
            /* observer = */ Observer {
//                Log.d("roy93~", "observe")
                it?.let {
                    ivOriginal.setImageBitmap(viewModel.boardImage.value)
                    boardView.resize(
                        size = Size(it.width, it.height),
                        image = viewModel.boardImage.value
                    )
                    resetTimer()
                    undoCount = 3 // Reset undo limit
                }
            }
        )
        val shuffleButton = findViewById<Button>(R.id.btShuffle)
        val resetButton = findViewById<Button>(R.id.btReset)
        val undoButton = findViewById<android.widget.ImageButton>(R.id.btUndo)

        shuffleButton.addSpringClickAnimation()
        resetButton.addSpringClickAnimation()
        undoButton.addSpringClickAnimation()
        
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
            showDlg(
                title = "Shuffle Puzzle",
                message = "Do you want to shuffle the puzzle pieces?",
                onYes = {
                    // Thêm shuffle animation effect
                    performShuffleWithAnimation(boardView)
                    resetTimer()
                    undoCount = 3
                },
                onNo = {
                    // Xử lý khi nhấn No
                }
            )
        }
        resetButton.setOnClickListener {
            showDlg(
                title = "Reset Puzzle",
                message = "Do you want to reset the puzzle to its original state?",
                onYes = {
                    // Thêm reset animation effect
                    performResetWithAnimation(boardView)
                    resetTimer()
                    undoCount = 3
                },
                onNo = {
                    // Xử lý khi nhấn No
                }
            )
        }
    }

    private fun showDlg(
        title: String = "Confirmation",
        message: String = "Are you sure you want to continue?",
        onYes: () -> Unit,
        onNo: () -> Unit,
    ) {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                // Post to next frame to prevent ANR
                findViewById<android.view.View>(android.R.id.content).post {
                    onYes()
                }
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
                // Post to next frame to prevent ANR
                findViewById<android.view.View>(android.R.id.content).post {
                    onNo()
                }
            }
            .create()

        dialog.show()

        // Add entrance animation to dialog
        dialog.window?.let { window ->
            val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce_scale_in)
            window.decorView.startAnimation(bounceAnimation)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel.apply {
            boardSize.value = initialConfig.size
            boardImage.value = initialConfig.bitmap
        }
        super.onCreate(savedInstanceState)
        
        // REMOVED WindowCompat.setDecorFitsSystemWindows - it might be resetting colors
        android.util.Log.d("roy93~", "GameAct: API Level = ${android.os.Build.VERSION.SDK_INT}")
        
        setContentView(R.layout.act_game)
        
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
                overridePendingTransition(R.anim.smooth_slide_in_left, R.anim.smooth_slide_out_right)
            }
        })

        mountBoard()

        // Add elegant entrance animations
        val ivOriginal = findViewById<ImageView>(R.id.ivOriginal)
        val boardView = findViewById<GameBoard>(R.id.boardView)

        // Stagger the animations for better visual effect
        ivOriginal.alpha = 0f
        boardView.alpha = 0f

        val elegantFadeIn = AnimationUtils.loadAnimation(this, R.anim.elegant_fade_in)
        val bounceScaleIn = AnimationUtils.loadAnimation(this, R.anim.bounce_scale_in)

        // Start image animation first
        ivOriginal.postDelayed({
            ivOriginal.alpha = 1f
            ivOriginal.startAnimation(elegantFadeIn)
        }, 200)

        // Start board animation with delay for staggered effect
        boardView.postDelayed({
            boardView.alpha = 1f
            boardView.startAnimation(bounceScaleIn)
        }, 600)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    
    override fun onResume() {
        super.onResume()
        // Force status bar color on resume to ensure it persists
        val primaryColor = androidx.core.content.ContextCompat.getColor(this, R.color.md_theme_primary)
        window.statusBarColor = primaryColor
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        androidx.core.view.WindowCompat.getInsetsController(window, window.decorView)?.apply {
            isAppearanceLightStatusBars = false
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
    }
}
