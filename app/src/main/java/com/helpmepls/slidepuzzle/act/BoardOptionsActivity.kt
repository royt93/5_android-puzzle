package com.helpmepls.slidepuzzle.act

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.helpmepls.slidepuzzle.R
import com.helpmepls.slidepuzzle.frm.BoardOptionsFragment

class BoardOptionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_options_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, BoardOptionsFragment.Companion.newInstance())
                .commitNow()
        }
    }

}