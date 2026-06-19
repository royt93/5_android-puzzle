package com.helpmepls.slidepuzzle.util

/** Logic high score thuan Kotlin (unit-test duoc tren JVM). */
object ScoreUtils {
    /** Khong co record truoc do => previousBest = NO_BEST. */
    const val NO_BEST: Int = Int.MAX_VALUE

    /**
     * True khi [moves] lap ky luc moi. Lan giai dau (previousBest = NO_BEST) luon la new best.
     * Bang diem cu => khong tinh la moi.
     */
    fun isNewBest(moves: Int, previousBest: Int): Boolean = moves < previousBest
}
