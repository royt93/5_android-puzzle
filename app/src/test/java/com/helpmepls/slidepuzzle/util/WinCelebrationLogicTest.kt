package com.helpmepls.slidepuzzle.util

import org.junit.Assert.*
import org.junit.Test

/** Unit test cho star-rating formula và best-badge conditions. */
class WinCelebrationLogicTest {

    // --- Star rating ---

    @Test
    fun threeStarsForFewerThanBoardAreaTimes3() {
        val boardArea = 16  // 4×4
        assertEquals(3, ScoreUtils.starsFor(moves = 30, boardArea = boardArea))   // 30 ≤ 48
    }

    @Test
    fun threeStarsExactlyAtBoundary() {
        assertEquals(3, ScoreUtils.starsFor(moves = 48, boardArea = 16))  // 48 = 16*3
    }

    @Test
    fun twoStarsAboveThreeStar() {
        assertEquals(2, ScoreUtils.starsFor(moves = 49, boardArea = 16))  // 49 > 48, ≤ 96
    }

    @Test
    fun twoStarsExactlyAtBoundary() {
        assertEquals(2, ScoreUtils.starsFor(moves = 96, boardArea = 16))  // 96 = 16*6
    }

    @Test
    fun oneStarAboveTwoStar() {
        assertEquals(1, ScoreUtils.starsFor(moves = 97, boardArea = 16))  // 97 > 96
    }

    @Test
    fun threeStarsForSmallBoard() {
        val boardArea = 9  // 3×3
        assertEquals(3, ScoreUtils.starsFor(moves = 15, boardArea = boardArea))  // 15 ≤ 27
    }

    // --- Best badge logic ---

    @Test
    fun newBestMovesWhenFewerThanPrevious() {
        assertTrue(ScoreUtils.isNewBest(moves = 18, previousBest = 20))
    }

    @Test
    fun notNewBestWhenEqual() {
        assertFalse(ScoreUtils.isNewBest(moves = 20, previousBest = 20))
    }

    @Test
    fun newBestTimeWhenFaster() {
        assertTrue(ScoreUtils.isNewBestTime(seconds = 60, previousBestTime = 90))
    }

    @Test
    fun notNewBestTimeWhenSlower() {
        assertFalse(ScoreUtils.isNewBestTime(seconds = 120, previousBestTime = 90))
    }

    @Test
    fun firstSolveAlwaysNewBest() {
        assertTrue(ScoreUtils.isNewBest(99, ScoreUtils.NO_BEST))
        assertTrue(ScoreUtils.isNewBestTime(99, ScoreUtils.NO_BEST_TIME))
    }
}
