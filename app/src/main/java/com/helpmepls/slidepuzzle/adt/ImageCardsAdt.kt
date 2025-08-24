package com.helpmepls.slidepuzzle.adt

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.helpmepls.slidepuzzle.R
import com.helpmepls.slidepuzzle.model.TitledCardInfo

class ImageCardsAdt(
    private val parentContext: Context,
    private val cards: Array<TitledCardInfo>,
) : BaseAdapter() {

    override fun getCount(): Int = cards.size
    override fun getItemId(position: Int): Long = position.toLong()
    override fun getItem(position: Int): Any = cards[position]

    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: ViewHolder
        val view: View

        if (convertView == null) {
            val vi = LayoutInflater.from(parentContext)
            view = vi.inflate(R.layout.frm_titled_image_card, parent, false)
            holder = ViewHolder(
                view.findViewById(R.id.title),
                view.findViewById(R.id.image)
            )
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        // ✅ Luôn cập nhật data mới
        val card = cards[position]
        holder.title.text = card.title
        holder.image.setImageBitmap(card.image)

        return view
    }

    private data class ViewHolder(
        val title: TextView,
        val image: ImageView,
    )
}
