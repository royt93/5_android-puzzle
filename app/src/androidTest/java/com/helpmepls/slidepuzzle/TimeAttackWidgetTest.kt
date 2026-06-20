package com.helpmepls.slidepuzzle

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.helpmepls.slidepuzzle.act.GameAct
import com.helpmepls.slidepuzzle.vm.BoardOptionsVm
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Widget + integration tests cho Task 23 — Time Attack Mode.
 * Verify: countdown timer, tvMoveBudget ẩn, không crash với mọi difficulty.
 */
@RunWith(AndroidJUnit4::class)
class TimeAttackWidgetTest {

    private val ctx: Context get() = ApplicationProvider.getApplicationContext()

    private fun imageResId() = BoardOptionsVm.PREDEFINED_IMAGES
        .first { it.first != BoardOptionsVm.GALLERY_SLOT_RES_ID }.first

    private fun launchTimeAttack(
        timeLimitSeconds: Int,
        boardW: Int = 4,
        boardH: Int = 4,
    ): ActivityScenario<GameAct> {
        val intent = Intent(ctx, GameAct::class.java).apply {
            putExtra(GameAct.EXTRA_IMAGE_RES_ID, imageResId())
            putExtra(GameAct.EXTRA_BOARD_WIDTH, boardW)
            putExtra(GameAct.EXTRA_BOARD_HEIGHT, boardH)
            putExtra(GameAct.EXTRA_TIME_LIMIT_SECONDS, timeLimitSeconds)
        }
        return ActivityScenario.launch<GameAct>(intent)
            .also { it.moveToState(Lifecycle.State.RESUMED) }
    }

    private fun launchClassic(): ActivityScenario<GameAct> {
        val intent = Intent(ctx, GameAct::class.java).apply {
            putExtra(GameAct.EXTRA_IMAGE_RES_ID, imageResId())
            putExtra(GameAct.EXTRA_BOARD_WIDTH, 4)
            putExtra(GameAct.EXTRA_BOARD_HEIGHT, 4)
        }
        return ActivityScenario.launch<GameAct>(intent)
            .also { it.moveToState(Lifecycle.State.RESUMED) }
    }

    // ─── Không crash khi launch với mọi difficulty ────────────────────────

    @Test
    fun timeAttack_easy300s_launchesWithoutCrash() {
        launchTimeAttack(300).use { scenario ->
            assertEquals(Lifecycle.State.RESUMED, scenario.state)
        }
    }

    @Test
    fun timeAttack_medium180s_launchesWithoutCrash() {
        launchTimeAttack(180).use { scenario ->
            assertEquals(Lifecycle.State.RESUMED, scenario.state)
        }
    }

    @Test
    fun timeAttack_hard90s_launchesWithoutCrash() {
        launchTimeAttack(90).use { scenario ->
            assertEquals(Lifecycle.State.RESUMED, scenario.state)
        }
    }

    // ─── tvTimer hiển thị countdown (không phải count-up) ─────────────────

    @Test
    fun tvTimer_showsCountdownFormatForHard90s() {
        launchTimeAttack(90).use { scenario ->
            scenario.onActivity { act ->
                val text = act.findViewById<android.widget.TextView>(R.id.tvTimer).text.toString()
                // Countdown từ 90s: text nên là "01:xx" hoặc "00:xx" (không phải "00:00" nếu timer đã chạy)
                assertTrue("tvTimer phải hiển thị countdown, got: '$text'",
                    text.startsWith("⏱"))
                // Format phải là MM:SS
                val timepart = text.removePrefix("⏱").trim()
                assertTrue("Format phải là MM:SS, got: '$timepart'",
                    timepart.matches(Regex("\\d{2}:\\d{2}")))
            }
        }
    }

