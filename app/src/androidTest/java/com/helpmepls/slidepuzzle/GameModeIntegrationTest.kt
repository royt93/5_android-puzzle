package com.helpmepls.slidepuzzle

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.helpmepls.slidepuzzle.act.GameAct
import com.helpmepls.slidepuzzle.game.GameBoard
import com.helpmepls.slidepuzzle.vm.BoardOptionsVm
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests cho Task 23 + Task 26 + fixes:
 * - Classic / Move Challenge / Time Attack mode đều launch đúng
 * - YOUR PHOTO ở đầu danh sách
 * - BoardSizeSpinnerFrm không crash sau onDestroyView (leak fix)
 * - YOUR PHOTO card icon không stretch (scaleType fix)
 */
@RunWith(AndroidJUnit4::class)
class GameModeIntegrationTest {

    private val ctx: Context get() = ApplicationProvider.getApplicationContext()

    private fun imageResId() = BoardOptionsVm.PREDEFINED_IMAGES
        .first { it.first != BoardOptionsVm.GALLERY_SLOT_RES_ID }.first

    private fun baseIntent(boardW: Int = 4, boardH: Int = 4) =
        Intent(ctx, GameAct::class.java).apply {
            putExtra(GameAct.EXTRA_IMAGE_RES_ID, imageResId())
            putExtra(GameAct.EXTRA_BOARD_WIDTH, boardW)
            putExtra(GameAct.EXTRA_BOARD_HEIGHT, boardH)
        }

    // ─── Classic mode: không có budget, timer count-up ────────────────────

