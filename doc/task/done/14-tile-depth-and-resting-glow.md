# Task 14 — Tile Depth & Resting Glow

**Priority:** P0 — nhìn trực tiếp vào game board, đây là vấn đề nổi bật nhất

## Vấn đề hiện tại

- `tileSpacing = 3` (3px cứng, không phải dp) → khoảng cách giữa tile quá mỏng, không rõ ranh giới
- Chỉ tile ĐANG trượt mới có glow border — tất cả tile còn lại trông phẳng như ảnh cắt đơn thuần
- Không có background tối giữa các tile → khi nhìn, board trông như 1 ảnh duy nhất bị cắt vụng

## Mục tiêu

Mỗi tile luôn có:
1. Khoảng cách 6dp giữa các tile (thay 3px)
2. Viền glow thường trực (alpha thấp ~0x33) ở trạng thái nghỉ
3. Viền glow sáng (alpha 0xCC) khi đang trượt (giữ logic cũ, tăng intensity)
4. Background `neon_bg_deep` lộ ra ở khoảng trống giữa tile → cảm giác "panel neon grid"

## Thay đổi cần làm

### `GameBoard.kt`
```
// Line 42 hiện tại:
private val tileSpacing = 3

// Đổi thành (6dp → pixel):
private val tileSpacingDp = 6
private val tileSpacingPx: Int get() = (tileSpacingDp * resources.displayMetrics.density + 0.5f).toInt()
```
→ Thay mọi chỗ dùng `tileSpacing` bằng `tileSpacingPx`.

Trong `onDraw()`, **trước** vòng for vẽ tile, fill toàn board bằng `neon_bg_deep`:
```kotlin
paint.style = Paint.Style.FILL
paint.color = 0xFF0E1230.toInt()
canvas.drawRoundRect(boardRectF, 16.0f, 16.0f, paint)
```

Sau khi `canvas.drawBitmap(...)` cho mỗi tile, thêm glow resting:
```kotlin
// Resting glow (tất cả tile, không chỉ active)
tmpRectF.set(renderOffset)
glowPaint.style = Paint.Style.STROKE
glowPaint.strokeWidth = 3.0f
glowPaint.color = (accentColor and 0x00FFFFFF) or 0x33000000
canvas.drawRoundRect(tmpRectF, 8.0f, 8.0f, glowPaint)
```

Active glow: giữ `drawGlowRoundRect(canvas, tmpRectF, 8.0f, highlightColor, 4.0f)` nhưng tăng `coreWidth` lên `6.0f` và tăng alpha lớp cuối lên `0xFF`.

### `GameBoard.kt` — `drawGlowRoundRect`
Tăng alpha layer cuối từ `0xE0` → `0xFF`.

## File ảnh hưởng
- `app/src/main/java/com/helpmepls/slidepuzzle/game/GameBoard.kt`

## Acceptance
- [ ] Board trông như "grid panel neon" — mỗi tile có viền phát sáng nhẹ
- [ ] Khi trượt tile, viền sáng rõ rệt hơn resting state
- [ ] Không có regression animation (test shuffle, undo, reset)
