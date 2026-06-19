package com.helpmepls.slidepuzzle.game.state

import androidx.annotation.Keep

// Chi giu index (vi tri dung cua manh ghep). Pixel duoc ve truc tiep tu anh nguon
// qua src Rect khi render, khong con cat thanh bitmap con => khong cap phat bitmap thua.
@Keep
data class PuzzleDescriptor(val index: Int)
