package com.helpmepls.slidepuzzle.util

/** Logic high score thuan Kotlin (unit-test duoc tren JVM, khong import Android). */
object ScoreUtils {
    const val NO_BEST: Int = Int.MAX_VALUE
    const val NO_BEST_TIME: Int = Int.MAX_VALUE

    // SharedPreferences name dung chung.
    const val PREFS_NAME = "puzzle_prefs"

    fun isNewBest(moves: Int, previousBest: Int): Boolean = moves < previousBest
    fun isNewBestTime(seconds: Int, previousBestTime: Int): Boolean = seconds < previousBestTime

    // Per-puzzle keys: tach biet theo (imageResId × boardSize) de tranh xung dot giua anh khac nhau.
    fun movesKey(imageResId: Int, w: Int, h: Int): String = "best_${imageResId}_${w}x${h}"
    fun timeKey(imageResId: Int, w: Int, h: Int): String = "besttime_${imageResId}_${w}x${h}"

    // Backwards-compat keys (board-size only) — giu de khong mat du lieu cu.
    fun legacyMovesKey(w: Int, h: Int): String = "best_${w}x${h}"
    fun legacyTimeKey(w: Int, h: Int): String = "best_time_${w}x${h}"

    fun starsFor(moves: Int, boardArea: Int): Int = when {
        moves <= boardArea * 3 -> 3
        moves <= boardArea * 6 -> 2
        else -> 1
    }
}
