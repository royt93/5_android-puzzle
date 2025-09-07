package com.helpmepls.slidepuzzle.act

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.helpmepls.sdkadbmob.UIUtils
import com.helpmepls.slidepuzzle.R
import com.helpmepls.slidepuzzle.frm.BoardOptionsFrm

class BoardOptionsAct : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        UIUtils.setupEdgeToEdge1(window)
        setContentView(R.layout.act_board_options)
//        UIUtils.setupEdgeToEdge2(findViewById(R.id.layoutRoot))
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
