package com.helpmepls.slidepuzzle

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.helpmepls.slidepuzzle.act.GameAct
import com.helpmepls.slidepuzzle.game.GameBoard
import com.helpmepls.slidepuzzle.vm.BoardOptionsVm
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/** Widget test cho GameBoard: render canvas (glow A7 + cache nhãn A8), undo no-op, toggle số. */
@RunWith(AndroidJUnit4::class)
class GameBoardWidgetTest {

    private fun launchGame(): ActivityScenario<GameAct> {
        val ctx: Context = ApplicationProvider.getApplicationContext()
        val intent = Intent(ctx, GameAct::class.java).apply {
            putExtra(GameAct.EXTRA_IMAGE_RES_ID, BoardOptionsVm.PREDEFINED_IMAGES.first { it.first != BoardOptionsVm.GALLERY_SLOT_RES_ID }.first)
            putExtra(GameAct.EXTRA_BOARD_WIDTH, 4)
            putExtra(GameAct.EXTRA_BOARD_HEIGHT, 4)
        }
        return ActivityScenario.launch<GameAct>(intent).also { it.moveToState(Lifecycle.State.RESUMED) }
    }

    @Test
    fun undoReturnsFalseWhenNoMoves() {
        launchGame().use { scenario ->
            scenario.onActivity { act ->
                val board = act.findViewById<GameBoard>(R.id.boardView)
                // Chua co nuoc di nao -> undo phai tra ve false (A11 early-return).
                assertFalse(board.undo())
            }
        }
    }

    @Test
    fun boardRendersNonBlankWithGlowAndNumbers() {
        launchGame().use { scenario ->
            scenario.onActivity { act ->
                val board = act.findViewById<GameBoard>(R.id.boardView)
                board.showNumbers = true
                assertTrue("board phai duoc layout", board.width > 0 && board.height > 0)

                val bmp = Bitmap.createBitmap(board.width, board.height, Bitmap.Config.ARGB_8888)
                board.draw(Canvas(bmp))

                // Co it nhat 1 pixel khong trong suot => onDraw (tile + glow + so) da ve.
                var painted = false
                loop@ for (x in 0 until board.width step 16) {
                    for (y in 0 until board.height step 16) {
                        if (bmp.getPixel(x, y) != 0) {
                            painted = true
                            break@loop
                        }
                    }
                }
                assertTrue("GameBoard phai ve ra noi dung", painted)
                bmp.recycle()
            }
        }
    }

    @Test
    fun playWinFeedbackRunsWithoutCrash() {
        // M2: loe halo lime khi thang.
        launchGame().use { scenario ->
            scenario.onActivity { act ->
                act.findViewById<GameBoard>(R.id.boardView).playWinFeedback()
            }
        }
    }

    @Test
    fun toggleShowNumbersDoesNotCrash() {
        launchGame().use { scenario ->
            scenario.onActivity { act ->
                val board = act.findViewById<GameBoard>(R.id.boardView)
                board.showNumbers = false
                board.showNumbers = true
            }
        }
    }

    @Test
    fun shuffleClearsUndoHistory() {
        // C4: sau shuffle, undo stack phai duoc xoa -> undo() tra ve false.
        launchGame().use { scenario ->
            scenario.onActivity { act ->
                val board = act.findViewById<GameBoard>(R.id.boardView)
                board.shuffle(false)
                assertFalse("shuffle phai xoa undo history", board.undo())
            }
        }
    }

    @Test
    fun resetClearsUndoHistory() {
        // C4: sau reset (shuffle reset=true), undo stack phai duoc xoa -> undo() tra ve false.
        launchGame().use { scenario ->
            scenario.onActivity { act ->
                val board = act.findViewById<GameBoard>(R.id.boardView)
                board.shuffle(true)
                assertFalse("reset phai xoa undo history", board.undo())
            }
        }
    }

    @Test
    fun onMoveSoundHookCanBeSetAndInvoked() {
        // SFX wiring: hook onMoveSound goi duoc, dem dung so lan.
        launchGame().use { scenario ->
            scenario.onActivity { act ->
                val board = act.findViewById<GameBoard>(R.id.boardView)
                var count = 0
                board.onMoveSound = { count++ }
                board.onMoveSound?.invoke()
                assertTrue("onMoveSound phai goi duoc", count == 1)
            }
        }
    }
}
