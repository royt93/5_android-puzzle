package com.helpmepls.slidepuzzle

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.helpmepls.slidepuzzle.act.GameAct
import com.helpmepls.slidepuzzle.vm.BoardOptionsVm
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppFlowIntegrationTest {
    @Test
    fun gameShuffleDialogFlowWorks() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = Intent(context, GameAct::class.java).apply {
            putExtra(GameAct.EXTRA_IMAGE_RES_ID, BoardOptionsVm.PREDEFINED_IMAGES.first { it.first != BoardOptionsVm.GALLERY_SLOT_RES_ID }.first)
            putExtra(GameAct.EXTRA_BOARD_WIDTH, 4)
            putExtra(GameAct.EXTRA_BOARD_HEIGHT, 4)
        }

        ActivityScenario.launch<GameAct>(intent).use {
            onView(withId(R.id.boardView)).check(matches(isDisplayed()))
            onView(withId(R.id.btShuffle)).perform(click())
            onView(withText("SHUFFLE PUZZLE")).check(matches(isDisplayed()))
            onView(withText("NO")).perform(click())
            onView(withId(R.id.btReset)).check(matches(isDisplayed()))
        }
    }
}
