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
import com.helpmepls.slidepuzzle.util.Prefs
import com.helpmepls.slidepuzzle.vm.BoardOptionsVm
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** Widget tests cho Task 30 — shimmer animation trong ImageCardsAdt. */
@RunWith(AndroidJUnit4::class)
class ShimmerAdapterTest {

    private lateinit var ctx: Context

    @Before
    fun setUp() {
        val appCtx: Context = ApplicationProvider.getApplicationContext()
        ctx = ContextThemeWrapper(appCtx, R.style.AppTheme)
    }

    private fun buildAdapter(): ImageCardsAdt {
        // Bỏ gallery slot (resId=0) — shimmer chỉ áp dụng cho image cards thật.
        val cards = BoardOptionsVm.PREDEFINED_IMAGES
            .filter { it.first != BoardOptionsVm.GALLERY_SLOT_RES_ID }
            .take(3)
            .map { (resId, title) -> TitledCardInfo(imageResId = resId, title = title) }
            .toTypedArray()
        return ImageCardsAdt(ctx, cards, Prefs.get(ctx), boardW = 4, boardH = 4)
    }

    private fun getViewOnMain(adapter: ImageCardsAdt, position: Int): View {
        var result: View? = null
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            result = adapter.getView(position, null, FrameLayout(ctx))
        }
        return result!!
    }

    @Test
    fun shimmerLineExistsInCardLayout() {
        val adapter = buildAdapter()
        val view = getViewOnMain(adapter, 0)
        assertNotNull("shimmerLine phai co trong frm_titled_image_card",
            view.findViewById<View>(R.id.shimmerLine))
    }

    @Test
    fun shimmerLineGoneByDefault() {
        val adapter = buildAdapter()
        val view = getViewOnMain(adapter, 0)
        val shimmer = view.findViewById<View>(R.id.shimmerLine)
        assertEquals("shimmerLine phai GONE mac dinh", View.GONE, shimmer.visibility)
    }

    @Test
    fun shimmerVisibleWhenPositionSelected() {
        val adapter = buildAdapter()
        adapter.setSelectedPosition(0)
        val view = getViewOnMain(adapter, 0)
        val shimmer = view.findViewById<View>(R.id.shimmerLine)
        assertEquals("shimmerLine phai VISIBLE khi position duoc chon",
            View.VISIBLE, shimmer.visibility)
    }

    @Test
    fun shimmerGoneForUnselectedPosition() {
        val adapter = buildAdapter()
        adapter.setSelectedPosition(0)
        val view = getViewOnMain(adapter, 1)
        val shimmer = view.findViewById<View>(R.id.shimmerLine)
        assertEquals("shimmerLine phai GONE cho position khong duoc chon",
            View.GONE, shimmer.visibility)
    }

    @Test
    fun setSelectedPositionMinusOneHidesAllShimmers() {
        val adapter = buildAdapter()
        adapter.setSelectedPosition(-1)
        val view = getViewOnMain(adapter, 0)
        val shimmer = view.findViewById<View>(R.id.shimmerLine)
        assertEquals("shimmerLine phai GONE khi selectedPosition = -1",
            View.GONE, shimmer.visibility)
    }
}
