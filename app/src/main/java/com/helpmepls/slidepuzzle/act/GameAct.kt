package com.helpmepls.slidepuzzle.act

import android.os.Bundle
import android.util.Size
import android.view.MenuItem
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
        findViewById<Button>(R.id.btShuffle).setOnClickListener {
            showDlg(
                onYes = {
                    // Xử lý khi nhấn Yes
                    boardView.shuffle()
                },
                onNo = {
                    // Xử lý khi nhấn No
                }
            )
        }
        findViewById<Button>(R.id.btReset).setOnClickListener {
            showDlg(
                onYes = {
                    // Xử lý khi nhấn Yes
                    boardView.shuffle(true)
                },
                onNo = {
                    // Xử lý khi nhấn No
                }
            )
        }
    }

    private fun showDlg(
        onYes: () -> Unit,
        onNo: () -> Unit,
    ) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Confirmation")
            .setMessage("Are you sure you want to continue?")
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
            .show()
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
                overridePendingTransition(0, 0)
            }
        })

        mountBoard()
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
