package com.helpmepls.slidepuzzle.adt

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.helpmepls.slidepuzzle.R
import com.helpmepls.slidepuzzle.model.TitledCardInfo
import com.helpmepls.slidepuzzle.util.ScoreUtils
import com.helpmepls.slidepuzzle.vm.BoardOptionsVm

class ImageCardsAdt(
    private val parentContext: Context,
    private val cards: Array<TitledCardInfo>,
    private var prefs: SharedPreferences,
    private var boardW: Int = 4,
    private var boardH: Int = 4,
) : BaseAdapter() {

    // Task 30: vị trí card đang được chọn để hiển thị shimmer.
    private var selectedPosition: Int = -1
    // Cache bitmap placeholder của gallery slot — vẽ 1 lần, tái dùng.
    private var galleryBitmap: android.graphics.Bitmap? = null

    /** Đặt card được chọn (hiện shimmer) và redraw. */
    fun setSelectedPosition(pos: Int) {
        selectedPosition = pos
        notifyDataSetChanged()
    }

    /** Cập nhật board size + prefs rồi redraw — KHÔNG gọi setAdapter() → tránh crash GridViewWithHeaderAndFooter. */
    fun refreshScores(newPrefs: SharedPreferences, newBoardW: Int, newBoardH: Int) {
        prefs = newPrefs
        boardW = newBoardW
        boardH = newBoardH
        notifyDataSetChanged()
    }

    override fun getCount(): Int = cards.size
    override fun getItemId(position: Int): Long = position.toLong()
    override fun getItem(position: Int): Any = cards[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(parentContext).inflate(R.layout.frm_titled_image_card, parent, false)
            holder = ViewHolder(
                titleView = view.findViewById(R.id.title),
                imageView = view.findViewById(R.id.image),
                bestScore = view.findViewById(R.id.tvBestScore),
                shimmerView = view.findViewById(R.id.shimmerLine),
            )
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
            // Cancel shimmer từ binding trước khi recycle view này.
            holder.shimmerAnimator?.cancel()
            holder.shimmerAnimator = null
        }

        // Mặc định ẩn shimmer; sẽ bật lại nếu position == selectedPosition.
        holder.shimmerView.visibility = View.GONE

        val card = cards[position]

        if (card.isGallerySlot || card.imageResId == BoardOptionsVm.GALLERY_SLOT_RES_ID) {
            bindGallerySlot(holder, parent)
        } else {
            bindImageCard(holder, card, parent)
            // Task 30: shimmer scan line khi card được chọn.
            if (position == selectedPosition) {
                holder.shimmerView.visibility = View.VISIBLE
                val anim = ObjectAnimator.ofFloat(holder.shimmerView, "translationY", -300f, 300f).apply {
                    duration = 900
                    repeatCount = ValueAnimator.INFINITE
                    repeatMode = ValueAnimator.RESTART
                    start()
                }
                holder.shimmerAnimator = anim
            }
        }

        view.setTag(R.id.tag_card_data, card)
        return view
    }

    private fun bindImageCard(holder: ViewHolder, card: TitledCardInfo, parent: ViewGroup) {
        holder.imageView.scaleType = android.widget.ImageView.ScaleType.FIT_XY
        holder.imageView.setPadding(0, 0, 0, 0)
        holder.imageView.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        holder.titleView.text = card.title
        holder.titleView.visibility = if (card.title.isNullOrBlank()) View.GONE else View.VISIBLE

        Glide.with(holder.imageView.context)
            .load(card.imageResId)
            .override(parent.width.coerceAtLeast(360) / 2, parent.width.coerceAtLeast(360) / 2)
            .format(DecodeFormat.PREFER_ARGB_8888)
            .into(holder.imageView)

        androidx.core.view.ViewCompat.setTransitionName(holder.imageView, "hero_image_${card.imageResId}")

        // Best score badge
        val best = prefs.getInt(ScoreUtils.movesKey(card.imageResId, boardW, boardH), ScoreUtils.NO_BEST)
        if (best != ScoreUtils.NO_BEST) {
            holder.bestScore.text = "⭐ $best"
            holder.bestScore.visibility = View.VISIBLE
        } else {
            holder.bestScore.visibility = View.GONE
        }
    }

    private fun bindGallerySlot(holder: ViewHolder, parent: ViewGroup) {
        val sizePx = (parent.width.coerceAtLeast(400) / 2)
        if (galleryBitmap == null || galleryBitmap!!.width != sizePx) {
            galleryBitmap?.recycle()
            galleryBitmap = buildGalleryBitmap(sizePx)
        }
        holder.imageView.scaleType = android.widget.ImageView.ScaleType.FIT_XY
        holder.imageView.setPadding(0, 0, 0, 0)
        holder.imageView.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        holder.imageView.setImageBitmap(galleryBitmap)
        // Title ẩn — text "YOUR PHOTO" đã được vẽ bên trong bitmap để card cùng height với thumbnails
        holder.titleView.visibility = View.GONE
        holder.bestScore.visibility = View.GONE
        androidx.core.view.ViewCompat.setTransitionName(holder.imageView, "hero_image_gallery")
    }

    /** Vẽ full-bleed bitmap: background fill + camera icon ở center.
     *  FIT_XY trên SquareImageView → fill 100% card area, giống thumbnail. */
    private fun buildGalleryBitmap(sizePx: Int): android.graphics.Bitmap {
        val bmp = android.graphics.Bitmap.createBitmap(sizePx, sizePx, android.graphics.Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bmp)

        // Fill nền elevated navy
        canvas.drawColor(androidx.core.content.ContextCompat.getColor(parentContext, R.color.neon_bg_elevated))

        // Subtle radial glow ở center (accent cyan 10% alpha)
        val glowPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
        glowPaint.shader = android.graphics.RadialGradient(
            sizePx / 2f, sizePx / 2f, sizePx * 0.55f,
            0x1A38F9E4.toInt(), 0x0038F9E4.toInt(),
            android.graphics.Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, sizePx.toFloat(), sizePx.toFloat(), glowPaint)

        // Layout: icon + text căn giữa theo chiều dọc như 1 cụm
        val textPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            color = androidx.core.content.ContextCompat.getColor(parentContext, R.color.neon_text_primary)
            textSize = sizePx * 0.08f
            textAlign = android.graphics.Paint.Align.CENTER
            typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.BOLD)
            letterSpacing = 0.05f
        }
        val textHeight = textPaint.descent() - textPaint.ascent()
        val gap = sizePx * 0.04f
        val iconPx = (sizePx * 0.42f).toInt()
        val groupHeight = iconPx + gap + textHeight
        val groupTop = (sizePx - groupHeight) / 2f

        val drawable = androidx.core.content.ContextCompat.getDrawable(parentContext, R.drawable.ic_add_photo_neon)!!
        val iconLeft = (sizePx - iconPx) / 2
        val iconTop = groupTop.toInt()
        drawable.setBounds(iconLeft, iconTop, iconLeft + iconPx, iconTop + iconPx)
        drawable.draw(canvas)

        val textY = groupTop + iconPx + gap - textPaint.ascent()
        canvas.drawText("YOUR PHOTO", sizePx / 2f, textY, textPaint)

        return bmp
    }

    private class ViewHolder(
        val titleView: TextView,
        val imageView: ImageView,
        val bestScore: TextView,
        val shimmerView: View,
        var shimmerAnimator: ObjectAnimator? = null,
    )
}
