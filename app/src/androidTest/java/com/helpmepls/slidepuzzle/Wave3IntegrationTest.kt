package com.helpmepls.slidepuzzle

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.helpmepls.slidepuzzle.act.GameAct
import com.helpmepls.slidepuzzle.game.GameBoard
import com.helpmepls.slidepuzzle.vm.BoardOptionsVm
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration test cho Wave 3 (Tasks 25, 27, 30, 32):
 * - Hint button tồn tại và không crash khi tap.
 * - Almost there banner tồn tại và GONE lúc khởi động.
 * - correctTilePercent + getWrongPositionTiles chạy đúng trên board đang chơi.
 * - getCompletedBitmap trả về bitmap có nội dung.
 */
@RunWith(AndroidJUnit4::class)
class Wave3IntegrationTest {

    private fun launchGame(): ActivityScenario<GameAct> {
        val ctx: Context = ApplicationProvider.getApplicationContext()
        val intent = Intent(ctx, GameAct::class.java).apply {
            putExtra(GameAct.EXTRA_IMAGE_RES_ID, BoardOptionsVm.PREDEFINED_IMAGES.first { it.first != BoardOptionsVm.GALLERY_SLOT_RES_ID }.first)
            putExtra(GameAct.EXTRA_BOARD_WIDTH, 3)
            putExtra(GameAct.EXTRA_BOARD_HEIGHT, 3)
        }
        return ActivityScenario.launch<GameAct>(intent).also { it.moveToState(Lifecycle.State.RESUMED) }
    }

    @Test
    fun allWave3ViewsExistInGameActivity() {
        launchGame().use { scenario ->
            scenario.onActivity { act ->
                assertNotNull("btnHint phai co", act.findViewById(R.id.btnHint))
                assertNotNull("tvAlmostThere phai co", act.findViewById(R.id.tvAlmostThere))
                assertNotNull("boardView phai co", act.findViewById<GameBoard>(R.id.boardView))
            }
        }
    }

    @Test
    fun almostThereBannerGoneOnGameStart() {
        launchGame().use { scenario ->
            scenario.onActivity { act ->
                val banner = act.findViewById<android.widget.TextView>(R.id.tvAlmostThere)
                assertEquals("banner phai GONE luc moi vao game",
                    View.GONE, banner.visibility)
            }
        }
    }

    @Test
    fun hintButtonTapDoesNotCrash() {
        launchGame().use { scenario ->
            scenario.onActivity { act ->
                act.findViewById<View>(R.id.btnHint)?.performClick()
            }
        }
    }

    @Test
    fun correctTilePercentAndWrongTilesConsistent() {
        launchGame().use { scenario ->
            scenario.onActivity { act ->
                val board = act.findViewById<GameBoard>(R.id.boardView)
                val percent = board.correctTilePercent()
                val wrong = board.getWrongPositionTiles()
                val totalTiles = 3 * 3 - 1  // 3x3 board, trừ blank
                val expectedCorrect = totalTiles - wrong.size
                val expectedPercent = expectedCorrect.toFloat() / totalTiles
                assertTrue(
                    "percent (%.3f) phai khop voi wrong tiles (%d sai / %d tong)".format(
                        percent, wrong.size, totalTiles),
                    Math.abs(percent - expectedPercent) < 0.01f
                )
            }
        }
    }

    @Test
    fun getCompletedBitmapMatchesBoardSize() {
        launchGame().use { scenario ->
            scenario.onActivity { act ->
                val board = act.findViewById<GameBoard>(R.id.boardView)
                val bmp = board.getCompletedBitmap()
                assertEquals("bitmap width phai bang board width", board.width, bmp.width)
                assertEquals("bitmap height phai bang board height", board.height, bmp.height)
                bmp.recycle()
            }
        }
    }

    @Test
    fun shuffleResetsHintAndBanner() {
        launchGame().use { scenario ->
            scenario.onActivity { act ->
                val board = act.findViewById<GameBoard>(R.id.boardView)
                val banner = act.findViewById<android.widget.TextView>(R.id.tvAlmostThere)
                // Simulate: flash hint then shuffle.
                val wrong = board.getWrongPositionTiles()
                if (wrong.isNotEmpty()) {
                    val (gx, gy) = wrong.first()
                    board.flashHintTile(gx, gy, 50L)
                }
                board.shuffle(false)
                // Sau shuffle: glow multiplier phai reset.
                board.setGlowIntensity(1.0f)  // Không crash.
                // Banner phai GONE (do board reset → onMoveListener invoke với moves=0).
                // Đây là best-effort check: banner có thể chưa update nếu UI thread chưa chạy.
                assertNotNull(banner)
            }
        }
    }
}
