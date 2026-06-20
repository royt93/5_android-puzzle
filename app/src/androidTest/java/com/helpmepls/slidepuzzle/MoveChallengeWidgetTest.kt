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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Widget + integration tests cho Task 26 — Move Challenge.
 * Verify: tvMoveBudget, dividerBudget hiển thị đúng theo mode.
 */
@RunWith(AndroidJUnit4::class)
class MoveChallengeWidgetTest {

    private val ctx: Context get() = ApplicationProvider.getApplicationContext()

    private fun launchClassic(boardW: Int = 4, boardH: Int = 4): ActivityScenario<GameAct> {
        val intent = Intent(ctx, GameAct::class.java).apply {
            putExtra(GameAct.EXTRA_IMAGE_RES_ID, BoardOptionsVm.PREDEFINED_IMAGES
                .first { it.first != BoardOptionsVm.GALLERY_SLOT_RES_ID }.first)
            putExtra(GameAct.EXTRA_BOARD_WIDTH, boardW)
            putExtra(GameAct.EXTRA_BOARD_HEIGHT, boardH)
            // KHÔNG pass EXTRA_MOVE_BUDGET → Classic mode
        }
        return ActivityScenario.launch<GameAct>(intent)
            .also { it.moveToState(Lifecycle.State.RESUMED) }
    }

    private fun launchMoveChallenge(
        budget: Int = 150,
        boardW: Int = 4,
        boardH: Int = 4,
    ): ActivityScenario<GameAct> {
        val intent = Intent(ctx, GameAct::class.java).apply {
            putExtra(GameAct.EXTRA_IMAGE_RES_ID, BoardOptionsVm.PREDEFINED_IMAGES
                .first { it.first != BoardOptionsVm.GALLERY_SLOT_RES_ID }.first)
            putExtra(GameAct.EXTRA_BOARD_WIDTH, boardW)
            putExtra(GameAct.EXTRA_BOARD_HEIGHT, boardH)
            putExtra(GameAct.EXTRA_MOVE_BUDGET, budget)
        }
        return ActivityScenario.launch<GameAct>(intent)
            .also { it.moveToState(Lifecycle.State.RESUMED) }
    }

    // ─── tvMoveBudget visibility ───────────────────────────────────────────

    @Test
    fun tvMoveBudget_existsInLayout() {
        launchClassic().use { scenario ->
            scenario.onActivity { act ->
                assertNotNull("tvMoveBudget phải có trong act_game.xml",
                    act.findViewById(R.id.tvMoveBudget))
            }
        }
    }

    @Test
    fun tvMoveBudget_goneInClassicMode() {
        launchClassic().use { scenario ->
            scenario.onActivity { act ->
                val tv = act.findViewById<android.widget.TextView>(R.id.tvMoveBudget)
                assertEquals("tvMoveBudget phải GONE trong Classic mode",
                    View.GONE, tv.visibility)
            }
        }
    }

    @Test
    fun tvMoveBudget_visibleInMoveChallengeMode() {
        launchMoveChallenge(budget = 150).use { scenario ->
            scenario.onActivity { act ->
                val tv = act.findViewById<android.widget.TextView>(R.id.tvMoveBudget)
                assertEquals("tvMoveBudget phải VISIBLE trong Move Challenge mode",
                    View.VISIBLE, tv.visibility)
            }
        }
    }

    @Test
    fun tvMoveBudget_showsCorrectInitialBudget() {
        launchMoveChallenge(budget = 150).use { scenario ->
            scenario.onActivity { act ->
                val tv = act.findViewById<android.widget.TextView>(R.id.tvMoveBudget)
                val text = tv.text.toString()
                assertTrue("tvMoveBudget phải hiển thị budget 150, got: '$text'",
                    text.contains("150"))
            }
        }
    }

    @Test
    fun tvMoveBudget_showsEmojiPrefix() {
        launchMoveChallenge(budget = 150).use { scenario ->
            scenario.onActivity { act ->
                val text = act.findViewById<android.widget.TextView>(R.id.tvMoveBudget).text.toString()
                assertTrue("tvMoveBudget phải có emoji 🎯, got: '$text'",
                    text.contains("🎯"))
            }
        }
    }

    // ─── dividerBudget visibility ──────────────────────────────────────────

    @Test
    fun dividerBudget_existsInLayout() {
        launchClassic().use { scenario ->
            scenario.onActivity { act ->
                assertNotNull("dividerBudget phải có trong act_game.xml",
                    act.findViewById(R.id.dividerBudget))
            }
        }
    }

    @Test
    fun dividerBudget_goneInClassicMode() {
        launchClassic().use { scenario ->
            scenario.onActivity { act ->
                val divider = act.findViewById<View>(R.id.dividerBudget)
                assertEquals("dividerBudget phải GONE trong Classic mode",
                    View.GONE, divider.visibility)
            }
        }
    }

    @Test
    fun dividerBudget_visibleInMoveChallengeMode() {
        launchMoveChallenge(budget = 150).use { scenario ->
            scenario.onActivity { act ->
                val divider = act.findViewById<View>(R.id.dividerBudget)
                assertEquals("dividerBudget phải VISIBLE trong Move Challenge mode",
                    View.VISIBLE, divider.visibility)
            }
        }
    }

    // ─── Classic mode: tvTimer vẫn hiển thị đúng ──────────────────────────

    @Test
    fun tvTimer_visibleInClassicMode() {
        launchClassic().use { scenario ->
            scenario.onActivity { act ->
                val tv = act.findViewById<android.widget.TextView>(R.id.tvTimer)
                assertEquals("tvTimer phải VISIBLE trong Classic mode",
                    View.VISIBLE, tv.visibility)
            }
        }
    }

    @Test
    fun tvTimer_showsInitialZeroInClassicMode() {
        launchClassic().use { scenario ->
            scenario.onActivity { act ->
                val text = act.findViewById<android.widget.TextView>(R.id.tvTimer).text.toString()
                assertTrue("tvTimer phải bắt đầu từ 00:00 trong Classic mode, got: '$text'",
                    text.contains("00:00"))
            }
        }
    }

    // ─── Move Challenge với budget nhỏ ────────────────────────────────────

    @Test
    fun tvMoveBudget_showsCorrectSmallBudget() {
        launchMoveChallenge(budget = 20).use { scenario ->
            scenario.onActivity { act ->
                val text = act.findViewById<android.widget.TextView>(R.id.tvMoveBudget).text.toString()
                assertTrue("tvMoveBudget phải hiển thị budget 20, got: '$text'",
                    text.contains("20"))
            }
        }
    }

    @Test
    fun gameActLaunchesWithMoveBudgetWithoutCrash() {
        launchMoveChallenge(budget = 150, boardW = 3, boardH = 3).use { scenario ->
            assertEquals("GameAct phải RESUME với Move Challenge extra",
                Lifecycle.State.RESUMED, scenario.state)
        }
    }

    // ─── tvMoves luôn visible trong mọi mode ──────────────────────────────

    @Test
    fun tvMoves_visibleInBothModes() {
        launchClassic().use { s ->
            s.onActivity { act ->
                assertEquals(View.VISIBLE,
                    act.findViewById<View>(R.id.tvMoves).visibility)
            }
        }
        launchMoveChallenge().use { s ->
            s.onActivity { act ->
                assertEquals(View.VISIBLE,
                    act.findViewById<View>(R.id.tvMoves).visibility)
            }
        }
    }
}
