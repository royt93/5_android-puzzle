package com.helpmepls.slidepuzzle.frm

import android.content.Intent
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = requireContext()
        val board = view.findViewById<GridViewWithHeaderAndFooter>(R.id.gvImages)

        if (board != null) {
            // Add elegant staggered entrance animation for grid view
            board.alpha = 0f
            val staggeredAnimation = AnimationUtils.loadAnimation(context, R.anim.staggered_grid_item)

            board.postDelayed({
                board.alpha = 1f
                board.startAnimation(staggeredAnimation)
            }, 300)

            val layoutInflater = LayoutInflater.from(context)
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
                        imageResId = id,
                        title = name
                    )
                }.toTypedArray()
            )

            board.setOnItemClickListener { _, itemView, _, _ ->
                viewModel?.let {
                    // Lấy dữ liệu từ tag đã đặt
                    val cardInfo = itemView.getTag(R.id.tag_card_data) as TitledCardInfo
                    val boardSize = it.boardSize.value ?: BoardOptionsVm.PREDEFINED_BOARD_SIZE[1]
                    
                    val transitionName = "hero_image_${cardInfo.imageResId}"
                    val intent = Intent(requireActivity(), GameAct::class.java).apply {
                        putExtra(GameAct.EXTRA_IMAGE_RES_ID, cardInfo.imageResId)
                        putExtra(GameAct.EXTRA_BOARD_WIDTH, boardSize.width)
                        putExtra(GameAct.EXTRA_BOARD_HEIGHT, boardSize.height)
                        putExtra("TRANSITION_NAME", transitionName)
                    }
                    
                    val options = androidx.core.app.ActivityOptionsCompat.makeSceneTransitionAnimation(
                        requireActivity(),
                        androidx.core.util.Pair(itemView.findViewById(R.id.image), transitionName)
                    )
                    
                    startActivity(intent, options.toBundle())
                    // Removed overridePendingTransition to allow ActivityOptions transition to work
                }
            }
        }
        
        // App Features Logic
        view.findViewById<View>(R.id.btnRate)?.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse("market://details?id=${context.packageName}")))
            } catch (e: Exception) {
                startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")))
            }
        }

        view.findViewById<View>(R.id.btnMore)?.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse("market://search?q=pub:SAIGON PHANTOM LABS")))
            } catch (e: Exception) {
                try {
                startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse("market://details?id=${context.packageName}")))
                } catch (e2: Exception) {}
            }
        }

        view.findViewById<View>(R.id.btnShare)?.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            val shareMessage = "Check out this puzzle game! https://play.google.com/store/apps/details?id=${context.packageName}"
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_app)))
        }

        view.findViewById<View>(R.id.btnAbout)?.setOnClickListener {
            showAboutDialog()
        }
    }

    /** Dialog About: ten app + version + developer + license OSS; nut GitHub mo repo. */
    private fun showAboutDialog() {
        val ctx = context ?: return
        val appName = getString(R.string.app_name)
        val message = buildString {
            append(appName)
            append("\nVersion ")
            append(com.helpmepls.slidepuzzle.BuildConfig.VERSION_NAME)
            append("\n\n")
            append(getString(R.string.about_developer))
            append("\n\n")
            append(getString(R.string.about_licenses))
        }
        com.helpmepls.slidepuzzle.DialogUtils.showGameDialog(
            context = ctx,
            title = getString(R.string.about_title, appName),
            message = message,
            yesText = getString(R.string.about_github),
            noText = getString(R.string.about_close),
            onYes = {
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            android.net.Uri.parse(getString(R.string.about_github_url))
                        )
                    )
                } catch (_: Exception) {
                    // Khong co trinh duyet -> bo qua, khong crash.
                }
            }
        )
    }
}
