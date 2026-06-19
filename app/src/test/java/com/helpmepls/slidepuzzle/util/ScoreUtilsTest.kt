package com.helpmepls.slidepuzzle.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Unit test (JVM) cho logic new-best, gom ca ca lan giai dau tien (A6). */
class ScoreUtilsTest {

    @Test
    fun firstSolveIsAlwaysNewBest() {
        assertTrue(ScoreUtils.isNewBest(moves = 25, previousBest = ScoreUtils.NO_BEST))
    }

    @Test
    fun fewerMovesThanRecordIsNewBest() {
        assertTrue(ScoreUtils.isNewBest(moves = 18, previousBest = 20))
    }

    @Test
    fun moreMovesThanRecordIsNotNewBest() {
        assertFalse(ScoreUtils.isNewBest(moves = 30, previousBest = 20))
    }

    @Test
    fun equalToRecordIsNotNewBest() {
        assertFalse(ScoreUtils.isNewBest(moves = 20, previousBest = 20))
    }

    @Test
    fun firstSolveIsAlwaysNewBestTime() {
        assertTrue(ScoreUtils.isNewBestTime(seconds = 90, previousBestTime = ScoreUtils.NO_BEST_TIME))
    }

    @Test
    fun fasterThanRecordIsNewBestTime() {
        assertTrue(ScoreUtils.isNewBestTime(seconds = 70, previousBestTime = 85))
    }

    @Test
    fun slowerThanRecordIsNotNewBestTime() {
        assertFalse(ScoreUtils.isNewBestTime(seconds = 120, previousBestTime = 85))
    }

    @Test
    fun equalTimeIsNotNewBestTime() {
        assertFalse(ScoreUtils.isNewBestTime(seconds = 85, previousBestTime = 85))
    }
}
