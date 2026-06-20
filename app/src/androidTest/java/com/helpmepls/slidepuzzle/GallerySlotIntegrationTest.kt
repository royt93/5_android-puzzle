package com.helpmepls.slidepuzzle

import android.content.Context
import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.helpmepls.slidepuzzle.act.GameAct
import com.helpmepls.slidepuzzle.vm.BoardOptionsVm
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * Integration test: GameAct xử lý custom image path từ gallery (không cần ảnh thật).
 * Tạo file ảnh giả trong cache rồi truyền path vào GameAct.
 */
@RunWith(AndroidJUnit4::class)
class GallerySlotIntegrationTest {

    @Test
    fun gameActLaunchesWithCustomImagePath() {
        val ctx: Context = ApplicationProvider.getApplicationContext()

        // Tạo bitmap giả 64×64 và lưu vào cache.
        val bmp = android.graphics.Bitmap.createBitmap(64, 64, android.graphics.Bitmap.Config.ARGB_8888)
        android.graphics.Canvas(bmp).drawColor(android.graphics.Color.CYAN)
        val cacheFile = File(ctx.cacheDir, "test_gallery.jpg")
        cacheFile.outputStream().use { bmp.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, it) }
        bmp.recycle()

        val intent = Intent(ctx, GameAct::class.java).apply {
            putExtra(GameAct.EXTRA_IMAGE_RES_ID, BoardOptionsVm.GALLERY_SLOT_RES_ID)
            putExtra(GameAct.EXTRA_BOARD_WIDTH, 4)
            putExtra(GameAct.EXTRA_BOARD_HEIGHT, 4)
            putExtra(GameAct.EXTRA_CUSTOM_IMAGE_PATH, cacheFile.absolutePath)
        }

        ActivityScenario.launch<GameAct>(intent).use { scenario ->
            scenario.moveToState(Lifecycle.State.RESUMED)
            assertEquals("GameAct phải RESUME với custom image", Lifecycle.State.RESUMED, scenario.state)
            // Board phải hiển thị.
            onView(withId(R.id.boardView)).check(matches(isDisplayed()))
        }

        cacheFile.delete()
    }

    @Test
    fun gameActLaunchesGracefullyWithMissingCacheFile() {
        val ctx: Context = ApplicationProvider.getApplicationContext()

        val intent = Intent(ctx, GameAct::class.java).apply {
            putExtra(GameAct.EXTRA_IMAGE_RES_ID, BoardOptionsVm.PREDEFINED_IMAGES.first().first)
            putExtra(GameAct.EXTRA_BOARD_WIDTH, 4)
            putExtra(GameAct.EXTRA_BOARD_HEIGHT, 4)
            // custom path trỏ đến file không tồn tại → fallback về imageResId.
            putExtra(GameAct.EXTRA_CUSTOM_IMAGE_PATH, "/cache/nonexistent_file.jpg")
        }

        ActivityScenario.launch<GameAct>(intent).use { scenario ->
            scenario.moveToState(Lifecycle.State.RESUMED)
            assertEquals("GameAct phải RESUME dù custom path sai", Lifecycle.State.RESUMED, scenario.state)
            onView(withId(R.id.boardView)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun gallerySlotResIdIsZero() {
        // Đảm bảo GALLERY_SLOT_RES_ID không thay đổi (regression guard).
        assertEquals(0, BoardOptionsVm.GALLERY_SLOT_RES_ID)
    }

    @Test
    fun predefinedImagesContainsGallerySlot() {
        val images = BoardOptionsVm.PREDEFINED_IMAGES
        val hasGallerySlot = images.any { it.first == BoardOptionsVm.GALLERY_SLOT_RES_ID }
        assertTrue("PREDEFINED_IMAGES phải có gallery slot ở cuối", hasGallerySlot)
        // Gallery slot phải là phần tử cuối cùng.
        assertEquals("Gallery slot phải ở cuối danh sách", BoardOptionsVm.GALLERY_SLOT_RES_ID, images.last().first)
    }
}
