package com.helpmepls.slidepuzzle.game.state

import android.graphics.Bitmap
import androidx.annotation.Keep

@Keep
data class PuzzleDescriptor(val index: Int, val bitmap: Bitmap)
