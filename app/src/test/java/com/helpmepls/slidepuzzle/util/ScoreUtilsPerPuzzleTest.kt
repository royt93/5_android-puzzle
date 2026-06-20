package com.helpmepls.slidepuzzle.util

import org.junit.Assert.*
import org.junit.Test

/** Unit test cho per-puzzle key logic — thuần JVM, không cần Android. */
class ScoreUtilsPerPuzzleTest {

    @Test
    fun keysDifferByImage() {
        assertNotEquals(
            ScoreUtils.movesKey(imageResId = 1, w = 4, h = 4),
            ScoreUtils.movesKey(imageResId = 2, w = 4, h = 4),
        )
    }

    @Test
    fun keysDifferByBoardSize() {
        assertNotEquals(
            ScoreUtils.movesKey(imageResId = 1, w = 4, h = 4),
            ScoreUtils.movesKey(imageResId = 1, w = 3, h = 3),
        )
    }

    @Test
    fun movesAndTimeKeysDifferForSamePuzzle() {
        assertNotEquals(
            ScoreUtils.movesKey(imageResId = 1, w = 4, h = 4),
            ScoreUtils.timeKey(imageResId = 1, w = 4, h = 4),
        )
    }

    @Test
    fun samePuzzleSameKeyEveryTime() {
        val k1 = ScoreUtils.movesKey(100, 4, 4)
        val k2 = ScoreUtils.movesKey(100, 4, 4)
        assertEquals(k1, k2)
    }

    @Test
    fun gallerySlotResIdZeroHasValidKey() {
        // resId=0 (gallery slot) không gây crash hay key rỗng.
        val key = ScoreUtils.movesKey(0, 4, 4)
        assertTrue("key gallery phải không rỗng", key.isNotEmpty())
    }

    @Test
    fun legacyKeysDifferFromPerPuzzleKeys() {
        // Đảm bảo migration không overlap.
        assertNotEquals(
            ScoreUtils.legacyMovesKey(4, 4),
            ScoreUtils.movesKey(1, 4, 4),
        )
    }
}
