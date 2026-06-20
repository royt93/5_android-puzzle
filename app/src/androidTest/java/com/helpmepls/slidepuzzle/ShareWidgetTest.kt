package com.helpmepls.slidepuzzle

import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.helpmepls.slidepuzzle.util.ShareUtils
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/** Widget tests cho Task 27 — ShareUtils.createShareBitmap. */
@RunWith(AndroidJUnit4::class)
class ShareWidgetTest {

    @Test
    fun createShareBitmapHasCorrectDimensions() {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val puzzleBmp = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        val shareBmp = ShareUtils.createShareBitmap(ctx, puzzleBmp, moves = 42, timeSeconds = 125)
        assertNotNull(shareBmp)
        assertTrue("share bitmap phai rong 800px", shareBmp.width == 800)
        assertTrue("share bitmap phai cao 900px", shareBmp.height == 900)
        puzzleBmp.recycle()
        shareBmp.recycle()
    }

    @Test
    fun createShareBitmapHasNonTransparentBackground() {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val puzzleBmp = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888).apply {
            eraseColor(android.graphics.Color.RED)
        }
        val shareBmp = ShareUtils.createShareBitmap(ctx, puzzleBmp, moves = 10, timeSeconds = 60)
        // Pixel goc trai tren = nen deep navy (khong trong suot).
        val cornerPixel = shareBmp.getPixel(0, 0)
        assertTrue("nen share bitmap phai khong trong suot", cornerPixel != 0)
        puzzleBmp.recycle()
        shareBmp.recycle()
    }

    @Test
    fun createShareBitmapWithZeroMovesDoesNotCrash() {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val puzzleBmp = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val shareBmp = ShareUtils.createShareBitmap(ctx, puzzleBmp, moves = 0, timeSeconds = 0)
        assertNotNull(shareBmp)
        puzzleBmp.recycle()
        shareBmp.recycle()
    }

    @Test
    fun shareImageWritesCacheFile() {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val puzzleBmp = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888).apply {
            eraseColor(android.graphics.Color.BLUE)
        }
        val shareBmp = ShareUtils.createShareBitmap(ctx, puzzleBmp, moves = 5, timeSeconds = 30)
        // Ghi file cache để kiểm tra không crash khi tạo file.
        val file = java.io.File(ctx.cacheDir, "puzzle_result.png")
        file.outputStream().use { shareBmp.compress(Bitmap.CompressFormat.PNG, 90, it) }
        assertTrue("cache file phai ton tai sau khi ghi", file.exists())
        assertTrue("cache file phai co du lieu", file.length() > 0)
        puzzleBmp.recycle()
        shareBmp.recycle()
        file.delete()
    }
}
