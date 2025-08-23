package com.helpmepls.slidepuzzle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.helpmepls.slidepuzzle.ui.boardoptions.BoardOptionsFragment

class BoardOptionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_options_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, BoardOptionsFragment.newInstance())
                .commitNow()
        }
    }

}
