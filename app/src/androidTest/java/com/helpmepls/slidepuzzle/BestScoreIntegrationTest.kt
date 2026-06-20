package com.helpmepls.slidepuzzle

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.helpmepls.slidepuzzle.util.ScoreUtils
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration test: per-puzzle score save/load trong SharedPreferences.
 * Không cần Activity — test trực tiếp qua ScoreUtils keys + SharedPreferences.
 */
@RunWith(AndroidJUnit4::class)
class BestScoreIntegrationTest {

    private lateinit var ctx: Context
    private val testResId = 999          // resId giả, không cần drawable thật
    private val testW = 4; private val testH = 4

    @Before
    fun setUp() {
        ctx = ApplicationProvider.getApplicationContext()
        // Xóa dữ liệu test trước mỗi ca.
        ctx.getSharedPreferences(ScoreUtils.PREFS_NAME, Context.MODE_PRIVATE).edit().clear().commit()
    }

    @After
    fun tearDown() {
        ctx.getSharedPreferences(ScoreUtils.PREFS_NAME, Context.MODE_PRIVATE).edit().clear().commit()
    }

    @Test
    fun initialScoreIsNoBest() {
        val prefs = ctx.getSharedPreferences(ScoreUtils.PREFS_NAME, Context.MODE_PRIVATE)
        val stored = prefs.getInt(ScoreUtils.movesKey(testResId, testW, testH), ScoreUtils.NO_BEST)
        assertEquals("Chưa có record → phải trả NO_BEST", ScoreUtils.NO_BEST, stored)
    }

    @Test
    fun savingNewBestPersists() {
        val prefs = ctx.getSharedPreferences(ScoreUtils.PREFS_NAME, Context.MODE_PRIVATE)
        val key = ScoreUtils.movesKey(testResId, testW, testH)
        prefs.edit().putInt(key, 30).commit()

        val stored = prefs.getInt(key, ScoreUtils.NO_BEST)
        assertEquals("Score 30 phải được lưu đúng", 30, stored)
    }

    @Test
    fun scoreSavedForImageADoesNotAffectImageB() {
        val prefs = ctx.getSharedPreferences(ScoreUtils.PREFS_NAME, Context.MODE_PRIVATE)
        val keyA = ScoreUtils.movesKey(imageResId = 100, w = 4, h = 4)
        val keyB = ScoreUtils.movesKey(imageResId = 200, w = 4, h = 4)

        prefs.edit().putInt(keyA, 25).commit()

        // Image B vẫn phải là NO_BEST.
        val storedB = prefs.getInt(keyB, ScoreUtils.NO_BEST)
        assertEquals("Lưu score cho ảnh A không được ảnh hưởng ảnh B", ScoreUtils.NO_BEST, storedB)
    }

    @Test
    fun sameImageDifferentSizeHasSeparateKey() {
        val prefs = ctx.getSharedPreferences(ScoreUtils.PREFS_NAME, Context.MODE_PRIVATE)
        val key4x4 = ScoreUtils.movesKey(testResId, 4, 4)
        val key3x3 = ScoreUtils.movesKey(testResId, 3, 3)

        prefs.edit().putInt(key4x4, 40).commit()

        val stored3x3 = prefs.getInt(key3x3, ScoreUtils.NO_BEST)
        assertEquals("4x4 và 3x3 phải tách biệt", ScoreUtils.NO_BEST, stored3x3)
    }

    @Test
    fun isNewBestReturnsFalseWhenWorse() {
        val prefs = ctx.getSharedPreferences(ScoreUtils.PREFS_NAME, Context.MODE_PRIVATE)
        val key = ScoreUtils.movesKey(testResId, testW, testH)
        prefs.edit().putInt(key, 20).commit()

        val existing = prefs.getInt(key, ScoreUtils.NO_BEST)
        assertFalse("30 moves > 20 → không phải new best", ScoreUtils.isNewBest(30, existing))
    }

    @Test
    fun isNewBestReturnsTrueWhenBetter() {
        val prefs = ctx.getSharedPreferences(ScoreUtils.PREFS_NAME, Context.MODE_PRIVATE)
        val key = ScoreUtils.movesKey(testResId, testW, testH)
        prefs.edit().putInt(key, 30).commit()

        val existing = prefs.getInt(key, ScoreUtils.NO_BEST)
        assertTrue("15 moves < 30 → là new best", ScoreUtils.isNewBest(15, existing))
    }

    @Test
    fun bestTimeSavedSeparatelyFromMoves() {
        val prefs = ctx.getSharedPreferences(ScoreUtils.PREFS_NAME, Context.MODE_PRIVATE)
        val movesKey = ScoreUtils.movesKey(testResId, testW, testH)
        val timeKey  = ScoreUtils.timeKey(testResId, testW, testH)

        prefs.edit().putInt(movesKey, 20).commit()

        // timeKey không bị ảnh hưởng bởi movesKey.
        val storedTime = prefs.getInt(timeKey, ScoreUtils.NO_BEST_TIME)
        assertEquals("Time key phải độc lập với moves key", ScoreUtils.NO_BEST_TIME, storedTime)
    }
}
