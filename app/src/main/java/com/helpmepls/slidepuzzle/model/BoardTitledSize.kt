package com.helpmepls.slidepuzzle.model

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class BoardTitledSize(
    val width: Int,
    val height: Int,
) : Serializable {
    override fun toString(): String {
        return "$width x $height"
    }
}
