package com.helpmepls.slidepuzzle.model

import android.graphics.Bitmap
import androidx.annotation.Keep

@Keep
data class TitledCardInfo(
    val image: Bitmap,
    val title: String,
)
