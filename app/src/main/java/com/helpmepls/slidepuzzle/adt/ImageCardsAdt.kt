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
    override fun getCount(): Int {
        return cards.size
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getItem(position: Int): Any {
        return cards[position]
    }

    @SuppressLint("InflateParams")
    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup,
    ): View? {
        var newView: View? = convertView
        if (newView == null) {
            val vi = parentContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            newView = vi.inflate(R.layout.frm_titled_image_card, null)
            newView.tag = cards[position]
        }

        val tag: TitledCardInfo = newView?.tag as TitledCardInfo
        newView.findViewById<TextView>(R.id.title).text = tag.title
        newView.findViewById<ImageView>(R.id.image).setImageBitmap(tag.image)

        return newView
    }
}
