# Task 31 — Custom Tile Shape

**Priority:** P2 — visual differentiation, nhưng cần thay đổi GameBoard render  
**Effort:** Medium

---

## Mô tả

Thêm option tile shape trong Settings: **Square** (hiện tại), **Rounded** (corner radius 12dp), **Soft** (corner radius 24dp). Shape persist qua `tile_shape` pref. GameBoard đọc và apply khi vẽ.

---

## S1 — Prefs

**`Prefs.kt`** — thêm key `tile_shape` (default `"square"`).

---

## S2 — Render rounded tile trong GameBoard

**`GameBoard.kt`** — trong `onDraw()`, thay `canvas.drawBitmap(bmp, null, tileRect, paint)` bằng:

```kotlin
private fun drawTile(canvas: Canvas, bmp: Bitmap, rect: RectF, cornerRadius: Float) {
    if (cornerRadius == 0f) {
        canvas.drawBitmap(bmp, null, rect, tilePaint)
        return
    }
    // Clip path rounded rect
    val path = Path().apply { addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW) }
    canvas.save()
    canvas.clipPath(path)
    canvas.drawBitmap(bmp, null, rect, tilePaint)
    canvas.restore()
    // Vẽ lại border neon lên trên (đảm bảo glow không bị clip)
    drawTileGlow(canvas, rect, cornerRadius)
}

private val cornerRadius: Float
    get() = when (Prefs.get(context).getString("tile_shape", "square")) {
        "rounded" -> resources.displayMetrics.density * 12
        "soft"    -> resources.displayMetrics.density * 24
        else      -> 0f
    }
```

---

## S3 — Settings UI

**`SettingsActivity.kt`** — section "Tile Shape":
```xml
<RadioGroup android:id="@+id/rgTileShape">
    <RadioButton android:text="Square" android:tag="square" />
    <RadioButton android:text="Rounded" android:tag="rounded" />
    <RadioButton android:text="Soft" android:tag="soft" />
</RadioGroup>
```

Preview mini: 3 thumbnail tile nhỏ với border neon theo shape.

Khi thay đổi → lưu pref + `boardView.invalidate()` ngay (nếu đang ở GameAct).

---

## S4 — Performance note

`canvas.clipPath()` không phải hardware accelerated trên mọi API. Fallback: nếu API < 28, dùng `PorterDuff.Mode.DST_IN` với rounded mask bitmap (pre-rendered 1 lần vào cache).

---

## Files ảnh hưởng

- `GameBoard.kt` — `drawTile()` với clip path, đọc `cornerRadius`
- `SettingsActivity.kt` — RadioGroup tile shape
- `Prefs.kt` — key `tile_shape`

## Acceptance

- [ ] Square: render như hiện tại (không regression)
- [ ] Rounded/Soft: góc bo đúng bán kính
- [ ] Glow/border vẫn hiển thị đúng trên tile bo góc
- [ ] Thay đổi trong Settings áp dụng ngay khi quay lại game (onResume reload)
- [ ] Không OOM (không cấp phát Path/Bitmap mới trong mỗi frame `onDraw`)
- [ ] API 23 fallback không crash
