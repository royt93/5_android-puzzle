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
import com.helpmepls.slidepuzzle.model.BoardTitledSize
import com.helpmepls.slidepuzzle.vm.BoardOptionsVm

class GameAct : BaseActivity() {
    companion object Companion {
        const val EXTRA_IMAGE_RES_ID = "com.helpmepls.slidepuzzle.EXTRA_IMAGE_RES_ID"
        const val EXTRA_BOARD_WIDTH = "com.helpmepls.slidepuzzle.EXTRA_BOARD_WIDTH"
        const val EXTRA_BOARD_HEIGHT = "com.helpmepls.slidepuzzle.EXTRA_BOARD_HEIGHT"

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

    private var timerSeconds = 0
    private var timerHandler = android.os.Handler(android.os.Looper.getMainLooper())
    private var timerRunnable: Runnable? = null
    private var isGameRunning = false
    private var undoCount = 3
    private var showNumbers = true

    private fun loadShowNumbers(): Boolean {
        return getSharedPreferences("puzzle_prefs", android.content.Context.MODE_PRIVATE)
            .getBoolean("show_numbers", true)
    }

    private fun saveShowNumbers(value: Boolean) {
        getSharedPreferences("puzzle_prefs", android.content.Context.MODE_PRIVATE)
            .edit().putBoolean("show_numbers", value).apply()
    }
    
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
        findViewById<android.widget.TextView>(R.id.tvTimer)?.text = String.format(Locale.US, "⏱ %02d:%02d", mins, secs)
    }
    
    private fun saveHighScore(moves: Int) {
        val size = viewModel.boardSize.value ?: return
        val prefs = getSharedPreferences("puzzle_prefs", android.content.Context.MODE_PRIVATE)
        val key = "best_${size.width}x${size.height}"
        if (moves < prefs.getInt(key, com.helpmepls.slidepuzzle.util.ScoreUtils.NO_BEST)) {
            prefs.edit().putInt(key, moves).apply()
        }
    }

    private fun showWinDialog(moves: Int) {
        stopTimer()

        // New best dung ca o lan giai dau tien (chua co record => prevBest = MAX_VALUE).
        val size = viewModel.boardSize.value
        val prefs = getSharedPreferences("puzzle_prefs", android.content.Context.MODE_PRIVATE)
        val prevBest = size?.let {
            prefs.getInt("best_${it.width}x${it.height}", com.helpmepls.slidepuzzle.util.ScoreUtils.NO_BEST)
        } ?: com.helpmepls.slidepuzzle.util.ScoreUtils.NO_BEST
        val isNewBest = com.helpmepls.slidepuzzle.util.ScoreUtils.isNewBest(moves, prevBest)

        // Save score
        saveHighScore(moves)

        val timerText = findViewById<android.widget.TextView>(R.id.tvTimer)?.text ?: "00:00"
        val newBestText = if (isNewBest) "\n\n🏆 NEW HIGH SCORE! 🏆" else ""
        
        DialogUtils.showGameDialog(
            context = this,
            title = "🎉 VICTORY! 🎉",
            message = "You solved it in $moves moves and $timerText!$newBestText",
            yesText = "SHARE",
            noText = "CLOSE",
            onYes = {
                shareSuccess()
            },
            onNo = {
                // Just close
            }
        )
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
                 boardView.playWinFeedback()
                 // Hoan dialog mot nhip de thay hieu ung loe sang truoc (M2).
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
        initGameLogic(boardView)

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
            showShuffleDialog(boardView)
        }
        resetButton.setOnClickListener {
            showResetDialog(boardView)
        }
    }

    // Unified Dialog Function removed, use DialogUtils instead.
    
    private fun showShuffleDialog(boardView: GameBoard) {
        DialogUtils.showGameDialog(
            context = this,
            title = "SHUFFLE PUZZLE",
            message = "Do you want to shuffle\nthe puzzle pieces?",
            yesText = "YES",
            noText = "NO",
            onYes = {
                 performShuffleWithAnimation(boardView)
                 resetTimer()
                 undoCount = 3
            }
        )
    }

    private fun showResetDialog(boardView: GameBoard) {
        DialogUtils.showGameDialog(
            context = this,
            title = "RESET PUZZLE",
            message = "Do you want to reset\nto original state?",
            yesText = "RESET",
            noText = "CANCEL",
            onYes = {
                performResetWithAnimation(boardView)
                resetTimer()
                undoCount = 3
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Enable window content transitions
        window.requestFeature(android.view.Window.FEATURE_CONTENT_TRANSITIONS)
        window.enterTransition = android.transition.Fade()
        window.exitTransition = android.transition.Fade()
        super.onCreate(savedInstanceState)

        val imageResId = intent.getIntExtra(
            EXTRA_IMAGE_RES_ID,
            BoardOptionsVm.PREDEFINED_IMAGES.first().first
        )
        val boardWidth = intent.getIntExtra(EXTRA_BOARD_WIDTH, BoardOptionsVm.PREDEFINED_BOARD_SIZE[1].width)
        val boardHeight = intent.getIntExtra(EXTRA_BOARD_HEIGHT, BoardOptionsVm.PREDEFINED_BOARD_SIZE[1].height)
        viewModel.apply {
            boardSize.value = BoardTitledSize(width = boardWidth, height = boardHeight)
            boardImage.value = decodeBoardBitmap(imageResId)
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
        }
        return super.onOptionsItemSelected(item)
    }
    
    override fun onResume() {
        super.onResume()
        androidx.core.view.WindowCompat.getInsetsController(window, window.decorView)?.apply {
            isAppearanceLightStatusBars = false
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
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
        val maxTextureSize = resources.displayMetrics.widthPixels.coerceAtLeast(1)
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
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
