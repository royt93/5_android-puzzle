package com.helpmepls.slidepuzzle

import android.graphics.Bitmap
import android.graphics.Point
import android.util.Size
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.helpmepls.slidepuzzle.game.state.PuzzleGrid
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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

    @Test
    fun moveSlideIsReversible_underpinsUndo() {
        // Day la co che undo (A11): di chuyen 1 tile roi moveSlide chinh vi tri moi
        // se day tile ve cho cu -> ve trang thai solved.
        val grid = PuzzleGrid(sourceImage = bitmap(80, 80), size = Size(4, 4))
        grid.regenerate(newSize = Size(4, 4), shuffle = false)
        assertTrue(grid.isSolved())

        // O trong o goc duoi-phai (3,3); tile (2,3) co the truot sang phai.
        val newPos = grid.moveSlide(Point(2, 3))
        assertNotNull("nuoc di hop le phai tra ve vi tri moi", newPos)
        assertTrue("sau khi di chuyen, khong con solved", !grid.isSolved())

        // Undo: moveSlide tai vi tri tile vua toi -> hoan tac.
        val back = grid.moveSlide(newPos!!)
        assertNotNull(back)
        assertTrue("sau undo phai tro lai solved", grid.isSolved())
    }
}
