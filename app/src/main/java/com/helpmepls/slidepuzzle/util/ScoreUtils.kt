package com.helpmepls.slidepuzzle.util

/** Logic high score thuan Kotlin (unit-test duoc tren JVM). */
object ScoreUtils {
    /** Khong co record truoc do => previousBest = NO_BEST. */
    const val NO_BEST: Int = Int.MAX_VALUE

    /** Khong co record time truoc do => previousBestTime = NO_BEST_TIME. */
    const val NO_BEST_TIME: Int = Int.MAX_VALUE

    /**
     * True khi [moves] lap ky luc moi. Lan giai dau (previousBest = NO_BEST) luon la new best.
     * Bang diem cu => khong tinh la moi.
     */
    fun isNewBest(moves: Int, previousBest: Int): Boolean = moves < previousBest

    /**
     * True khi [seconds] lap ky luc thoi gian moi (it giay hon = tot hon). Lan giai dau
     * (previousBestTime = NO_BEST_TIME) luon la new best. Bang thoi gian cu => khong tinh la moi.
     */
    fun isNewBestTime(seconds: Int, previousBestTime: Int): Boolean = seconds < previousBestTime
}
