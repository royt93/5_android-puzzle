package com.helpmepls.slidepuzzle.vm

import com.helpmepls.slidepuzzle.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Unit test (JVM): danh sach anh co du 6 vector neon va dung tong so. */
class NeonImagesTest {

    @Test
    fun predefinedImagesIncludeSixNeonVectors() {
        val neon = BoardOptionsVm.PREDEFINED_IMAGES.filter { it.second == "Neon" }
        assertEquals(6, neon.size)

        val ids = neon.map { it.first }.toSet()
        listOf(
            R.drawable.neon_img_1, R.drawable.neon_img_2, R.drawable.neon_img_3,
            R.drawable.neon_img_4, R.drawable.neon_img_5, R.drawable.neon_img_6,
        ).forEach { assertTrue("thieu neon id=$it", ids.contains(it)) }
    }

    @Test
    fun totalImageCountIsPastelPlusNeon() {
        // 27 pastel (i/a/b 1..9) + 6 neon
        assertEquals(33, BoardOptionsVm.PREDEFINED_IMAGES.size)
    }
}
