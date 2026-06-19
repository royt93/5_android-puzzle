package com.helpmepls.slidepuzzle

import android.graphics.Bitmap
import android.util.Size
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.helpmepls.slidepuzzle.game.state.PuzzleGrid
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PuzzleGridInstrumentedTest {

    private fun bitmap(w: Int, h: Int): Bitmap =
        Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)

    @Test
    fun solvedGridHasOrderedIndicesAndSingleEmpty() {
        val grid = PuzzleGrid(sourceImage = bitmap(80, 80), size = Size(4, 4))
        grid.regenerate(newSize = Size(4, 4), shuffle = false)

        var expected = 0
        var empties = 0
        for (y in 0 until 4) {
            for (x in 0 until 4) {
                val p = grid.puzzles[y][x]
                if (p == null) empties++ else assertEquals(expected, p.index)
                expected++
            }
        }
        assertEquals(1, empties)
        assertTrue(grid.isSolved())
    }

    @Test
    fun regenerateSameImageAndSizeKeepsSameImageReference() {
        val img = bitmap(80, 80)
        val grid = PuzzleGrid(sourceImage = img, size = Size(4, 4))
        val before = grid.image

        // Reshuffle/reset cung board khong duoc doi anh nguon (khong cap phat lai).
        grid.regenerate(newSize = Size(4, 4), shuffle = true)

        assertSame(before, grid.image)
        assertSame(img, grid.image)
    }

    @Test
    fun shufflePreservesAllTileIndices() {
        val grid = PuzzleGrid(sourceImage = bitmap(90, 90), size = Size(3, 3))
        grid.shuffle(reset = false)

        val indices = mutableListOf<Int>()
        for (y in 0 until 3) {
            for (x in 0 until 3) {
                grid.puzzles[y][x]?.let { indices.add(it.index) }
            }
        }
        // 9 o, 1 o trong => con dung 8 manh 0..7, khong trung lap.
        assertEquals((0 until 8).toList(), indices.sorted())
    }
}
