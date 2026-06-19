package com.helpmepls.slidepuzzle

import android.content.Context
import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.helpmepls.slidepuzzle.act.GameAct
import com.helpmepls.slidepuzzle.vm.BoardOptionsVm
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration test: dam bao Activity launch va render duoc duoi NEON theme moi
 * (bat loi thieu color/attr/drawable khi lat sang dark base).
 */
@RunWith(AndroidJUnit4::class)
class NeonThemeIntegrationTest {

    @Test
    fun gameActivityLaunchesAndBoardDisplaysUnderNeonTheme() {
        val ctx: Context = ApplicationProvider.getApplicationContext()
        val intent = Intent(ctx, GameAct::class.java).apply {
            putExtra(GameAct.EXTRA_IMAGE_RES_ID, BoardOptionsVm.PREDEFINED_IMAGES.first().first)
            putExtra(GameAct.EXTRA_BOARD_WIDTH, 4)
            putExtra(GameAct.EXTRA_BOARD_HEIGHT, 4)
        }

        ActivityScenario.launch<GameAct>(intent).use { scenario ->
            scenario.moveToState(Lifecycle.State.RESUMED)
            assertEquals(Lifecycle.State.RESUMED, scenario.state)
            onView(withId(R.id.boardView)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun gameLaunchesWithNeonVectorImage() {
        // Exercise cau noi VectorDrawable -> Bitmap (Wave 4) + GameBoard glow (Wave 3).
        val ctx: Context = ApplicationProvider.getApplicationContext()
        val intent = Intent(ctx, GameAct::class.java).apply {
            putExtra(GameAct.EXTRA_IMAGE_RES_ID, R.drawable.neon_img_1)
            putExtra(GameAct.EXTRA_BOARD_WIDTH, 4)
            putExtra(GameAct.EXTRA_BOARD_HEIGHT, 4)
        }

        ActivityScenario.launch<GameAct>(intent).use { scenario ->
            scenario.moveToState(Lifecycle.State.RESUMED)
            assertEquals(Lifecycle.State.RESUMED, scenario.state)
            onView(withId(R.id.boardView)).check(matches(isDisplayed()))
        }
    }
}
