package com.helpmepls.slidepuzzle

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.helpmepls.slidepuzzle.R
import com.helpmepls.slidepuzzle.act.BoardOptionsAct
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WidgetFontScaleTest {
    @Test
    fun activityContextKeepsFontScaleAtOne() {
        ActivityScenario.launch(BoardOptionsAct::class.java).use { scenario ->
            scenario.onActivity { activity ->
                assertEquals(1.0f, activity.baseContext.resources.configuration.fontScale, 0.0f)
            }
            onView(withId(R.id.tbBoardOptions)).check(matches(isDisplayed()))
            onView(withId(R.id.gvImages)).check(matches(isDisplayed()))
        }
    }
}
