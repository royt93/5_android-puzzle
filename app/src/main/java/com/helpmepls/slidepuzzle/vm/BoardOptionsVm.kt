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
            TitledResourcePair(first = R.drawable.cat, second = "kotek"),
            TitledResourcePair(first = R.drawable.doge, second = "piesek"),
            TitledResourcePair(first = R.drawable.spiderman, second = "spajdemen"),
            TitledResourcePair(first = R.drawable.spiderman_office, second = "smuteczek"),
            TitledResourcePair(first = R.drawable.yeti, second = "yeti"),
            TitledResourcePair(first = R.drawable.pigeon, second = "szczur"),
            TitledResourcePair(first = R.drawable.spiderman_ok, second = "spajdermen okej"),
            TitledResourcePair(first = R.drawable.pepe, second = "żabka"),
            TitledResourcePair(first = R.drawable.dolan, second = "kaczka"),
            TitledResourcePair(first = R.drawable.original_pepe, second = "pepe"),
            TitledResourcePair(first = R.drawable.alien, second = "alien"),
            TitledResourcePair(first = R.drawable.wat, second = "wat")
        )
    }

    val boardSize = MutableLiveData<BoardTitledSize>()
    val boardImage = MutableLiveData<Bitmap>()
}
