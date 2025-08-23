package com.helpmepls.slidepuzzle.game.state

enum class Direction(val offsetX: Int, val offsetY: Int) {
    TOP(offsetX = 0, offsetY = -1),
    BOTTOM(offsetX = 0, offsetY = 1),
    LEFT(offsetX = -1, offsetY = 0),
    RIGHT(offsetX = 1, offsetY = 0)
}
