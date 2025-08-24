package com.helpmepls.slidepuzzle.vm

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.helpmepls.slidepuzzle.R
import com.helpmepls.slidepuzzle.model.BoardTitledSize
import com.helpmepls.slidepuzzle.model.TitledResourcePair

class BoardOptionsVm : ViewModel() {
    companion object Companion {
        val PREDEFINED_BOARD_SIZE = arrayOf(
            BoardTitledSize(width = 3, height = 3),
            BoardTitledSize(width = 4, height = 4),
            BoardTitledSize(width = 5, height = 5),
            BoardTitledSize(width = 6, height = 6),
            BoardTitledSize(width = 7, height = 7),
            BoardTitledSize(width = 8, height = 8)
        )

        val PREDEFINED_IMAGES: Array<TitledResourcePair> = arrayOf(
            TitledResourcePair(first = R.drawable.i1, second = ""),
            TitledResourcePair(first = R.drawable.i2, second = ""),
            TitledResourcePair(first = R.drawable.i3, second = ""),
            TitledResourcePair(first = R.drawable.i4, second = ""),
            TitledResourcePair(first = R.drawable.i5, second = ""),
            TitledResourcePair(first = R.drawable.i6, second = ""),
            TitledResourcePair(first = R.drawable.i7, second = ""),
            TitledResourcePair(first = R.drawable.i8, second = ""),
            TitledResourcePair(first = R.drawable.i9, second = ""),

            TitledResourcePair(first = R.drawable.a1, second = ""),
            TitledResourcePair(first = R.drawable.a2, second = ""),
            TitledResourcePair(first = R.drawable.a3, second = ""),
            TitledResourcePair(first = R.drawable.a4, second = ""),
            TitledResourcePair(first = R.drawable.a5, second = ""),
            TitledResourcePair(first = R.drawable.a6, second = ""),
            TitledResourcePair(first = R.drawable.a7, second = ""),
            TitledResourcePair(first = R.drawable.a8, second = ""),
            TitledResourcePair(first = R.drawable.a9, second = ""),

            TitledResourcePair(first = R.drawable.b1, second = ""),
            TitledResourcePair(first = R.drawable.b2, second = ""),
            TitledResourcePair(first = R.drawable.b3, second = ""),
            TitledResourcePair(first = R.drawable.b4, second = ""),
            TitledResourcePair(first = R.drawable.b5, second = ""),
            TitledResourcePair(first = R.drawable.b6, second = ""),
            TitledResourcePair(first = R.drawable.b7, second = ""),
            TitledResourcePair(first = R.drawable.b8, second = ""),
            TitledResourcePair(first = R.drawable.b9, second = ""),
        )
    }

    val boardSize = MutableLiveData<BoardTitledSize>()
    val boardImage = MutableLiveData<Bitmap>()
}
