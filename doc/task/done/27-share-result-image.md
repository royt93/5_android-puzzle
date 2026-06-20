# Task 27 — Share Result as Image

**Priority:** P0 — organic marketing, không cần server, high ROI  
**Effort:** Medium

---

## Mô tả

Khi thắng, nút **Share** trong win dialog tạo ảnh kết quả (Canvas) rồi share qua Intent. Ảnh gồm: thumbnail puzzle hoàn chỉnh + stats (moves, time) + neon frame + app logo.

---

## S1 — Vẽ share card bằng Canvas

**`ShareUtils.kt`** (file mới):
```kotlin
object ShareUtils {
    fun createShareBitmap(ctx: Context, puzzleBitmap: Bitmap, moves: Int, timeSeconds: Int): Bitmap {
        val W = 800; val H = 900
        val bmp = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)

        // Nền deep navy
        c.drawColor(0xFF0E1230.toInt())

        // Puzzle thumbnail — center top (640×640)
        val thumbRect = RectF(80f, 60f, 720f, 700f)
        c.drawBitmap(puzzleBitmap, null, thumbRect, null)

        // Neon border quanh thumbnail
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 4f
            color = 0xFF38F9E4.toInt()  // neon_cyan
            setShadowLayer(16f, 0f, 0f, 0x8038F9E4.toInt())
        }
        c.drawRoundRect(thumbRect, 16f, 16f, borderPaint)

        // Stats text
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFEAF2FF.toInt()
            textSize = 48f
            typeface = Typeface.DEFAULT_BOLD
            setShadowLayer(8f, 0f, 0f, 0x8038F9E4.toInt())
        }
        c.drawText("$moves moves  •  ${formatTime(timeSeconds)}", 80f, 780f, textPaint)

        // App tag
        val tagPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF9FB0D9.toInt()
            textSize = 32f
        }
        c.drawText("Neon Puzzle", 80f, 850f, tagPaint)

        return bmp
    }

    fun share(ctx: Context, bmp: Bitmap) {
        val file = File(ctx.cacheDir, "puzzle_result.png")
        file.outputStream().use { bmp.compress(Bitmap.CompressFormat.PNG, 90, it) }
        val uri = FileProvider.getUriForFile(ctx, "${ctx.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "I solved a Neon Puzzle! 🧩✨")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        ctx.startActivity(Intent.createChooser(intent, "Share result"))
    }

    private fun formatTime(s: Int) = "%d:%02d".format(s / 60, s % 60)
}
```

---

## S2 — Gọi từ win dialog

**`DialogUtils.kt`** — wiring nút Share (đã có trong dialog_win.xml):
```kotlin
btnShare.setOnClickListener {
    val puzzleBmp = boardView.getCompletedBitmap()  // snapshot board hiện tại
    val shareBmp = ShareUtils.createShareBitmap(ctx, puzzleBmp, moves, timeSeconds)
    ShareUtils.share(ctx, shareBmp)
}
```

**`GameBoard.kt`** — thêm:
```kotlin
fun getCompletedBitmap(): Bitmap {
    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    draw(Canvas(bmp))
    return bmp
}
```

---

## S3 — FileProvider

**`AndroidManifest.xml`**:
```xml
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_provider_paths" />
</provider>
```

**`res/xml/file_provider_paths.xml`** (tạo mới):
```xml
<paths>
    <cache-path name="shared_images" path="." />
</paths>
```

---

## Files ảnh hưởng

- `util/ShareUtils.kt` — tạo mới
- `GameBoard.kt` — `getCompletedBitmap()`
- `DialogUtils.kt` — wiring btnShare
- `AndroidManifest.xml` — FileProvider
- `res/xml/file_provider_paths.xml` — tạo mới

## Acceptance

- [ ] Tap Share trong win dialog → system share sheet mở
- [ ] Ảnh share chứa: puzzle thumbnail + moves + time + app tag
- [ ] Không crash khi share qua Zalo/Facebook/Instagram
- [ ] Cache file ghi đúng, không tích lũy nhiều file
- [ ] Không OOM với ảnh lớn (inSampleSize khi tạo thumbnail)
