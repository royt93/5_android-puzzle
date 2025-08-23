package com.helpmepls.slidepuzzle.act

import android.os.Bundle
import android.util.Size
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.helpmepls.slidepuzzle.R
import com.helpmepls.slidepuzzle.game.GameBoard
import com.helpmepls.slidepuzzle.model.BoardActivityParams
import com.helpmepls.slidepuzzle.vm.BoardOptionsViewModel

class GameAct : AppCompatActivity() {
    companion object Companion {
        lateinit var initialConfig: BoardActivityParams
    }

    private val viewModel: BoardOptionsViewModel by lazy {
        ViewModelProviders.of(this).get(BoardOptionsViewModel::class.java)
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
        findViewById<Button>(R.id.shuffle).setOnClickListener {
            board.shuffle()
        }
        findViewById<Button>(R.id.reset).setOnClickListener {
            board.shuffle(true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel.apply {
            boardSize.value = initialConfig.size
            boardImage.value = initialConfig.bitmap
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_activity)
        setSupportActionBar(findViewById(R.id.board_options_toolbar))
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