    @Test
    fun tvTimer_hard90s_initialValueBelow90Seconds() {
        launchTimeAttack(90).use { scenario ->
            scenario.onActivity { act ->
                val text = act.findViewById<android.widget.TextView>(R.id.tvTimer).text.toString()
                val timepart = text.removePrefix("⏱").trim()
                val parts = timepart.split(":")
                if (parts.size == 2) {
                    val totalSecs = parts[0].toInt() * 60 + parts[1].toInt()
                    assertTrue("Countdown 90s phải <= 90, got $totalSecs", totalSecs <= 90)
                    assertTrue("Countdown 90s phải >= 0, got $totalSecs", totalSecs >= 0)
                }
            }
        }
    }

    @Test
    fun tvTimer_medium180s_initialValueBelow180Seconds() {
        launchTimeAttack(180).use { scenario ->
            scenario.onActivity { act ->
                val text = act.findViewById<android.widget.TextView>(R.id.tvTimer).text.toString()
                val timepart = text.removePrefix("⏱").trim()
                val parts = timepart.split(":")
                if (parts.size == 2) {
                    val totalSecs = parts[0].toInt() * 60 + parts[1].toInt()
                    assertTrue("Countdown 180s phải <= 180, got $totalSecs", totalSecs <= 180)
                }
            }
        }
    }

    // ─── tvMoveBudget phải ẩn trong Time Attack ───────────────────────────

    @Test
    fun tvMoveBudget_goneInTimeAttackMode() {
        launchTimeAttack(90).use { scenario ->
            scenario.onActivity { act ->
                val tv = act.findViewById<android.widget.TextView>(R.id.tvMoveBudget)
                assertEquals("tvMoveBudget phải GONE trong Time Attack (không có budget)",
                    View.GONE, tv.visibility)
            }
        }
    }

    @Test
    fun dividerBudget_goneInTimeAttackMode() {
        launchTimeAttack(90).use { scenario ->
            scenario.onActivity { act ->
                val divider = act.findViewById<View>(R.id.dividerBudget)
                assertEquals("dividerBudget phải GONE trong Time Attack",
                    View.GONE, divider.visibility)
            }
        }
    }

    // ─── Classic vs Time Attack: timer format khác nhau ───────────────────

    @Test
    fun classic_timerStartsAt_00_00() {
        launchClassic().use { scenario ->
            scenario.onActivity { act ->
                val text = act.findViewById<android.widget.TextView>(R.id.tvTimer).text.toString()
                assertTrue("Classic timer phải bắt đầu 00:00, got: '$text'",
                    text.contains("00:00"))
            }
        }
    }

    @Test
    fun timeAttack_timerDoesNotStartAt_00_00_for90sHard() {
        // Hard 90s timer tự start ngay, nên sẽ > 00:00 ngay sau khi board render
        launchTimeAttack(90).use { scenario ->
            scenario.onActivity { act ->
                val text = act.findViewById<android.widget.TextView>(R.id.tvTimer).text.toString()
                // Countdown bắt đầu từ 01:30 (90s), không phải 00:00
                assertFalse(
                    "Time Attack 90s timer không được bắt đầu từ 00:00, got: '$text'",
                    text.contains("00:00")
                )
            }
        }
    }

    // ─── tvMoves luôn visible trong Time Attack ───────────────────────────

    @Test
    fun tvMoves_visibleInTimeAttackMode() {
        launchTimeAttack(180).use { scenario ->
            scenario.onActivity { act ->
                assertEquals("tvMoves phải VISIBLE trong Time Attack",
                    View.VISIBLE,
                    act.findViewById<View>(R.id.tvMoves).visibility)
            }
        }
    }

    // ─── Board view tồn tại và không crash ────────────────────────────────

    @Test
    fun boardView_visibleInTimeAttackMode() {
        launchTimeAttack(180).use { scenario ->
            scenario.onActivity { act ->
                val board = act.findViewById<com.helpmepls.slidepuzzle.game.GameBoard>(R.id.boardView)
                assertTrue("boardView phải có kích thước > 0 sau layout", board.width > 0)
            }
        }
    }
}
