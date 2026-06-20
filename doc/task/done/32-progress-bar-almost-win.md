# Task 32 — Progress Glow khi sắp thắng

**Priority:** P0 — zero logic risk, visual only, tăng tension cực hiệu quả  
**Effort:** Small

---

## Mô tả

Khi ≥ 80% tile đã đúng vị trí, board pulse nhẹ (cyan glow tăng dần) và hiện banner **"Almost there!"**. Khi 100% → trigger win như thường.

---

## S1 — Tính phần trăm tile đúng

**`GameBoard.kt`**:
```kotlin
fun correctTilePercent(): Float {
    val total = state.tiles.size - 1  // trừ blank
    val correct = state.tiles.indices.count { i ->
        state.tiles[i] != 0 && state.tiles[i] == i + 1
    }
    return correct.toFloat() / total
}
```

Gọi trong `GameAct.kt` sau mỗi move (đã có listener move).

---

## S2 — Pulse board + banner

**`GameAct.kt`**:
```kotlin
private fun onMoveResult(percent: Float) {
    when {
        percent >= 1.0f -> { /* win — handled elsewhere */ }
        percent >= 0.8f -> {
            tvAlmostThere.visibility = View.VISIBLE
            boardView.setGlowIntensity(GlowLevel.HIGH)  // amplify halo
        }
        else -> {
            tvAlmostThere.visibility = View.GONE
            boardView.setGlowIntensity(GlowLevel.NORMAL)
        }
    }
}
```

**`GameBoard.kt`** — `setGlowIntensity()`: điều chỉnh `haloAlphaMultiplier` (1.0f bình thường, 2.0f khi HIGH). `invalidate()` để redraw halo sáng hơn.

---

## S3 — Banner layout

**`act_game.xml`**:
```xml
<TextView
    android:id="@+id/tvAlmostThere"
    android:text="Almost there! ✨"
    android:textColor="@color/neon_cyan"
    android:shadowColor="@color/neon_cyan_glow"
    android:shadowRadius="12"
    android:visibility="gone"
    android:layout_gravity="top|center_horizontal"
    ... />
```

Fade-in 300ms khi hiện.

---

## Files ảnh hưởng

- `GameBoard.kt` — `correctTilePercent()`, `setGlowIntensity()`
- `GameAct.kt` — gọi sau mỗi move, điều khiển banner
- `act_game.xml` — thêm `tvAlmostThere`

## Acceptance

- [ ] ≥ 80% tile đúng → banner "Almost there!" hiện, board glow sáng hơn
- [ ] < 80% → banner ẩn, glow về normal
- [ ] Hoàn thành → banner ẩn, win dialog hiện (không hiện chồng nhau)
- [ ] Không ảnh hưởng FPS (chỉ thay alpha multiplier, không alloc mới)
