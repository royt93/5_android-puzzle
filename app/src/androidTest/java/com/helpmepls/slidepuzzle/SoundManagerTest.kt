package com.helpmepls.slidepuzzle

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.helpmepls.slidepuzzle.util.SoundManager
import org.junit.Test
import org.junit.runner.RunWith

/** Widget test: SoundManager khoi tao/phat/release khong crash (SoundPool can Android runtime). */
@RunWith(AndroidJUnit4::class)
class SoundManagerTest {

    @Test
    fun constructPlayAndReleaseDoesNotCrash() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val manager = SoundManager(context)
        try {
            manager.isEnabled = true
            manager.playMove()
            manager.playWin()
            // Tat tieng: van goi duoc, chi la no-op.
            manager.isEnabled = false
            manager.playMove()
            manager.playWin()
        } finally {
            manager.release()
        }
    }
}
