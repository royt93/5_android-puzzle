package com.helpmepls.slidepuzzle.game.state

import com.helpmepls.slidepuzzle.model.BoardTitledSize
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Random

class GameStateTest {
    @Test
    fun oppositeDirectionsCancelOut() {
        assertEquals(0, Direction.LEFT.offsetX + Direction.RIGHT.offsetX)
        assertEquals(0, Direction.TOP.offsetY + Direction.BOTTOM.offsetY)
    }

    @Test
    fun boardSizeLabelMatchesSpinnerFormat() {
        assertEquals("4 x 4", BoardTitledSize(width = 4, height = 4).toString())
    }

    @Test
    fun solvedStateHasEmptyTileAtBottomRight() {
        val state = SlidingPuzzleState.solved(width = 4, height = 4)

        assertTrue(state.isSolved())
        assertEquals(SlidingPuzzleState.EMPTY, state.tileAt(3, 3))
        assertEquals(SlidingPuzzleState.Cell(3, 3), state.emptyCell())
    }

    @Test
    fun onlyAdjacentTilesCanMoveIntoEmptyCell() {
        val state = SlidingPuzzleState.solved(width = 4, height = 4)

        assertTrue(state.canMove(2, 3))
        assertTrue(state.canMove(3, 2))
        assertFalse(state.canMove(0, 0))
        assertFalse(state.canMove(3, 3))
    }

    @Test
    fun validMoveSwapsTileWithEmptyCell() {
        val state = SlidingPuzzleState.solved(width = 4, height = 4)

        assertTrue(state.moveTile(2, 3))

        assertEquals(15, state.tileAt(3, 3))
        assertEquals(SlidingPuzzleState.EMPTY, state.tileAt(2, 3))
        assertFalse(state.isSolved())
    }

    @Test
    fun movingSameTileAgainUndoesPreviousMove() {
        val state = SlidingPuzzleState.solved(width = 4, height = 4)
        val solvedSnapshot = state.snapshot()

        assertTrue(state.moveTile(2, 3))
        assertTrue(state.moveTile(3, 3))

        assertEquals(solvedSnapshot, state.snapshot())
        assertTrue(state.isSolved())
    }

    @Test
    fun shuffledStateIsSolvableByConstructionAndUsuallyNotSolved() {
        val state = SlidingPuzzleState.shuffled(
            width = 4,
            height = 4,
            moves = 20,
            random = Random(7),
        )

        assertNotEquals(SlidingPuzzleState.solved(4, 4).snapshot(), state.snapshot())
        assertEquals((0 until 16).toList(), state.snapshot().sorted())
    }
}
