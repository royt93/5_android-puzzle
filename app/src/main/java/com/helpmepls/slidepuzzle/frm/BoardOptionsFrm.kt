package com.helpmepls.slidepuzzle.frm

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.helpmepls.slidepuzzle.R
import com.helpmepls.slidepuzzle.act.GameAct
import com.helpmepls.slidepuzzle.adt.ImageCardsAdt
import com.helpmepls.slidepuzzle.model.TitledCardInfo
import com.helpmepls.slidepuzzle.util.ScoreUtils
import com.helpmepls.slidepuzzle.vm.BoardOptionsVm
import `in`.srain.cube.views.GridViewWithHeaderAndFooter
import java.io.File

class BoardOptionsFrm : Fragment() {
    companion object {
        fun newInstance() = BoardOptionsFrm()
    }

    private val viewModel: BoardOptionsVm? by lazy {
        activity?.let { ViewModelProvider(it)[BoardOptionsVm::class.java] }
    }

    private var gridView: GridViewWithHeaderAndFooter? = null
    // Giữ reference trực tiếp vì GridViewWithHeaderAndFooter.getAdapter() trả về
    // wrapper HeaderViewGridAdapter chứ không phải ImageCardsAdt.
    private var imageAdapter: ImageCardsAdt? = null
    // Game mode state (Task 26 + Task 23).
    private var isMoveChallengeMode = false
    private var isTimeAttackMode = false
    private var timeLimitSeconds = 180  // default: Medium 3min
    private var gridHeader: View? = null

    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val uri = result.data?.data ?: return@registerForActivityResult
            launchGameWithGalleryUri(uri)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.frm_board_options, container, false)
        (activity as AppCompatActivity).setSupportActionBar(view.findViewById(R.id.tbBoardOptions))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = requireContext()
        gridView = view.findViewById(R.id.gvImages)

        gridView?.let { board ->
            board.alpha = 0f
            board.postDelayed({
                board.alpha = 1f
                board.startAnimation(AnimationUtils.loadAnimation(context, R.anim.staggered_grid_item))
            }, 300)

            val header = LayoutInflater.from(context).inflate(R.layout.item_board_options_grid_header, null)
            gridHeader = header
            board.addHeaderView(header)
            setupModeChips(header)

            buildAdapter()
            // boardSize observe bị bỏ vì setAdapter() 2 lần trên GridViewWithHeaderAndFooter gây crash.
            // Thay bằng refreshScores() trong onResume() — không tạo adapter mới.

            board.setOnItemClickListener { _, itemView, position, _ ->
                val cardInfo = itemView.getTag(R.id.tag_card_data) as? TitledCardInfo ?: return@setOnItemClickListener

                if (cardInfo.isGallerySlot || cardInfo.imageResId == BoardOptionsVm.GALLERY_SLOT_RES_ID) {
                    openGalleryPicker()
                    return@setOnItemClickListener
                }

                // Task 30: bật shimmer trên card được chọn — visible trong suốt transition animation.
                imageAdapter?.setSelectedPosition(position)

                val vm = viewModel ?: return@setOnItemClickListener
                val boardSize = vm.boardSize.value ?: BoardOptionsVm.PREDEFINED_BOARD_SIZE[1]
                val transitionName = "hero_image_${cardInfo.imageResId}"
                val intent = Intent(requireActivity(), GameAct::class.java).apply {
                    putExtra(GameAct.EXTRA_IMAGE_RES_ID, cardInfo.imageResId)
                    putExtra(GameAct.EXTRA_BOARD_WIDTH, boardSize.width)
                    putExtra(GameAct.EXTRA_BOARD_HEIGHT, boardSize.height)
                    putExtra("TRANSITION_NAME", transitionName)
                    if (isMoveChallengeMode) {
                        putExtra(GameAct.EXTRA_MOVE_BUDGET, calcMoveBudget(boardSize.width, boardSize.height))
                    }
                    if (isTimeAttackMode) {
                        putExtra(GameAct.EXTRA_TIME_LIMIT_SECONDS, timeLimitSeconds)
                    }
                }
                val options = androidx.core.app.ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(),
                    androidx.core.util.Pair(itemView.findViewById(R.id.image), transitionName)
                )
                startActivity(intent, options.toBundle())
            }
        }

        view.findViewById<View>(R.id.btnRate)?.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}")))
            } catch (_: Exception) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")))
            }
        }
        view.findViewById<View>(R.id.btnMore)?.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:SAIGON PHANTOM LABS")))
            } catch (_: Exception) {
                try { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}"))) }
                catch (_: Exception) {}
            }
        }
        view.findViewById<View>(R.id.btnShare)?.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                putExtra(Intent.EXTRA_TEXT, "Check out this puzzle game! https://play.google.com/store/apps/details?id=${context.packageName}")
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_app)))
        }
        view.findViewById<View>(R.id.btnAbout)?.setOnClickListener { showAboutDialog() }
        view.findViewById<View>(R.id.btnSettings)?.setOnClickListener {
            startActivity(Intent(requireActivity(), com.helpmepls.slidepuzzle.act.SettingsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        // Làm mới best-score badges khi quay lại từ GameAct — dùng refreshScores thay vì setAdapter.
        val ctx = context ?: return
        val size = viewModel?.boardSize?.value ?: BoardOptionsVm.PREDEFINED_BOARD_SIZE[1]
        val prefs = ctx.getSharedPreferences(ScoreUtils.PREFS_NAME, android.content.Context.MODE_PRIVATE)
        // Dùng imageAdapter trực tiếp (gridView.adapter trả về wrapper, không phải ImageCardsAdt).
        imageAdapter?.refreshScores(prefs, size.width, size.height)
    }

    private fun buildAdapter() {
        val ctx = context ?: return
        val board = gridView ?: return
        val size = viewModel?.boardSize?.value ?: BoardOptionsVm.PREDEFINED_BOARD_SIZE[1]
        val prefs = ctx.getSharedPreferences(ScoreUtils.PREFS_NAME, android.content.Context.MODE_PRIVATE)

        val cards = BoardOptionsVm.PREDEFINED_IMAGES.map { (id, name) ->
            TitledCardInfo(
                imageResId = id,
                title = name,
                isGallerySlot = (id == BoardOptionsVm.GALLERY_SLOT_RES_ID),
            )
        }.toTypedArray()

        imageAdapter = ImageCardsAdt(
            parentContext = ctx,
            cards = cards,
            prefs = prefs,
            boardW = size.width,
            boardH = size.height,
        )
        board.adapter = imageAdapter
    }

    private fun openGalleryPicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
        galleryLauncher.launch(intent)
    }

    private fun launchGameWithGalleryUri(uri: Uri) {
        val ctx = context ?: return
        val vm = viewModel ?: return
        val boardSize = vm.boardSize.value ?: BoardOptionsVm.PREDEFINED_BOARD_SIZE[1]

        // Decode + crop sang square → lưu vào cache file.
        val bitmap = decodeCroppedSquare(uri, 512) ?: return
        val cacheFile = File(ctx.cacheDir, "custom_puzzle.jpg")
        cacheFile.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) }
        bitmap.recycle()

        val intent = Intent(requireActivity(), GameAct::class.java).apply {
            putExtra(GameAct.EXTRA_IMAGE_RES_ID, BoardOptionsVm.GALLERY_SLOT_RES_ID)
            putExtra(GameAct.EXTRA_BOARD_WIDTH, boardSize.width)
            putExtra(GameAct.EXTRA_BOARD_HEIGHT, boardSize.height)
            putExtra(GameAct.EXTRA_CUSTOM_IMAGE_PATH, cacheFile.absolutePath)
            if (isMoveChallengeMode) {
                putExtra(GameAct.EXTRA_MOVE_BUDGET, calcMoveBudget(boardSize.width, boardSize.height))
            }
            if (isTimeAttackMode) {
                putExtra(GameAct.EXTRA_TIME_LIMIT_SECONDS, timeLimitSeconds)
            }
        }
        startActivity(intent)
    }

    private fun decodeCroppedSquare(uri: Uri, targetSize: Int): Bitmap? {
        val ctx = context ?: return null
        // Pass 1: kích thước
        val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        ctx.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opts) }
        if (opts.outWidth <= 0) return null

        // Pass 2: decode với inSampleSize
        val sample = calculateSample(opts.outWidth, opts.outHeight, targetSize)
        val fullOpts = BitmapFactory.Options().apply { inSampleSize = sample }
        val bmp = ctx.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, fullOpts)
        } ?: return null

        // Center-crop to square
        val side = minOf(bmp.width, bmp.height)
        val x = (bmp.width - side) / 2
        val y = (bmp.height - side) / 2
        val cropped = Bitmap.createBitmap(bmp, x, y, side, side)
        if (cropped !== bmp) bmp.recycle()
        return cropped
    }

    private fun calculateSample(srcW: Int, srcH: Int, target: Int): Int {
        var s = 1
        while (srcW / (s * 2) >= target && srcH / (s * 2) >= target) s *= 2
        return s
    }

    private fun setupModeChips(header: View) {
        val chipGroupMode = header.findViewById<com.google.android.material.chip.ChipGroup>(R.id.chipGroupMode) ?: return
        val chipGroupDiff = header.findViewById<com.google.android.material.chip.ChipGroup>(R.id.chipGroupDifficulty)
        chipGroupMode.setOnCheckedStateChangeListener { _, checkedIds ->
            isMoveChallengeMode = checkedIds.contains(R.id.chipMoveChallenge)
            isTimeAttackMode = checkedIds.contains(R.id.chipTimeAttack)
            chipGroupDiff?.visibility = if (isTimeAttackMode) android.view.View.VISIBLE else android.view.View.GONE
        }
        chipGroupDiff?.setOnCheckedStateChangeListener { _, checkedIds ->
            timeLimitSeconds = when {
                checkedIds.contains(R.id.chipEasy)   -> 300
                checkedIds.contains(R.id.chipHard)   -> 90
                else                                  -> 180
            }
        }
    }

    private fun calcMoveBudget(boardW: Int, boardH: Int): Int =
        (100 * 1.5).toInt().coerceAtLeast(boardW * boardH * 2)

    private fun showAboutDialog() {
        val ctx = context ?: return
        val appName = getString(R.string.app_name)
        val message = buildString {
            append(appName); append("\nVersion "); append(com.helpmepls.slidepuzzle.BuildConfig.VERSION_NAME)
            append("\n\n"); append(getString(R.string.about_developer))
            append("\n\n"); append(getString(R.string.about_licenses))
        }
        com.helpmepls.slidepuzzle.DialogUtils.showGameDialog(
            context = ctx,
            title = getString(R.string.about_title, appName),
            message = message,
            yesText = getString(R.string.about_github),
            noText = getString(R.string.about_close),
            onYes = {
                try { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.about_github_url)))) }
                catch (_: Exception) {}
            }
        )
    }
}
