package com.helpmepls.slidepuzzle.adt

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.helpmepls.slidepuzzle.R
import com.helpmepls.slidepuzzle.model.TitledCardInfo

class ImageCardsAdt(
    private val parentContext: Context,
    private val cards: Array<TitledCardInfo>,
) : BaseAdapter() {
    override fun getCount(): Int = cards.size
    override fun getItemId(position: Int): Long = position.toLong()
    override fun getItem(position: Int): Any = cards[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            val vi = LayoutInflater.from(parentContext)
            view = vi.inflate(R.layout.frm_titled_image_card, parent, false)
            holder = ViewHolder(
                titleView = view.findViewById(R.id.title),
                imageView = view.findViewById(R.id.image)
            )
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val card = cards[position]
        holder.titleView.text = card.title
//        holder.imageView.setImageBitmap(card.image)
        Glide.with(holder.imageView.context)
            .load(card.image)
            .override(SIZE_ORIGINAL, SIZE_ORIGINAL)
            .format(DecodeFormat.PREFER_ARGB_8888)
            .into(holder.imageView)
        
        // Hero Animation: Set unique transition name
        androidx.core.view.ViewCompat.setTransitionName(holder.imageView, "hero_image_${card.title}")

        // Lưu dữ liệu card vào view để sử dụng trong click listener
        view.setTag(R.id.tag_card_data, card)

        return view
    }

    // Sử dụng data class cho ViewHolder
    private data class ViewHolder(
        val titleView: TextView,
        val imageView: ImageView,
    )
}
