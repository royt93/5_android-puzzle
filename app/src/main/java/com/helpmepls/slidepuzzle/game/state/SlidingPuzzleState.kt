package com.helpmepls.slidepuzzle.game.state

import java.util.Random
import kotlin.math.abs

class SlidingPuzzleState private constructor(
    val width: Int,
    val height: Int,
    private val tiles: IntArray,
) {
    init {
        require(width > 1 && height > 1) { "Puzzle size must be at least 2x2." }
        require(tiles.size == width * height) { "Tile count must match puzzle size." }
        require(tiles.count { it == EMPTY } == 1) { "Puzzle must contain exactly one empty tile." }
    }

    val emptyIndex: Int
        get() = tiles.indexOf(EMPTY)

    fun tileAt(x: Int, y: Int): Int {
        require(x in 0 until width && y in 0 until height) { "Cell is outside the board." }
        return tiles[indexOf(x, y)]
    }

    fun emptyCell(): Cell = cellOf(emptyIndex)

    fun isSolved(): Boolean {
        for (index in 0 until tiles.lastIndex) {
            if (tiles[index] != index + 1) return false
        }
        return tiles.last() == EMPTY
    }

    fun canMove(x: Int, y: Int): Boolean {
        if (x !in 0 until width || y !in 0 until height) return false
        val target = indexOf(x, y)
        if (tiles[target] == EMPTY) return false
        val empty = emptyCell()
        return abs(empty.x - x) + abs(empty.y - y) == 1
    }

    fun moveTile(x: Int, y: Int): Boolean {
        if (!canMove(x, y)) return false
        val target = indexOf(x, y)
        val empty = emptyIndex
        tiles[empty] = tiles[target]
        tiles[target] = EMPTY
        return true
    }

    fun snapshot(): List<Int> = tiles.toList()

    private fun indexOf(x: Int, y: Int): Int = y * width + x

    private fun cellOf(index: Int): Cell = Cell(
        x = index % width,
        y = index / width,
    )

    data class Cell(val x: Int, val y: Int)

    companion object {
        const val EMPTY = 0

        fun solved(width: Int, height: Int): SlidingPuzzleState {
            val len = width * height
            val tiles = IntArray(len) { index ->
                if (index == len - 1) EMPTY else index + 1
            }
            return SlidingPuzzleState(width, height, tiles)
        }

        fun shuffled(
            width: Int,
            height: Int,
            moves: Int = 100,
            random: Random = Random(),
        ): SlidingPuzzleState {
            val state = solved(width, height)
            var previousEmpty: Cell? = null

            repeat(moves.coerceAtLeast(0)) {
                val empty = state.emptyCell()
                val candidates = listOf(
                    Cell(empty.x - 1, empty.y),
                    Cell(empty.x + 1, empty.y),
                    Cell(empty.x, empty.y - 1),
                    Cell(empty.x, empty.y + 1),
                ).filter { cell ->
                    cell.x in 0 until width &&
                        cell.y in 0 until height &&
                        cell != previousEmpty
                }

                if (candidates.isNotEmpty()) {
                    val selected = candidates[random.nextInt(candidates.size)]
                    previousEmpty = empty
                    state.moveTile(selected.x, selected.y)
                }
            }

            return state
        }
    }
}
