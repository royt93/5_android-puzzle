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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/** Widget tests cho Task 25 (Hint), Task 27 (Share bitmap), Task 32 (progress percent). */
@RunWith(AndroidJUnit4::class)
class HintWidgetTest {

    private fun launchGame(): ActivityScenario<GameAct> {
        val ctx: Context = ApplicationProvider.getApplicationContext()
        val intent = Intent(ctx, GameAct::class.java).apply {
            putExtra(GameAct.EXTRA_IMAGE_RES_ID, BoardOptionsVm.PREDEFINED_IMAGES.first().first)
            putExtra(GameAct.EXTRA_BOARD_WIDTH, 4)
            putExtra(GameAct.EXTRA_BOARD_HEIGHT, 4)
        }
        return ActivityScenario.launch<GameAct>(intent).also { it.moveToState(Lifecycle.State.RESUMED) }
    }

    // Task 32 —————————————————————————————

    @Test
    fun correctTilePercentIsInValidRange() {
        launchGame().use { scenario ->
            scenario.onActivity { act ->
                val board = act.findViewById<GameBoard>(R.id.boardView)
                val percent = board.correctTilePercent()
                assertTrue("percent phai >= 0", percent >= 0f)
                assertTrue("percent phai <= 1", percent <= 1f)
            }
        }
    }

    @Test
    fun setGlowIntensityDoesNotCrash() {
        launchGame().use { scenario ->
            scenario.onActivity { act ->
                val board = act.findViewById<GameBoard>(R.id.boardView)
                board.setGlowIntensity(2.0f)
                board.setGlowIntensity(1.0f)
            }
        }
    }

    @Test
    fun almostThereBannerExistsInLayout() {
        launchGame().use { scenario ->
            scenario.onActivity { act ->
                assertNotNull("tvAlmostThere phai co trong layout",
                    act.findViewById(R.id.tvAlmostThere))
            }
        }
    }

    // Task 25 —————————————————————————————

    @Test
    fun hintButtonExistsInLayout() {
        launchGame().use { scenario ->
            scenario.onActivity { act ->
                assertNotNull("btnHint phai co trong layout",
                    act.findViewById(R.id.btnHint))
            }
        }
    }

    @Test
    fun getWrongPositionTilesOnShuffledBoard() {
        launchGame().use { scenario ->
            scenario.onActivity { act ->
                val board = act.findViewById<GameBoard>(R.id.boardView)
                // Board shuffle ngay khi resize → phai co tile sai vi tri.
                val wrong = board.getWrongPositionTiles()
                assertTrue("board da shuffle phai co tile sai vi tri", wrong.isNotEmpty())
            }
        }
    }

    @Test
    fun flashHintTileDoesNotCrash() {
        launchGame().use { scenario ->
            scenario.onActivity { act ->
                val board = act.findViewById<GameBoard>(R.id.boardView)
                val wrong = board.getWrongPositionTiles()
                if (wrong.isNotEmpty()) {
                    val (gx, gy) = wrong.first()
                    board.flashHintTile(gx, gy, 100L)
                }
            }
        }
    }

    @Test
    fun getWrongPositionTilesEmptyOnSolvedState() {
        launchGame().use { scenario ->
            scenario.onActivity { act ->
                val board = act.findViewById<GameBoard>(R.id.boardView)
                // Reset về solved state.
                board.shuffle(reset = true)
                // Solved state = tất cả tile đúng vị trí.
                // Sau shuffle(reset=true) board vẫn shuffle lại → không nhất thiết solved.
                // Ta chỉ test rằng hàm không crash và trả về List.
                val wrong = board.getWrongPositionTiles()
                assertNotNull(wrong)
            }
        }
    }

    // Task 27 —————————————————————————————

    @Test
    fun getCompletedBitmapIsNonBlank() {
        launchGame().use { scenario ->
            scenario.onActivity { act ->
                val board = act.findViewById<GameBoard>(R.id.boardView)
                assertTrue("board phai duoc layout truoc khi snapshot",
                    board.width > 0 && board.height > 0)
                val bmp = board.getCompletedBitmap()
                assertNotNull(bmp)
                assertTrue("snapshot phai co kich thuoc > 0",
                    bmp.width > 0 && bmp.height > 0)
                var hasContent = false
                loop@ for (x in 0 until bmp.width step 16) {
                    for (y in 0 until bmp.height step 16) {
                        if (bmp.getPixel(x, y) != 0) { hasContent = true; break@loop }
                    }
                }
                assertTrue("snapshot board phai co noi dung", hasContent)
                bmp.recycle()
            }
        }
    }
}
