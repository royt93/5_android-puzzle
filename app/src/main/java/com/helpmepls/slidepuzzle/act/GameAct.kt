package com.helpmepls.slidepuzzle.act

import android.os.Bundle
import android.util.Size
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.helpmepls.slidepuzzle.R
import com.helpmepls.slidepuzzle.game.GameBoard
import com.helpmepls.slidepuzzle.model.BoardActivityParams
import com.helpmepls.slidepuzzle.vm.BoardOptionsVm

//TODO roy93~ show anh goc ben tren
class GameAct : AppCompatActivity() {
    companion object Companion {
        lateinit var initialConfig: BoardActivityParams
    }

    private val viewModel: BoardOptionsVm by lazy {
        ViewModelProviders.of(this).get(BoardOptionsVm::class.java)
    }

    private fun mountBoard() {
        val board = findViewById<GameBoard>(R.id.boardView)
        viewModel.boardSize.observe(
            /* owner = */ this,
            /* observer = */ Observer {
                it?.let {
                    board.resize(
                        size = Size(it.width, it.height),
                        image = viewModel.boardImage.value
                    )
                }
            }
        )
        findViewById<Button>(R.id.btShuffle).setOnClickListener {
            board.shuffle()
            showDlg()
        }
        findViewById<Button>(R.id.btReset).setOnClickListener {
            board.shuffle(true)
        }
    }

    private fun showDlg() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Confirmation")
            .setMessage("Are you sure you want to continue?")
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel.apply {
            boardSize.value = initialConfig.size
            boardImage.value = initialConfig.bitmap
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_game)
        setSupportActionBar(findViewById(R.id.tbBoardOptions))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mountBoard()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
