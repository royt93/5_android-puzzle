package com.helpmepls.slidepuzzle

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.content.res.AppCompatResources
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/** Widget test: 6 vector neon render ra bitmap vuong khong rong (co hoa tiet). */
@RunWith(AndroidJUnit4::class)
class NeonImagesWidgetTest {

    private val ctx: Context = ApplicationProvider.getApplicationContext()

    private val neonIds = intArrayOf(
        R.drawable.neon_img_1, R.drawable.neon_img_2, R.drawable.neon_img_3,
        R.drawable.neon_img_4, R.drawable.neon_img_5, R.drawable.neon_img_6,
    )

    @Test
    fun neonVectorsRenderNonBlankSquareBitmap() {
        val size = 64
        for (id in neonIds) {
            val drawable = AppCompatResources.getDrawable(ctx, id)
            assertNotNull("drawable id=$id null", drawable)

            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable!!.setBounds(0, 0, size, size)
            drawable.draw(canvas)

            // Goc (0,0) la mau nen; phai co it nhat 1 pixel khac => vector da ve hoa tiet.
            val bg = bitmap.getPixel(0, 0)
            var hasPattern = false
            loop@ for (x in 0 until size step 2) {
                for (y in 0 until size step 2) {
                    if (bitmap.getPixel(x, y) != bg) {
                        hasPattern = true
                        break@loop
                    }
                }
            }
            assertTrue("neon img id=$id phai ve ra hoa tiet", hasPattern)
        }
    }
}
