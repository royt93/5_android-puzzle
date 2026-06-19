package com.helpmepls.slidepuzzle

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.helpmepls.slidepuzzle.act.GameAct
import com.helpmepls.slidepuzzle.vm.BoardOptionsVm
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TileNumbersToggleTest {

    private fun prefs(context: Context) =
        context.getSharedPreferences("puzzle_prefs", Context.MODE_PRIVATE)

    @Test
    fun togglingShowTileNumbersPersistsToPrefs() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Bat dau tu trang thai bat (mac dinh).
        prefs(context).edit().putBoolean("show_numbers", true).apply()

        val intent = Intent(context, GameAct::class.java).apply {
            putExtra(GameAct.EXTRA_IMAGE_RES_ID, BoardOptionsVm.PREDEFINED_IMAGES.first().first)
            putExtra(GameAct.EXTRA_BOARD_WIDTH, 4)
            putExtra(GameAct.EXTRA_BOARD_HEIGHT, 4)
        }

        ActivityScenario.launch<GameAct>(intent).use {
            openActionBarOverflowOrOptionsMenu(context)
            onView(withText(R.string.show_tile_numbers)).perform(click())
        }

        // Sau khi tat, gia tri phai duoc luu lai (false).
        assertFalse(prefs(context).getBoolean("show_numbers", true))
    }
}
