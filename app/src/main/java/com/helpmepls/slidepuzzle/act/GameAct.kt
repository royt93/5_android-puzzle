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

    private fun mountBoard() {
        val ivOriginal = findViewById<ImageView>(R.id.ivOriginal)
        val boardView = findViewById<GameBoard>(R.id.boardView)
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
                }
            }
        )
        val shuffleButton = findViewById<Button>(R.id.btShuffle)
        val resetButton = findViewById<Button>(R.id.btReset)

        shuffleButton.addSpringClickAnimation()
        resetButton.addSpringClickAnimation()

        shuffleButton.setOnClickListener {
            showDlg(
                title = "Shuffle Puzzle",
                message = "Do you want to shuffle the puzzle pieces?",
                onYes = {
                    // Thêm shuffle animation effect
                    performShuffleWithAnimation(boardView)
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
        UIUtils.setupEdgeToEdge1(window = window)
        setContentView(R.layout.act_game)
        UIUtils.setupEdgeToEdge2(
            rootView = findViewById(R.id.layoutRoot),
//            paddingBottom = false
        )
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
}
