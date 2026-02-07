package com.helpmepls.slidepuzzle.game.state

import android.graphics.Bitmap
import android.graphics.Point
import android.util.Size
import androidx.annotation.Keep
import com.helpmepls.slidepuzzle.game.utils.BitmapTile
import kotlin.*

@Keep
class PuzzleGrid(
    val sourceImage: Bitmap?,
    var size: Size,
    private val missingSlides: Int = 1,
) {
    lateinit var puzzles: Puzzle2DArray
        private set

    fun isConfigured(): Boolean {
        return ::puzzles.isInitialized
    }

    private lateinit var bitmapTile: BitmapTile

    init {
        sourceImage?.let {
            regenerate(
                newSize = size,
                newImage = sourceImage
            )
        }
    }

    fun regenerate(
        newSize: Size,
        newImage: Bitmap? = null,
        shuffle: Boolean = false,
    ) {
        size = newSize
        bitmapTile = BitmapTile(image = newImage ?: sourceImage!!, size = newSize)
        puzzles = genRandomSlides(shuffle)
    }


    private fun genRandomSlides(shuffle: Boolean = true): Puzzle2DArray {
        // Generate solved state first
        val len = size.width * size.height
        val list = MutableList(len) { index ->
            if (index >= len - missingSlides)
                null
            else
                PuzzleDescriptor(
                    index,
                    bitmapTile.tiles[index / size.width][index % size.width]
                )
        }

        // Convert to 2D array
        val array = list.toTypedArray()
        val matrix = Array(size.height) { y ->
            Array(size.width) { x ->
                array[(y * size.width) + (x % size.width)]
            }
        }

        if (shuffle) {
            // Perform random valid moves to shuffle (Guarantees solvability)
            val moves = 100 // Sufficient for 4x4
            var emptyP = Point(size.width - 1, size.height - 1) // Start at bottom-right (standard solved state)

            // Find actual empty position if not standard (though we just generated it standard)
            // Just in case loop to find empty
            for(y in 0 until size.height) {
                for(x in 0 until size.width) {
                    if (matrix[y][x] == null) {
                        emptyP = Point(x, y)
                        break
                    }
                }
            }

            val random = java.util.Random()
            var lastDir: Direction? = null

            for (i in 0 until moves) {
                val validDirs = mutableListOf<Direction>()
                enumValues<Direction>().forEach { dir ->
                    // Check bounds for the TILE we want to move INTO empty
                    // Valid tile pos: emptyP.x - dir.offsetX, emptyP.y - dir.offsetY
                    val tileX = emptyP.x - dir.offsetX
                    val tileY = emptyP.y - dir.offsetY

                    if (tileX >= 0 && tileX < size.width && tileY >= 0 && tileY < size.height) {
                         // Don't undo immediate last move to avoid useless jitter
                         if (lastDir == null || (dir.offsetX != -lastDir!!.offsetX || dir.offsetY != -lastDir!!.offsetY)) {
                             validDirs.add(dir)
                         }
                    }
                }

                if (validDirs.isNotEmpty()) {
                    val dir = validDirs[random.nextInt(validDirs.size)]
                    // Swap
                    val tileX = emptyP.x - dir.offsetX
                    val tileY = emptyP.y - dir.offsetY
                    matrix[emptyP.y][emptyP.x] = matrix[tileY][tileX]
                    matrix[tileY][tileX] = null
                    emptyP.set(tileX, tileY)
                    lastDir = dir
                }
            }
        }

        return matrix
    }

    fun isSolved(): Boolean {
        var index = 0
        for (y in 0 until size.height) {
            for (x in 0 until size.width) {
                val puzzle = puzzles[y][x]
                if (puzzle == null) {
                    // Empty tile should be at the end for "solved" state usually,
                    // or just check if all other tiles are in order.
                    // For standard 15-puzzle, empty is at last index.
                    if (index != size.width * size.height - 1) {
                         // If we are strict that empty must be last. 
                         // Or we can just ignore null.
                         // But if null is in middle, order is broken.
                         return false
                    }
                } else {
                    if (puzzle.index != index) return false
                }
                index++
            }
        }
        return true
    }


    fun checkSlideMoveDirection(p: Point): Direction? {
        enumValues<Direction>().forEach { dir ->
            if (dir.offsetX + p.x < size.width
                && dir.offsetY + p.y < size.height
                && dir.offsetX + p.x >= 0
                && dir.offsetY + p.y >= 0
                && puzzles[p.y + dir.offsetY][p.x + dir.offsetX] == null
            ) {
                return dir
            }
        }

        return null
    }

    fun shuffle(reset: Boolean = false) {
        puzzles = genRandomSlides(!reset)
    }

    fun moveSlide(p: Point): Point? {
        val direction = checkSlideMoveDirection(p)

        direction?.let {
            val newPoint = Point(
                p.x + direction.offsetX,
                p.y + direction.offsetY
            )
            puzzles[newPoint.y][newPoint.x] = puzzles[p.y][p.x]
            puzzles[p.y][p.x] = null

            return newPoint
        }

        return null
    }
}
