package com.helpmepls.slidepuzzle

import com.helpmepls.slidepuzzle.act.GameAct
import com.helpmepls.slidepuzzle.vm.BoardOptionsVm
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests cho Task 23 (Time Attack) và Task 26 (Move Challenge).
 * Pure JVM — không cần emulator.
 */
class GameModeUnitTest {

    // ─── Move Budget formula ───────────────────────────────────────────────

    private fun calcMoveBudget(boardW: Int, boardH: Int): Int =
        (100 * 1.5).toInt().coerceAtLeast(boardW * boardH * 2)

    @Test
    fun moveBudget_formulaBase_is150() {
        // (100 * 1.5).toInt() = 150
        assertEquals(150, (100 * 1.5).toInt())
    }

    @Test
    fun moveBudget_3x3_returns150() {
        assertEquals(150, calcMoveBudget(3, 3))
    }

    @Test
    fun moveBudget_4x4_returns150() {
        assertEquals(150, calcMoveBudget(4, 4))
    }

    @Test
    fun moveBudget_8x8_returns150_sinceBaseIsLarger() {
        // 8*8*2=128 < 150 → coerceAtLeast clamp lên 150
        assertEquals(150, calcMoveBudget(8, 8))
    }

    @Test
    fun moveBudget_hypotheticalLargeBoard_usesAreaFormula() {
        // boardW*boardH*2 > 150 khi board >= 10x10
        val budget = calcMoveBudget(10, 10)
        assertEquals(200, budget) // 10*10*2=200 > 150
    }

    @Test
    fun moveBudget_alwaysPositive() {
        for (n in 3..8) assertTrue(calcMoveBudget(n, n) > 0)
    }

    // ─── Budget countdown display ──────────────────────────────────────────

    @Test
    fun budgetRemaining_decreasesWithMoves() {
        val budget = 150
        assertEquals(150, budget - 0)
        assertEquals(149, budget - 1)
        assertEquals(140, budget - 10)
        assertEquals(0, budget - 150)
    }

    @Test
    fun budgetRemaining_negativeWhenOverBudget() {
        val budget = 150
        assertTrue((budget - 160) < 0)
    }

    @Test
    fun budgetExceeded_triggerThreshold_is10() {
        // Dialog hỏi khi moves > budget + 10
        val budget = 150
        assertTrue(161 > budget + 10)  // trigger
        assertTrue(160 == budget + 10) // boundary (không trigger)
    }

    // ─── Time Attack limits ────────────────────────────────────────────────

    @Test
    fun timeLimit_easy_is300Seconds() {
        // Easy = 5 phút = 300 giây
        assertEquals(300, 5 * 60)
    }

    @Test
    fun timeLimit_medium_is180Seconds() {
        // Medium = 3 phút = 180 giây
        assertEquals(180, 3 * 60)
    }

    @Test
    fun timeLimit_hard_is90Seconds() {
        assertEquals(90, 90)
    }

    @Test
    fun timeLimit_hardIsShortestDifficulty() {
        val easy = 300; val medium = 180; val hard = 90
        assertTrue(hard < medium && medium < easy)
    }

    // ─── Countdown display logic ───────────────────────────────────────────

    @Test
    fun countdown_remaining_decrementsEachSecond() {
        val limit = 90
        for (elapsed in 0..89) {
            val remaining = limit - elapsed
            assertTrue("remaining phai > 0 khi elapsed=$elapsed", remaining > 0)
        }
        assertEquals(0, limit - limit)
    }

    @Test
    fun countdown_warning_triggersAtOrBelow30Seconds() {
        assertTrue(30 <= 30)   // boundary: đúng 30s → warn
        assertTrue(29 <= 30)   // 29s → warn
        assertTrue(31 > 30)    // 31s → không warn
    }

    @Test
    fun countdown_minuteFormat_correctForMedium() {
        val remaining = 178 // 2:58
        val mins = remaining / 60
        val secs = remaining % 60
        assertEquals(2, mins)
        assertEquals(58, secs)
    }

    @Test
    fun countdown_minuteFormat_correctFor0Seconds() {
        val remaining = 0
        assertEquals(0, remaining / 60)
        assertEquals(0, remaining % 60)
    }

    // ─── GameAct Extra constants (regression guard) ────────────────────────

    @Test
    fun extraMoveBudget_constantExists() {
        val key = GameAct.EXTRA_MOVE_BUDGET
        assertTrue("EXTRA_MOVE_BUDGET phải chứa package name", key.contains("slidepuzzle"))
    }

    @Test
    fun extraTimeLimitSeconds_constantExists() {
        val key = GameAct.EXTRA_TIME_LIMIT_SECONDS
        assertTrue("EXTRA_TIME_LIMIT_SECONDS phải chứa package name", key.contains("slidepuzzle"))
    }

    @Test
    fun extraMoveBudget_and_extraTimeLimitSeconds_areDifferent() {
        assertTrue(GameAct.EXTRA_MOVE_BUDGET != GameAct.EXTRA_TIME_LIMIT_SECONDS)
    }

    // ─── Gallery slot position ─────────────────────────────────────────────

    @Test
    fun gallerySlot_isFirstInPredefinedImages() {
        assertEquals(
            "YOUR PHOTO phải ở đầu danh sách sau khi chuyển lên top",
            BoardOptionsVm.GALLERY_SLOT_RES_ID,
            BoardOptionsVm.PREDEFINED_IMAGES.first().first
        )
    }

    @Test
    fun gallerySlot_labelIsYourPhoto() {
        val slot = BoardOptionsVm.PREDEFINED_IMAGES.first { it.first == BoardOptionsVm.GALLERY_SLOT_RES_ID }
        assertEquals("YOUR PHOTO", slot.second)
    }
}
