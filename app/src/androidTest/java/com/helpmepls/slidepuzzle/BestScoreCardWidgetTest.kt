package com.helpmepls.slidepuzzle

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.helpmepls.slidepuzzle.adt.ImageCardsAdt
import com.helpmepls.slidepuzzle.model.TitledCardInfo
import com.helpmepls.slidepuzzle.util.ScoreUtils
import com.helpmepls.slidepuzzle.vm.BoardOptionsVm
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Widget test: ImageCardsAdt render đúng best-score badge và gallery slot.
 * getView() gọi Glide → phải chạy trên main thread (runOnMainSync).
 */
@RunWith(AndroidJUnit4::class)
class BestScoreCardWidgetTest {

    private lateinit var ctx: Context
    private val testResId = BoardOptionsVm.PREDEFINED_IMAGES.first { it.first != BoardOptionsVm.GALLERY_SLOT_RES_ID }.first
    private val w = 4; private val h = 4

    @Before
    fun setUp() {
        val appCtx: Context = ApplicationProvider.getApplicationContext()
        ctx = ContextThemeWrapper(appCtx, R.style.AppTheme)
        ctx.getSharedPreferences(ScoreUtils.PREFS_NAME, Context.MODE_PRIVATE).edit().clear().commit()
    }

    @After
    fun tearDown() {
        ctx.getSharedPreferences(ScoreUtils.PREFS_NAME, Context.MODE_PRIVATE).edit().clear().commit()
    }

    private fun buildAdapter(cards: Array<TitledCardInfo>): ImageCardsAdt {
        val prefs = ctx.getSharedPreferences(ScoreUtils.PREFS_NAME, Context.MODE_PRIVATE)
        return ImageCardsAdt(ctx, cards, prefs, w, h)
    }

    private fun getViewOnMainThread(adapter: ImageCardsAdt, position: Int): View {
        var result: View? = null
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            result = adapter.getView(position, null, FrameLayout(ctx))
        }
        return result!!
    }

    @Test
    fun bestScoreBadgeGoneWhenNoRecord() {
        val adapter = buildAdapter(arrayOf(TitledCardInfo(testResId, "")))
        val view = getViewOnMainThread(adapter, 0)
        val badge = view.findViewById<TextView>(R.id.tvBestScore)
        assertEquals("Khi chưa có record, badge phải GONE", View.GONE, badge.visibility)
    }

    @Test
    fun bestScoreBadgeVisibleWhenRecordExists() {
        val prefs = ctx.getSharedPreferences(ScoreUtils.PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(ScoreUtils.movesKey(testResId, w, h), 24).commit()

        val adapter = buildAdapter(arrayOf(TitledCardInfo(testResId, "")))
        val view = getViewOnMainThread(adapter, 0)
        val badge = view.findViewById<TextView>(R.id.tvBestScore)

        assertEquals("Khi có record, badge phải VISIBLE", View.VISIBLE, badge.visibility)
        assertTrue("Badge text phải chứa số 24", badge.text.contains("24"))
    }

    @Test
    fun gallerySlotRendersTitleVisible() {
        val card = TitledCardInfo(BoardOptionsVm.GALLERY_SLOT_RES_ID, "YOUR PHOTO", isGallerySlot = true)
        val adapter = buildAdapter(arrayOf(card))
        val view = getViewOnMainThread(adapter, 0)
        val title = view.findViewById<TextView>(R.id.title)

        assertEquals("Gallery slot title phải VISIBLE", View.VISIBLE, title.visibility)
        assertTrue("Title phải chứa 'PHOTO'", title.text.contains("PHOTO", ignoreCase = true))
    }

    @Test
    fun gallerySlotBestScoreBadgeAlwaysGone() {
        val card = TitledCardInfo(BoardOptionsVm.GALLERY_SLOT_RES_ID, "", isGallerySlot = true)
        val adapter = buildAdapter(arrayOf(card))
        val view = getViewOnMainThread(adapter, 0)
        val badge = view.findViewById<TextView>(R.id.tvBestScore)

        assertEquals("Gallery slot không có best score badge", View.GONE, badge.visibility)
    }

    @Test
    fun adapterCountMatchesCards() {
        val cards = arrayOf(
            TitledCardInfo(testResId, ""),
            TitledCardInfo(BoardOptionsVm.GALLERY_SLOT_RES_ID, "", isGallerySlot = true),
        )
        assertEquals("Count phải đúng số card", 2, buildAdapter(cards).count)
    }
}
