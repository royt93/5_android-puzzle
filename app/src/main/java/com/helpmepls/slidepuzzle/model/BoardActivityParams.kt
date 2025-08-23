package com.helpmepls.slidepuzzle.model

import android.graphics.Bitmap
import androidx.annotation.Keep

@Keep
data class BoardActivityParams(
    val bitmap: Bitmap,
    val size: BoardTitledSize,
)
