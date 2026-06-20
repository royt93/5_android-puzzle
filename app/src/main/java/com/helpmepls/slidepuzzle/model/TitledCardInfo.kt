package com.helpmepls.slidepuzzle.model

import androidx.annotation.DrawableRes
import androidx.annotation.Keep

@Keep
data class TitledCardInfo(
    @DrawableRes val imageResId: Int,
    val title: String,
    val isGallerySlot: Boolean = false,
)
