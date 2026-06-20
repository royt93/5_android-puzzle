package com.helpmepls.slidepuzzle

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.helpmepls.slidepuzzle.act.GameAct
import com.helpmepls.slidepuzzle.v.NeonParticleView
import com.helpmepls.slidepuzzle.vm.BoardOptionsVm
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration test: win celebration flow.
 * Xác nhận NeonParticleView tồn tại và burst() chuyển visibility VISIBLE.
 * Không cần solve thật — test trực tiếp behavior của view.
 */
@RunWith(AndroidJUnit4::class)
class WinCelebrationIntegrationTest {

    private fun launchGame(): ActivityScenario<GameAct> {
        val ctx: Context = ApplicationProvider.getApplicationContext()
        val intent = Intent(ctx, GameAct::class.java).apply {
            putExtra(GameAct.EXTRA_IMAGE_RES_ID, BoardOptionsVm.PREDEFINED_IMAGES.first().first)
            putExtra(GameAct.EXTRA_BOARD_WIDTH, 4)
            putExtra(GameAct.EXTRA_BOARD_HEIGHT, 4)
        }
        return ActivityScenario.launch<GameAct>(intent).also { it.moveToState(Lifecycle.State.RESUMED) }
    }

    @Test
    fun particleViewExistsAndStartsGone() {
        launchGame().use { scenario ->
            scenario.onActivity { act ->
                val pv = act.findViewById<NeonParticleView>(R.id.particleView)
                assertNotNull("NeonParticleView phải tồn tại trong layout", pv)
                assertEquals("NeonParticleView phải GONE ban đầu", View.GONE, pv.visibility)
            }
        }
    }

    @Test
    fun particleViewBecomesVisibleAfterBurst() {
        launchGame().use { scenario ->
            scenario.onActivity { act ->
                val pv = act.findViewById<NeonParticleView>(R.id.particleView)
                // Trigger burst — mô phỏng khoảnh khắc win.
                pv.burst(500f, 500f)
                assertEquals("Sau burst, NeonParticleView phải VISIBLE", View.VISIBLE, pv.visibility)
            }
        }
    }

    @Test
    fun playWinFeedbackDoesNotCrash() {
        launchGame().use { scenario ->
            scenario.onActivity { act ->
                val board = act.findViewById<com.helpmepls.slidepuzzle.game.GameBoard>(R.id.boardView)
                // Không crash khi gọi win feedback bất cứ lúc nào.
                board.playWinFeedback()
            }
        }
    }

    @Test
    fun neonBorderViewExistsAndIsHardwareLayer() {
        launchGame().use { scenario ->
            scenario.onActivity { act ->
                val border = act.findViewById<com.helpmepls.slidepuzzle.v.NeonBorderView>(R.id.neonBorderView)
                assertNotNull("NeonBorderView phải tồn tại", border)
                assertEquals(
                    "NeonBorderView phải dùng HARDWARE layer",
                    View.LAYER_TYPE_HARDWARE,
                    border.layerType,
                )
            }
        }
    }
}