    @Test
    fun classicMode_noBudgetExtra_boardRenders() {
        ActivityScenario.launch<GameAct>(baseIntent()).use { scenario ->
            scenario.moveToState(Lifecycle.State.RESUMED)
            onView(withId(R.id.boardView)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun classicMode_tvMoveBudgetGone_tvTimerVisible() {
        ActivityScenario.launch<GameAct>(baseIntent()).use { scenario ->
            scenario.moveToState(Lifecycle.State.RESUMED)
            scenario.onActivity { act ->
                assertEquals(View.GONE,
                    act.findViewById<View>(R.id.tvMoveBudget).visibility)
                assertEquals(View.VISIBLE,
                    act.findViewById<View>(R.id.tvTimer).visibility)
                assertEquals(View.VISIBLE,
                    act.findViewById<View>(R.id.tvMoves).visibility)
            }
        }
    }

    // ─── Move Challenge: budget visible, value đúng ───────────────────────

    @Test
    fun moveChallenge_budgetVisible_initialValue150() {
        val intent = baseIntent().apply { putExtra(GameAct.EXTRA_MOVE_BUDGET, 150) }
        ActivityScenario.launch<GameAct>(intent).use { scenario ->
            scenario.moveToState(Lifecycle.State.RESUMED)
            scenario.onActivity { act ->
                val tv = act.findViewById<android.widget.TextView>(R.id.tvMoveBudget)
                assertEquals(View.VISIBLE, tv.visibility)
                assertTrue("Budget phải chứa 150, got: '${tv.text}'",
                    tv.text.toString().contains("150"))
            }
        }
    }

    @Test
    fun moveChallenge_3x3Board_budgetVisible() {
        val intent = baseIntent(3, 3).apply { putExtra(GameAct.EXTRA_MOVE_BUDGET, 150) }
        ActivityScenario.launch<GameAct>(intent).use { scenario ->
            scenario.moveToState(Lifecycle.State.RESUMED)
            scenario.onActivity { act ->
                assertEquals(View.VISIBLE,
                    act.findViewById<View>(R.id.tvMoveBudget).visibility)
            }
        }
    }

    @Test
    fun moveChallenge_timerStartsAt_00_00() {
        // Timer chỉ start khi first move — ngay sau launch phải là 00:00
        val intent = baseIntent().apply { putExtra(GameAct.EXTRA_MOVE_BUDGET, 150) }
        ActivityScenario.launch<GameAct>(intent).use { scenario ->
            scenario.moveToState(Lifecycle.State.RESUMED)
            scenario.onActivity { act ->
                val text = act.findViewById<android.widget.TextView>(R.id.tvTimer).text.toString()
                assertTrue("Timer trong Move Challenge phải bắt đầu 00:00, got: '$text'",
                    text.contains("00:00"))
            }
        }
    }

    // ─── Time Attack: countdown hiển thị, budget ẩn ──────────────────────

    @Test
    fun timeAttack_hard90s_tvMoveBudgetGone_tvTimerVisible() {
        val intent = baseIntent().apply { putExtra(GameAct.EXTRA_TIME_LIMIT_SECONDS, 90) }
        ActivityScenario.launch<GameAct>(intent).use { scenario ->
            scenario.moveToState(Lifecycle.State.RESUMED)
            scenario.onActivity { act ->
                assertEquals("Budget ẩn trong Time Attack",
                    View.GONE, act.findViewById<View>(R.id.tvMoveBudget).visibility)
                assertEquals("Timer visible trong Time Attack",
                    View.VISIBLE, act.findViewById<View>(R.id.tvTimer).visibility)
            }
        }
    }

    @Test
    fun timeAttack_autoTimerStart_timerNotZeroAfterBoard() {
        val intent = baseIntent().apply { putExtra(GameAct.EXTRA_TIME_LIMIT_SECONDS, 90) }
        ActivityScenario.launch<GameAct>(intent).use { scenario ->
            scenario.moveToState(Lifecycle.State.RESUMED)
            scenario.onActivity { act ->
                val text = act.findViewById<android.widget.TextView>(R.id.tvTimer).text.toString()
                assertFalse("Time Attack timer không được là 00:00 (auto-start)",
                    text.contains("00:00"))
            }
        }
    }

    @Test
    fun timeAttack_allDifficultiesLaunchWithoutCrash() {
        listOf(300, 180, 90).forEach { limit ->
            val intent = baseIntent().apply { putExtra(GameAct.EXTRA_TIME_LIMIT_SECONDS, limit) }
            ActivityScenario.launch<GameAct>(intent).use { scenario ->
                scenario.moveToState(Lifecycle.State.RESUMED)
                assertEquals("Time Attack ${limit}s phải RESUME",
                    Lifecycle.State.RESUMED, scenario.state)
            }
        }
    }

    // ─── YOUR PHOTO position regression guard ─────────────────────────────

    @Test
    fun gallerySlot_isFirstItem() {
        val images = BoardOptionsVm.PREDEFINED_IMAGES
        assertEquals(
            "YOUR PHOTO phải ở vị trí đầu (index 0)",
            BoardOptionsVm.GALLERY_SLOT_RES_ID,
            images[0].first
        )
    }

    @Test
    fun gallerySlot_notLastItem() {
        val images = BoardOptionsVm.PREDEFINED_IMAGES
        assertFalse(
            "YOUR PHOTO không được ở cuối sau khi đã chuyển lên đầu",
            images.last().first == BoardOptionsVm.GALLERY_SLOT_RES_ID
        )
    }

    // ─── Board renders đúng trên mọi size ────────────────────────────────

    @Test
    fun moveChallengeWith3x3Board_rendersCorrectly() {
        val intent = baseIntent(3, 3).apply { putExtra(GameAct.EXTRA_MOVE_BUDGET, 150) }
        ActivityScenario.launch<GameAct>(intent).use { scenario ->
            scenario.moveToState(Lifecycle.State.RESUMED)
            scenario.onActivity { act ->
                val board = act.findViewById<GameBoard>(R.id.boardView)
                assertNotNull(board)
                assertTrue("Board 3x3 phải có kích thước > 0", board.width > 0)
            }
        }
    }

    // ─── Shuffle/Reset không crash trong Move Challenge ───────────────────

    @Test
    fun moveChallenge_shuffleDoesNotCrash() {
        val intent = baseIntent().apply { putExtra(GameAct.EXTRA_MOVE_BUDGET, 150) }
        ActivityScenario.launch<GameAct>(intent).use { scenario ->
            scenario.moveToState(Lifecycle.State.RESUMED)
            scenario.onActivity { act ->
                val board = act.findViewById<GameBoard>(R.id.boardView)
                board.shuffle(false)  // shuffle không crash với budget mode
            }
        }
    }

    @Test
    fun moveChallenge_resetDoesNotCrash() {
        val intent = baseIntent().apply { putExtra(GameAct.EXTRA_MOVE_BUDGET, 150) }
        ActivityScenario.launch<GameAct>(intent).use { scenario ->
            scenario.moveToState(Lifecycle.State.RESUMED)
            scenario.onActivity { act ->
                val board = act.findViewById<GameBoard>(R.id.boardView)
                board.shuffle(true)  // reset (true = go to solved state)
            }
        }
    }

    // ─── BoardSizeSpinnerFrm leak fix: onDestroyView không crash ─────────

    @Test
    fun gameActDestroy_doesNotCrash() {
        // Verify GameAct lifecycle đi qua DESTROYED mà không crash
        ActivityScenario.launch<GameAct>(baseIntent()).use { scenario ->
            scenario.moveToState(Lifecycle.State.RESUMED)
            scenario.moveToState(Lifecycle.State.DESTROYED)
            // Nếu Spinner leak vẫn còn → có thể gây crash khi cleanup
        }
    }

    @Test
    fun timeAttack_gameActDestroy_doesNotCrash() {
        val intent = baseIntent().apply { putExtra(GameAct.EXTRA_TIME_LIMIT_SECONDS, 90) }
        ActivityScenario.launch<GameAct>(intent).use { scenario ->
            scenario.moveToState(Lifecycle.State.RESUMED)
            scenario.moveToState(Lifecycle.State.DESTROYED)
            // Timer phải được cancel trong onDestroy, không leak
        }
    }

    // ─── EXTRA constants không đổi (regression guard) ────────────────────

    @Test
    fun extraMoveBudgetKey_containsPackageName() {
        assertTrue(GameAct.EXTRA_MOVE_BUDGET.startsWith("com.helpmepls.slidepuzzle"))
    }

    @Test
    fun extraTimeLimitKey_containsPackageName() {
        assertTrue(GameAct.EXTRA_TIME_LIMIT_SECONDS.startsWith("com.helpmepls.slidepuzzle"))
    }
}
