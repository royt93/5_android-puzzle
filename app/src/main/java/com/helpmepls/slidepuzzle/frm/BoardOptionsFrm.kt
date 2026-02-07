package com.helpmepls.slidepuzzle.frm

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.helpmepls.sdkadbmob.UIUtils
import com.helpmepls.slidepuzzle.R
import com.helpmepls.slidepuzzle.act.GameAct
import com.helpmepls.slidepuzzle.adt.ImageCardsAdt
import com.helpmepls.slidepuzzle.model.BoardActivityParams
import com.helpmepls.slidepuzzle.model.TitledCardInfo
import com.helpmepls.slidepuzzle.vm.BoardOptionsVm
import `in`.srain.cube.views.GridViewWithHeaderAndFooter

class BoardOptionsFrm : Fragment() {
    companion object Companion {
        fun newInstance() = BoardOptionsFrm()
    }

    private val viewModel: BoardOptionsVm? by lazy {
        activity?.let {
            ViewModelProvider(it)[BoardOptionsVm::class.java]
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Removed UIUtils.setupEdgeToEdge1 - causing transparent status bar
        val view = inflater.inflate(
            /* resource = */ R.layout.frm_board_options,
            /* root = */ container,
            /* attachToRoot = */ false
        )
        // Removed UIUtils.setupEdgeToEdge2 - using theme's status bar color instead
        (activity as AppCompatActivity).setSupportActionBar(
            view.findViewById(R.id.tbBoardOptions)
        )
        return view
    }

    @SuppressLint("InflateParams")
    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val context = this.context
        val board = view?.findViewById<GridViewWithHeaderAndFooter>(R.id.gvImages)

        if (context != null && board != null) {
            // Add elegant staggered entrance animation for grid view
            board.alpha = 0f
            val staggeredAnimation = AnimationUtils.loadAnimation(context, R.anim.staggered_grid_item)

            board.postDelayed({
                board.alpha = 1f
                board.startAnimation(staggeredAnimation)
            }, 300)

            val layoutInflater = LayoutInflater.from(view?.context)
            board.addHeaderView(
                layoutInflater.inflate(
                    /* resource = */ R.layout.item_board_options_grid_header,
                    /* root = */ null
                )
            )

            board.adapter = ImageCardsAdt(
                parentContext = context,
                cards = BoardOptionsVm.Companion.PREDEFINED_IMAGES.map { (id, name) ->
                    TitledCardInfo(
                        image = BitmapFactory.decodeResource(
                            /* res = */ resources,
                            /* id = */ id
                        ),
                        title = name
                    )
                }.toTypedArray()
            )

            board.setOnItemClickListener { _, view, _, _ ->
                viewModel?.let {
                    // Lấy dữ liệu từ tag đã đặt
                    val cardInfo = view.getTag(R.id.tag_card_data) as TitledCardInfo
                    it.boardImage.value = cardInfo.image

                    GameAct.initialConfig = BoardActivityParams(
                        bitmap = it.boardImage.value!!,
                        size = it.boardSize.value!!
                    )
                    val intent = Intent(this.activity, GameAct::class.java)
                    startActivity(intent)
                    this.activity?.overridePendingTransition(R.anim.smooth_slide_in_right, R.anim.smooth_slide_out_left)
                }
            }
        }
    }
}
