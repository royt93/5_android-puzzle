package com.helpmepls.slidepuzzle

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.helpmepls.slidepuzzle.act.BoardOptionsAct
import org.junit.Test
import org.junit.runner.RunWith

/** Integration test (Task 08): bam About o footer -> dialog About hien voi nut GitHub. */
@RunWith(AndroidJUnit4::class)
class AboutDialogTest {

    @Test
    fun tappingAboutShowsDialogWithGithubButton() {
        ActivityScenario.launch(BoardOptionsAct::class.java).use {
            onView(withId(R.id.btnAbout)).perform(click())
            // Nut GitHub trong dialog (yesText) phai hien.
            onView(withText(R.string.about_github))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
        }
    }
}
