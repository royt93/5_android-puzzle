package com.helpmepls.slidepuzzle.act

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.helpmepls.slidepuzzle.R
import com.helpmepls.slidepuzzle.frm.BoardOptionsFrm

class BoardOptionsAct : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_board_options)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    /* containerViewId = */ R.id.container,
                    /* fragment = */ BoardOptionsFrm.Companion.newInstance()
                )
                .commitNow()
        }
        setupViews()
    }

    private fun setupViews() {

    }
}
